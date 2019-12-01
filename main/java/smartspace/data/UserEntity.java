package smartspace.data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "USERS")
public class UserEntity implements SmartspaceEntity<UserKey> {
	private UserKey userKey;
	private String userSmartspace;
	private String userEmail;
	private String username;
	private String avatar;
	private UserRole role;
	private long points;

	public UserEntity() {
	}

	public UserEntity(String userEmail, String userSmartspace, String username, String avatar, UserRole role,
			long points) {
		super();
		this.userSmartspace = userSmartspace;
		this.userEmail = userEmail;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
		this.points = points;
	}

	@EmbeddedId
	@Override
	@Column(name="theKey")
	public UserKey getKey() {
		return this.userKey;
	}

	@Override
	public void setKey(UserKey key) {
		this.userKey = key;
	}

	@Transient
	public String getUserSmartspace() {
		return userSmartspace;
	}

	public void setUserSmartspace(String userSmartspace) {
		this.userSmartspace = userSmartspace;
	}

	@Transient
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Enumerated(EnumType.STRING)
	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return "UserEntity [userKey=" + userKey + ", userSmartspace=" + userSmartspace + ", userEmail=" + userEmail
				+ ", username=" + username + ", avatar=" + avatar + ", role=" + role + ", points=" + points + "]";
	}

}
