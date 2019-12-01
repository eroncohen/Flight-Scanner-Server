package smartspace.data;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import smartspace.dao.rdb.MapToJsonConverter;

@Entity
@Table(name = "ACTIONS")
public class ActionEntity implements SmartspaceEntity<ActionKey> {
	private ActionKey key;
	private String actionSmartspace;
	private String actionId;
	private String elementSmartspace;
	private String elementId;
	private String playerSmartspace;
	private String playerEmail;
	private String type;
	private Date creationTimeStamp;
	private Map<String, Object> moreAttributes;

	public ActionEntity() {

	}

	public ActionEntity(String actionSmartspace, String actionId, String elementSmartspace, String elementId,
			String playerSmartspace, String playerEmail, String type, Date creationTimeStamp,
			Map<String, Object> moreAttributes) {
		super();
		this.actionSmartspace = actionSmartspace;
		this.actionId = actionId;
		this.elementSmartspace = elementSmartspace;
		this.elementId = elementId;
		this.playerSmartspace = playerSmartspace;
		this.playerEmail = playerEmail;
		this.type = type;
		this.creationTimeStamp = creationTimeStamp;
		this.moreAttributes = moreAttributes;
	}

	public ActionEntity(String elementSmartspace, String elementId, String playerSmartspace, String playerEmail,
			String type, Date creationTimeStamp, Map<String, Object> moreAttributes) {
		super();
		this.elementSmartspace = elementSmartspace;
		this.elementId = elementId;
		this.playerSmartspace = playerSmartspace;
		this.playerEmail = playerEmail;
		this.type = type;
		this.creationTimeStamp = creationTimeStamp;
		this.moreAttributes = moreAttributes;
	}

	@EmbeddedId
	@Override
	@Column(name="theKey")
	public ActionKey getKey() {
		return this.key;
	}

	@Override
	public void setKey(ActionKey key) {
		this.key = key;

	}

	@Transient
	public String getActionSmartspace() {
		return actionSmartspace;
	}

	public void setActionSmartspace(String actionSmartspace) {
		this.actionSmartspace = actionSmartspace;
	}

	@Transient
	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getElementSmartspace() {
		return elementSmartspace;
	}

	public void setElementSmartspace(String elementSmartspace) {
		this.elementSmartspace = elementSmartspace;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getPlayerSmartspace() {
		return playerSmartspace;
	}

	public void setPlayerSmartspace(String playerSmartspace) {
		this.playerSmartspace = playerSmartspace;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
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

	@Lob
	@Convert(converter = MapToJsonConverter.class)
	public Map<String, Object> getMoreAttributes() {
		return moreAttributes;
	}

	public void setMoreAttributes(Map<String, Object> moreAttributes) {
		this.moreAttributes = moreAttributes;
	}

	@Override
	public String toString() {
		return "ActionEntity [key=" + key + ", actionSmartspace=" + actionSmartspace + ", actionId=" + actionId
				+ ", elementSmartspace=" + elementSmartspace + ", elementId=" + elementId + ", playerSmartspace="
				+ playerSmartspace + ", playerEmail=" + playerEmail + ", type=" + type + ", creationTimeStamp="
				+ creationTimeStamp + ", moreAttributes=" + moreAttributes + "]";
	}
}
