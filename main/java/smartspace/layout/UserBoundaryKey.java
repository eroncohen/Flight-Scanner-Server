package smartspace.layout;

public class UserBoundaryKey {

	private String smartspace;
	private String email;

	public UserBoundaryKey() {

	}

	public UserBoundaryKey(String smartspace, String email) {
		this.smartspace = smartspace;
		this.email = email;
	}

	public String getSmartspace() {
		return smartspace;
	}

	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return smartspace + "#" + email;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UserBoundaryKey))
			return false;
		UserBoundaryKey that = (UserBoundaryKey) o;
		return getSmartspace().equals(that.getSmartspace()) && getEmail().equals(that.getEmail());
	}

}
