package smartspace.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ActionKey implements Serializable,Comparable<ActionKey>{

	private static final long serialVersionUID = 3L;
	private String actionSmartspace;
	private String actionId;

	public ActionKey() {
	}

	public ActionKey(String actionSmartspace, String actionId) {

		this.actionSmartspace = actionSmartspace;
		this.actionId = actionId;
	}

	@Column(name = "actionSmartpace")
	public String getActionSmartspace() {
		return actionSmartspace;
	}

	public void setActionSmartspace(String actionSmartspace) {
		this.actionSmartspace = actionSmartspace;
	}

	@Column(name = "actionId")
	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	@Override
	public String toString() {
		return actionSmartspace + "@" + actionId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ActionKey))
			return false;
		ActionKey that = (ActionKey) o;
		return Objects.equals(getActionSmartspace(), that.getActionSmartspace())
				&& Objects.equals(getActionId(), that.getActionId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getActionSmartspace(), getActionId());
	}

	@Override
	public int compareTo(ActionKey o) {	
		return this.toString().compareTo(o.toString());
	}
}
