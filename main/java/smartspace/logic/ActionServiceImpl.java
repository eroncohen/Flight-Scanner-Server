package smartspace.logic;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartspace.aop.UserValidator;
import smartspace.dao.AdvancedActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.ActionKey;
import smartspace.data.UserRole;
import smartspace.plugin.SmartspacePlugin;

@Service
public class ActionServiceImpl implements ActionService {

	private AdvancedActionDao actions;
	private String smartspace;
	private ApplicationContext ctx;

	@Autowired
	public ActionServiceImpl(AdvancedActionDao actionDao, ApplicationContext ctx) {
		this.actions = actionDao;
		this.ctx = ctx;
	}

	@Value("${smartspace.name:smartspace.user}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Transactional
	@Override
	public ActionEntity writeAction(ActionEntity actionEntity) {
		if (validate(actionEntity)) {
			return this.actions.create(actionEntity);
		} else {
			System.err.println(actionEntity);
			throw new RuntimeException("invalid action input");
		}
	}

	@UserValidator
	@Transactional
	@Override
	public List<ActionEntity> importActions(List<ActionEntity> actionsEntities, String adminSmartspace,
			String adminEmail) {

		actionsEntities.stream().forEach(action -> {
			if (validatKey(action.getKey()))
				action = writeAction(action);
			else
				throw new RuntimeException("invalid action key");

		});
		return actionsEntities;
	}

	@UserValidator
	@Override
	public List<ActionEntity> exportActions(int size, int page, String adminSmartspace, String adminEmail) {
		return this.actions.readAll("creationTimeStamp", size, page);
	}

	private boolean validate(ActionEntity actionEntity) {
		boolean result = true;
		result = result && actionEntity.getType() != null;
		result = result && actionEntity.getPlayerEmail() != null;
		result = result && actionEntity.getPlayerSmartspace() != null;
		return result;
	}

	private boolean validatKey(ActionKey actionKey) {
		return actionKey.getActionSmartspace() != null && !actionKey.getActionSmartspace().trim().isEmpty()
				&& !actionKey.getActionSmartspace().equals(this.smartspace) && actionKey.getActionId() != null
				&& !actionKey.getActionId().trim().isEmpty();
	}


	@Override
	@Transactional
	@UserValidator
	public Object handleAction(ActionEntity action, String userSmartspace,String userEmail,UserRole userRole) {
		
		if (userRole != UserRole.PLAYER)
			throw new RuntimeException("invalid UserRole!");
		
		if (action.getType()!= null) {
			try {
				String command = action.getType();
				
				String className = "smartspace.plugin." + command.toUpperCase().charAt(0)
						+ command.substring(1, command.length()) + "Plugin";
				
				Class<?> theClass = Class.forName(className);
				SmartspacePlugin plugin = (SmartspacePlugin) ctx.getBean(theClass);
				
				action.setCreationTimeStamp(new Date());
				Object res = plugin.process(action);
				
				if(!userEmail.equals("myTimer@Timer"))
					action = this.writeAction(action);			
				
				if(res == null) {
					return action;	
				}
				else {
					return res;
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else 
			throw new RuntimeException("invalid action input");
	}

}
