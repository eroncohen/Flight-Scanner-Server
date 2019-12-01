package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import smartspace.dao.memory.MemoryUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;

public class MemoryUserEntityDaoUpdateUnitTests {

	@Test
	public void testUpdateUserNameWitNewName() throws Exception {
		UserKey key = new UserKey("smartspace","44");
		String newName = "newName";
		// GIVEN MemoryEntityDao is initialized
		// AND a user in the Dao

		MemoryUserDao dao = MemoryEntityDaoIsInitialized();
		UserEntity user = new UserEntity("2018B.Rickyd", "dummy@mail.com", "oldName", ":)", UserRole.PLAYER, 2);
		user.setKey(key);
		dao.getMemory().put(key, user);

		// WHEN Update a users details
		UserEntity userUpdate = new UserEntity();
		userUpdate.setKey(key);
		userUpdate.setUsername(newName);
		dao.update(userUpdate);

		// THEN the updated user exists
		// AND is has a newName
		assertThat(dao.getMemory().get(key)).isNotNull();
		assertThat(dao.getMemory().get(key).getUsername()).isEqualTo(newName);
	}

	@Test
	public void testUpdateUserEmailWithNewEmail() throws Exception {
		UserKey key = new UserKey("smartspace","44");
		String updateEmail = "newName@afeka.ac.il";

		// GIVEN MemoryEntityDao is initialized
		MemoryUserDao dao = MemoryEntityDaoIsInitialized();
		UserEntity user = new UserEntity("2018B.Rickyd", "dummy@mail.com", "oldName", ":)", UserRole.PLAYER, 2);
		user.setKey(key);
		dao.getMemory().put(key, user);
		// WHEN I create with Valid UserEntity and Update his Email Field
		UserEntity userUpdate = new UserEntity();
		userUpdate.setKey(key);
		userUpdate.setUserEmail(updateEmail);
		dao.update(userUpdate);

		// THEN the user email field change to the new user email
		assertThat(dao.getMemory().get(key).getUserEmail()).isNotNull().isEqualTo(updateEmail);
	}

	@Test
	public void testUpdateUserAvatarWithNewAvatar() throws Exception {
		UserKey key = new UserKey("smartspace","44");
		String updateAvatar = "newAvatar";

		// GIVEN MemoryEntityDao is initialized
		MemoryUserDao dao = MemoryEntityDaoIsInitialized();
		UserEntity user = new UserEntity("2018B.Rickyd", "dummy@mail.com", "oldName", ":)", UserRole.PLAYER, 2);
		user.setKey(key);
		dao.getMemory().put(key, user);
		// WHEN I create with Valid UserEntity and Update his avatar Field
		UserEntity userUpdate = new UserEntity();
		userUpdate.setKey(key);
		userUpdate.setAvatar(updateAvatar);
		dao.update(userUpdate);

		// THEN the user avatar field change to the new user avatar
		assertThat(dao.getMemory().get(key).getAvatar()).isNotNull().isEqualTo(updateAvatar);
	}

	@Test
	public void testUpdateUserRoleWithNewRole() throws Exception {
		UserKey key = new UserKey("smartspace","44");
		UserRole updateRole = UserRole.PLAYER;

		// GIVEN MemoryEntityDao is initialized
		MemoryUserDao dao = MemoryEntityDaoIsInitialized();
		UserEntity user = new UserEntity("2018B.Rickyd", "dummy@mail.com", "oldName", ":)", UserRole.MANAGER, 2);
		user.setKey(key);
		dao.getMemory().put(key, user);

		// WHEN I create with Valid UserEntity and Update his role Field
		UserEntity userUpdate = new UserEntity();
		userUpdate.setKey(key);
		userUpdate.setRole(updateRole);
		dao.update(userUpdate);

		// THEN the user role field change to the new user role
		assertThat(dao.getMemory().get(key).getRole()).isNotNull().isEqualTo(updateRole);
	}

	@Test
	public void testUpdateUserPointWithNewPoint() throws Exception {
		UserKey key = new UserKey("smartspace","44");
		long updatePoints = 111111;

		// GIVEN MemoryEntityDao is initialized
		MemoryUserDao dao = MemoryEntityDaoIsInitialized();
		UserEntity user = new UserEntity("2018B.Rickyd", "dummy@mail.com", "oldName", ":)", UserRole.MANAGER, 2);
		user.setKey(key);
		dao.getMemory().put(key, user);

		// WHEN I create with Valid UserEntity and Update his point Field
		UserEntity userUpdate = new UserEntity();
		userUpdate.setKey(key);
		userUpdate.setPoints(updatePoints);
		;
		dao.update(userUpdate);

		// THEN the user point field change to the new user point
		assertThat(dao.getMemory().get(key).getPoints()).isNotNull().isPositive().isEqualTo(updatePoints);
	}

	/** Initialize Memory Entity Dao **/
	private MemoryUserDao MemoryEntityDaoIsInitialized() {
		String smartspace = "smartspace";
		MemoryUserDao dao = new MemoryUserDao();
		dao.setSmartspace(smartspace);
		return dao;
	}

}
