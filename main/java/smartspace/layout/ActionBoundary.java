package smartspace.layout;

import java.util.Date;
import java.util.Map;

import smartspace.data.ActionEntity;
import smartspace.data.ActionKey;

public class ActionBoundary {
	private ActionAndElementBoundaryKey actionKey;
	private String type;
	private Date created;
	private ActionAndElementBoundaryKey element;
	private UserBoundaryKey player;
	private Map<String, Object> properties;

	public ActionBoundary() {
		this.actionKey = null;
		this.element = null;
		this.player = null;
	}

	public ActionBoundary(ActionEntity action) {
		
		this.actionKey = new ActionAndElementBoundaryKey();
		this.element = new ActionAndElementBoundaryKey();
		this.player = new UserBoundaryKey();
		
		this.actionKey.setId(action.getActionId());
		this.actionKey.setSmartspace(action.getActionSmartspace());

		this.element.setId(action.getElementId());
		this.element.setSmartspace(action.getElementSmartspace());

		this.player.setSmartspace(action.getPlayerSmartspace());
		this.player.setEmail(action.getPlayerEmail());

		this.type = action.getType();
		this.created = action.getCreationTimeStamp();
		this.properties = action.getMoreAttributes();
	}


	
	public UserBoundaryKey getPlayer() {
		return player;
	}

	public void setPlayer(UserBoundaryKey player) {
		this.player = player;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public ActionEntity convertToEntity() {

		ActionEntity actionEntity = new ActionEntity();

		if (this.actionKey == null || this.actionKey.getId() == null) {
			actionEntity.setActionId(null);
			actionEntity.setActionSmartspace(null);
			actionEntity.setKey(null);
		} else {
			
			actionEntity.setActionId(this.actionKey.getId());
			actionEntity.setActionSmartspace(this.actionKey.getSmartspace());
			actionEntity.setKey(new ActionKey(this.actionKey.getSmartspace(), this.actionKey.getId()));
		}
		
		actionEntity.setType(this.type);
		actionEntity.setCreationTimeStamp(this.created);
		
		if(this.element!= null) {
			actionEntity.setElementId(this.element.getId());
			actionEntity.setElementSmartspace(this.element.getSmartspace());
		} else {
			actionEntity.setElementId(null);
			actionEntity.setElementSmartspace(null);
		}
			
		actionEntity.setMoreAttributes(this.properties);
		actionEntity.setPlayerEmail(this.player.getEmail());
		actionEntity.setPlayerSmartspace(this.player.getSmartspace());

		return actionEntity;
	}

	public ActionAndElementBoundaryKey getActionKey() {
		return actionKey;
	}

	public void setActionKey(ActionAndElementBoundaryKey actionKey) {
		this.actionKey = actionKey;
	}

	public ActionAndElementBoundaryKey getElement() {
		return element;
	}

	public void setElement(ActionAndElementBoundaryKey element) {
		this.element = element;
	}
}
