package smartspace.data;

import java.io.Serializable;
import java.util.Objects;

public class UserKey implements Serializable,Comparable<UserKey>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userSmartspace;
	private String userEmail;

	public UserKey() {

	}

	public UserKey(String userSmartspace, String userEmail) {
		super();
		this.userSmartspace = userSmartspace;
		this.userEmail = userEmail;
	}

	public String getUserSmartspace() {
		return userSmartspace;
	}

	public void setUserSmartspace(String userSmartspace) {
		this.userSmartspace = userSmartspace;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	@Override
	public String toString() {
		return userSmartspace + "#" + userEmail;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UserKey))
			return false;
		UserKey that = (UserKey) o;
		return getUserSmartspace().equals(that.getUserSmartspace())
				&& getUserEmail().equals(that.getUserEmail());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUserSmartspace(), getUserEmail());
	}

	@Override
	public int compareTo(UserKey o) {
		return this.toString().compareTo(o.toString());
	}
}
