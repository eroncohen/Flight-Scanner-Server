package smartspace.logic;

import java.util.List;

import smartspace.data.UserEntity;

public interface UserService {

	UserEntity writeUser(UserEntity user);

	public List<UserEntity> importUsers(List<UserEntity> userEntities, String adminSmartspace, String adminEmail);

	public List<UserEntity> exportUsers(int size, int page, String adminSmartspace, String adminEmail);

	void update(UserEntity entity, String userSmartspace, String userEmail);

	public UserEntity createUser(UserEntity userEntity);

	public UserEntity getUser(String userSmartspace, String userEmail);

}
