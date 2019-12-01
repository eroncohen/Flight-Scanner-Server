package smartspace.layout;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public class UserForm {

	private String email;
	private String role; // keep in mind that its an Enum.
	private String username;
	private String avatar;

	public UserForm() {
	}
	
	public UserForm(String email, String role, String username, String avatar) {
		super();
		this.email = email;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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

	public UserEntity convertToEntity() {
		UserEntity userEntity = new UserEntity();

		userEntity.setKey(null);

		userEntity.setAvatar(this.avatar);
		userEntity.setUsername(this.username);
		userEntity.setUserEmail(this.email);
		if (this.role != null) {
			userEntity.setRole(UserRole.valueOf(this.role));
		} else {
			userEntity.setRole(null);
		}
		return userEntity;
	}

	@Override
	public String toString() {
		return "userForm [email=" + email + ", role=" + role + ", username=" + username + ", avatar=" + avatar + "]";
	}


}
