package smartspace.dao;

import java.util.List;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public interface AdvancedUserDao<UserKey> extends UserDao<UserKey> {
	
	public List<UserEntity> readAll(int size, int page);
	public List<UserEntity> readAll(String sortBy, int size, int page);
	public List<UserEntity> readUserByRole (UserRole userRole, int size, int page);
	
}
