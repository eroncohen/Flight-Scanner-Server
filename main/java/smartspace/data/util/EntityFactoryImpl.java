package smartspace.data.util;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;


@Component
public class EntityFactoryImpl implements EntityFactory {

	@Override
	public UserEntity createNewUser( String userSmartspace,String userEmail, String username, String avatar,
			UserRole role, long point) {
		return new UserEntity(userSmartspace, userEmail, username, avatar, role, point);
	}

	@Override
	public ElementEntity createNewElement(String name, String type, Location location, Date creationTimeStamp,
			String creatorEmail, String creatorSmartspace, boolean expired, Map<String, Object> moreAttributes) {
		return new ElementEntity(location, name, type, creationTimeStamp, expired, creatorSmartspace, creatorEmail,
				moreAttributes);

	}

	@Override
	public ActionEntity createNewAction(String elementId, String elementSmartspace, String actionType,
			Date creationTimestamp, String playerEmail, String playerSmartspace, Map<String, Object> moreAttributes) {
		return new ActionEntity(elementSmartspace, elementId, playerSmartspace, playerEmail, actionType,
				creationTimestamp, moreAttributes);

	}

}
