package smartspace.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import smartspace.dao.rdb.MapToJsonConverter;

@Entity
@Table(name="ELEMENTS")
public class ElementEntity implements SmartspaceEntity<ElementKey> {

	private ElementKey key;
	private String elementSmartspace;
	private String elementId;
	private Location location;
	private String name;
	private String type;
	private Date creationTimeStamp;
	private boolean expired;
	private String creatorSmartspace;
	private String creatorEmail;
	private Map<String, Object> moreAttributes;

	public ElementEntity() {
		this.key = null;
	}
	
	public ElementEntity(Location location, String name, String type,
			Date creationTimeStamp, boolean expired, String creatorSmartspace, String creatorEmail,
			Map<String, Object> moreAttributes) {
		this();
		this.location = location;
		this.name = name;
		this.type = type;
		this.creationTimeStamp = creationTimeStamp;
		this.expired = expired;
		this.creatorSmartspace = creatorSmartspace;
		this.creatorEmail = creatorEmail;
		this.moreAttributes = moreAttributes;
	}
	
	//copy Constructor
	public ElementEntity(ElementEntity other) {
		this.key = other.key;
		this.elementSmartspace = other.getElementSmartspace();
		this.elementId = other.getElementId();
		this.location= other.getLocation();
		this.name= other.getName();
		this.type= other.getType();
		this.creationTimeStamp= other.getCreationTimeStamp();
		this.expired= other.isExpired();
		this.creatorSmartspace= other.getCreatorSmartspace();
		this.creatorEmail= other.getCreatorEmail();
		this.moreAttributes= new HashMap<String, Object>(other.getMoreAttributes());
	}
	
	@EmbeddedId
	@Override
	@Column(name="theKey")
	public ElementKey getKey() {
		return this.key;
	}

	@Override
	public void setKey(ElementKey key) {
		this.key = key;
	}
	
	@Transient
	public String getElementSmartspace() {
		return elementSmartspace;
	}
	
	public void setElementSmartspace(String elementSmartspace) {
		this.elementSmartspace = elementSmartspace;
	}
	
	@Transient
	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	@Embedded
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	//@Transient
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationTimeStamp() {
		return creationTimeStamp;
	}

	public void setCreationTimeStamp(Date creationTimeStamp) {
		this.creationTimeStamp = creationTimeStamp;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public String getCreatorSmartspace() {
		return creatorSmartspace;
	}

	public void setCreatorSmartspace(String creatorSmartspace) {
		this.creatorSmartspace = creatorSmartspace;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	@Lob
	@Convert(converter=MapToJsonConverter.class)
	public Map<String, Object> getMoreAttributes() {
		return moreAttributes;
	}

	public void setMoreAttributes(Map<String, Object> moreAttributes) {
		this.moreAttributes = moreAttributes;
	}

	@Override
	public String toString() {
		return "ElementEntity -> [key=" + key + ",elementSmartspace =" + elementSmartspace + ", elementId=" + elementId + ", location="
				+ location + ", name=" + name + ", type=" + type +
				", creationTimeStamp= " + creationTimeStamp + ",expired =" + expired +
				",creatorSmartspace =" + creatorSmartspace + ",creatorEmail =" + creatorEmail + ",moreAttributes =" + moreAttributes +
				 "]";
	}
}
