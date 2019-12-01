package smartspace.layout;

import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;

public class UserBoundary {

	private UserBoundaryKey key;
	private String role; // keep in mind that its an Enum.
	private String username;
	private String avatar;
	private long points;

	public UserBoundary() {
		this.key = new UserBoundaryKey();
	}

	public UserBoundary(UserEntity userEntity) {
		this();
		this.key.setSmartspace(userEntity.getUserSmartspace());
		this.key.setEmail(userEntity.getUserEmail());
		this.username = userEntity.getUsername();
		this.avatar = userEntity.getAvatar();
		this.role = userEntity.getRole().name();
		this.points = userEntity.getPoints();
	}

	public UserEntity convertToEntity() {
		UserEntity userEntity = new UserEntity();

		userEntity.setKey(null);
		if (validateKey(this.key)) {
			userEntity.setUserSmartspace(this.key.getSmartspace());
			userEntity.setUserEmail(this.key.getEmail());
			userEntity.setKey(new UserKey(this.key.getSmartspace(), this.key.getEmail()));
		}

		userEntity.setAvatar(this.avatar);
		userEntity.setPoints(this.points);
		userEntity.setUsername(this.username);

		if (this.role != null) {
			userEntity.setRole(UserRole.valueOf(this.role));
		} else {
			userEntity.setRole(null);
		}
		return userEntity;
	}

	private boolean validateKey(UserBoundaryKey key) {
		return (key.getSmartspace() != null && key.getEmail() != null);
	}

	public UserBoundaryKey getKey() {
		return key;
	}

	public void setKey(UserBoundaryKey key) {
		this.key = key;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
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
		return "UserBoundary [key=" + key + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ ", points=" + points + "]";
	}
}