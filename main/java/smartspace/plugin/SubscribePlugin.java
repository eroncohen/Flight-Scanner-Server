package smartspace.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import smartspace.dao.AdvancedElementDao;
import smartspace.dao.AdvancedUserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;

@Component
public class SubscribePlugin implements SmartspacePlugin {

	private AdvancedElementDao<ElementKey> elementDao;
	private AdvancedUserDao<UserKey> userDao;

	@Autowired
	public void setActionDao(AdvancedUserDao<UserKey> userDao, AdvancedElementDao<ElementKey> elementDao) {
		this.elementDao = elementDao;
		this.userDao = userDao;
	}

	@Override
	public ActionEntity process(ActionEntity action) {
		ElementEntity route = this.elementDao
				.readById(new ElementKey(action.getElementSmartspace(), action.getElementId())).get();
		if (validateAction(action) && validateElement(route)) {
			throw new RuntimeException();
		}
		
		UserEntity user = this.userDao.readById(new UserKey(action.getPlayerSmartspace(), action.getPlayerEmail()))
				.orElseThrow(() -> new RuntimeException(
						"No user with the key" + action.getPlayerSmartspace() + "#" + action.getPlayerEmail()));
		user.setPoints(user.getPoints() + 1);
		this.userDao.update(user);
		return null;
	}

	private boolean validateElement(ElementEntity route) {
		return route == null || route.isExpired();
	}

	private boolean validateAction(ActionEntity action) {

		return userDao.readById(new UserKey(action.getPlayerSmartspace(), action.getPlayerEmail())).isPresent();
	}

}
