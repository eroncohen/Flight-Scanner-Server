package smartspace.logic;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartspace.aop.UserValidator;
import smartspace.dao.AdvancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;

@Service
public class UserServiceImpl implements UserService {

	private AdvancedUserDao<UserKey> users;
	private String smartspace;

	@Value("${smartspace.name:smartspace.user}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Autowired
	public void setUsers(AdvancedUserDao<UserKey> users) {
		this.users = users;
	}

	@Transactional
	@Override
	public UserEntity writeUser(UserEntity userEntity) {
		if (validate(userEntity))
			return this.users.create(userEntity);
		throw new RuntimeException("Invalid user input!");
	}

	@UserValidator
	@Transactional
	@Override
	public List<UserEntity> importUsers(List<UserEntity> userEntities, String adminSmartspace, String adminEmail) {
		userEntities.stream().forEach(user -> {
			if (validatKey(user.getKey()))
				user = writeUser(user);
			else
				throw new RuntimeException("invalid user key");

		});
		return userEntities;
	}

	@UserValidator
	@Override
	public List<UserEntity> exportUsers(int size, int page, String adminSmartspace, String adminEmail) {
		return this.users.readAll("key", size, page);
	}

	@Override
	public void update(UserEntity entity, String userSmartspace, String userEmail) {
		// Optional<UserEntity> oldUser=users.readById(entity.getKey());
		this.users.update(entity);

	}

	@Transactional
	@Override
	public UserEntity createUser(UserEntity userEntity) {
		userEntity.setUserSmartspace(this.smartspace);
		if (userEntity.getKey() == null)
			return writeUser(userEntity);
		throw new RuntimeException("Invalid user key!");
	}

	@Override
	public UserEntity getUser(String userSmartspace, String userEmail) {
		UserKey userKey = new UserKey(userSmartspace, userEmail);
		return this.users.readById(userKey).orElseThrow(() -> new RuntimeException("No user with the key" + userKey));
	}

	private boolean validatKey(UserKey userKey) {

		return userKey.getUserSmartspace() != null && !userKey.getUserSmartspace().trim().isEmpty()
				&& !userKey.getUserSmartspace().equals(this.smartspace) && userKey.getUserEmail() != null
				&& !userKey.getUserEmail().trim().isEmpty();
	}

	private boolean validate(UserEntity userEntity) {
		Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
				Pattern.CASE_INSENSITIVE);
		return
		// UserName
		userEntity.getUsername() != null && !userEntity.getUsername().trim().isEmpty()
		// Avatar
				&& userEntity.getAvatar() != null && !userEntity.getAvatar().trim().isEmpty()
				// Role
				&& userEntity.getRole() != null
				// Points
				&& userEntity.getPoints() >= 0
				// email
				&& VALID_EMAIL_ADDRESS_REGEX.matcher(userEntity.getUserEmail().trim()).matches();

	}

}
