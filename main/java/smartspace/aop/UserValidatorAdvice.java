package smartspace.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import smartspace.dao.AdvancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;

@Component
@Aspect
public class UserValidatorAdvice {

	Log log = LogFactory.getLog(UserValidatorAdvice.class);

	private AdvancedUserDao<UserKey> users;
	private String smartspace;

	@Autowired
	public void setUsers(AdvancedUserDao<UserKey> users) {
		this.users = users;
	}

	@Value("${smartspace.name:smartspace.user}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}
	
	@Before("@annotation(smartspace.aop.UserValidator) && args(..,adminSmartspace,adminEmail) && execution(* import*(..))")
	public void validateAdmin(JoinPoint jp, String adminSmartspace, String adminEmail) throws Throwable {

		String methodName = jp.getSignature().getName();
		String className = jp.getTarget().getClass().getName();

		UserKey userKey = new UserKey(adminSmartspace, adminEmail);
		UserEntity user = getUser(userKey);
		
		if (user.getRole() != UserRole.ADMIN|| !user.getUserSmartspace().equals(this.smartspace))
			throw new RuntimeException("User is not authorized!");
		else
			log.trace("***********" + userKey + " is authorized. Used:" + className + "." + methodName);
	}
	
	@Before("@annotation(smartspace.aop.UserValidator)  && args(..,managerSmartspace,managerEmail) && "
			+ "execution(* createElement(..)) OR execution(* updateElement(..))")
	public void validateManager(JoinPoint jp, String managerSmartspace, String managerEmail) throws Throwable {

		String methodName = jp.getSignature().getName();
		String className = jp.getTarget().getClass().getName();

		UserKey userKey = new UserKey(managerSmartspace, managerEmail);
		UserEntity user = getUser(userKey);
		
		if (user.getRole() != UserRole.MANAGER)
			throw new RuntimeException("User is not authorized!");
		else
			log.trace("***********" + userKey + " is authorized. Used:" + className + "." + methodName);
	}

	@Around("@annotation(smartspace.aop.UserValidator) && args(..,userSmartspace,userEmail,userRole)")
	public Object validateUser(ProceedingJoinPoint pjp, String userSmartspace, String userEmail,UserRole userRole) throws Throwable{
		
		Object args[] = pjp.getArgs();
		args[args.length-1] = getUser(new UserKey(userSmartspace, userEmail)).getRole();

		try {
			Object rv = pjp.proceed(args);
			return rv;
		} catch (Throwable e) {
			throw e;
		}
	}

	private UserEntity getUser(UserKey userKey) {
		return this.users.readById(userKey)
				.orElseThrow(() -> new RuntimeException("No user with the key" + userKey));
	}
}
