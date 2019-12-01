package smartspace.layout;

public class ActionAndElementBoundaryKey {
	private String id;
	private String smartspace;

	public ActionAndElementBoundaryKey() {

	}

	public ActionAndElementBoundaryKey(String smartspace, String id) {
		this.smartspace = smartspace;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSmartspace() {
		return smartspace;
	}

	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Override
	public String toString() {
		return smartspace + "@" + id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ActionAndElementBoundaryKey))
			return false;
		ActionAndElementBoundaryKey that = (ActionAndElementBoundaryKey) o;
		return getSmartspace().equals(that.getSmartspace())
				&& (Integer.parseInt(getId()) == Integer.parseInt(that.getId()));
	}

}
