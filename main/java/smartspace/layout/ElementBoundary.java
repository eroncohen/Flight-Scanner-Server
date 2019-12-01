package smartspace.layout;

import java.util.Date;
import java.util.Map;

import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Location;

public class ElementBoundary {
	
	private ActionAndElementBoundaryKey key;
	private String elementType;
	private String name;
	private boolean expired;
	private Date created;
	private UserBoundaryKey creator;
	private LatLng latlng;
	private Map<String, Object> elementProperties;

	public ElementBoundary() {
		this.key = null;
		this.creator = null;
		this.latlng = null;
	}

	public ElementBoundary(ElementEntity elementEntity) {
		this();
		this.key = new ActionAndElementBoundaryKey(elementEntity.getElementSmartspace(),elementEntity.getElementId());

		this.elementType = elementEntity.getType();

		this.name = elementEntity.getName();

		this.expired = elementEntity.isExpired();

		this.created = elementEntity.getCreationTimeStamp();

		this.latlng = new LatLng(elementEntity.getLocation().getY(), elementEntity.getLocation().getX());

		this.creator = new UserBoundaryKey(elementEntity.getCreatorSmartspace(), elementEntity.getCreatorEmail());

		this.elementProperties = elementEntity.getMoreAttributes();
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public ActionAndElementBoundaryKey getKey() {
		return key;
	}

	public void setKey(ActionAndElementBoundaryKey key) {
		this.key = key;
	}

	public UserBoundaryKey getCreator() {
		return creator;
	}

	public void setCreator(UserBoundaryKey creator) {
		this.creator = creator;
	}

	public LatLng getLatlng() {
		return latlng;
	}

	public void setLatlng(LatLng latlng) {
		this.latlng = latlng;
	}

	public Map<String, Object> getElementProperties() {
		return elementProperties;
	}

	public void setElementProperties(Map<String, Object> elementProperties) {
		this.elementProperties = elementProperties;
	}

	public ElementEntity convertToEntity() {
		ElementEntity entity = new ElementEntity();

		if (this.key != null) {
			entity.setElementId(this.key.getId());
			entity.setElementSmartspace(this.key.getSmartspace());
			entity.setKey(new ElementKey(this.key.getSmartspace(), this.key.getId()));
		} else {
			entity.setElementId(null);
			entity.setElementSmartspace(null);
			entity.setKey(null);
		}
		entity.setName(this.name);
		entity.setExpired(this.expired);
		entity.setCreationTimeStamp(this.created);

		if (this.latlng != null)
			entity.setLocation(new Location(this.latlng.getLng(), this.latlng.getLat()));
		else
			entity.setLocation(null);

		entity.setType(this.elementType);

		if (this.creator != null) {
			entity.setCreatorEmail(this.creator.getEmail());
			entity.setCreatorSmartspace(this.creator.getSmartspace());
		} else {
			entity.setCreatorEmail(null);
			entity.setCreatorSmartspace(null);
		}

		entity.setMoreAttributes(this.elementProperties);
		return entity;

	}

	@Override
	public String toString() {
		return "ElementBoundary [key=" + key + ", elementType=" + elementType + ", name=" + name + ", expired="
				+ expired + ", created=" + created + ", creator=" + creator + ", latlng=" + latlng
				+ ", elementProperties=" + elementProperties + "]";
	}
}
