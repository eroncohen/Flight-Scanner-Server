package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.UserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class RdbUserDaoIntegrationTests {

	private UserDao<UserKey> userDao;
	private EntityFactory factory;

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@Autowired
	public void setUserDao(UserDao<UserKey> userDao) {
		this.userDao = userDao;
	}

	@Before
	public void setup() {
		this.userDao.deleteAll();
	}


	@Test
	public void createManyUsers() {
		// GIVEN we have clean database
		// AND a factory

		// WHEN I create numberOfUsers
		int numberOfUsers = 2;
		List<UserEntity> listOfUsers = IntStream.range(0, numberOfUsers)// int stream
				.mapToObj(num -> "dummy #" + num)// String stream
				.map(name -> this.factory.createNewUser(// user entity stream
						"2019B.rickyd." + name, name + "@mail.com", name, name + " :)", UserRole.PLAYER, 2))
				.map(this.userDao::create).collect(Collectors.toList());
		List<UserEntity> actualUsers = this.userDao.readAll();
		// THEN the listOfUser is stored

		assertThat(actualUsers).hasSize(numberOfUsers).usingElementComparatorOnFields("userKey")
				.containsAll(listOfUsers);

	}

	@Test
	public void testCreateUpdateReadByIdDeleteAllReadAll() throws Exception {
		// GIVEN we have a clear database
		// AND we have a factory

		// WHEN I create a player
		// AND I update a player
		// AND I read player by key
		// AND I delete all
		// AND I read all
		String username = "Player1";
		UserRole role = UserRole.PLAYER;
		String userEmail = "eronc@mail.afeka.ac.il";
		String userSmartspace = "2019B.rickyd";
		String avatar = ":)";
		long point = 1;

		UserEntity player1 = factory.createNewUser(userEmail, userSmartspace, username, avatar, role, point);
		player1 = this.userDao.create(player1);

		UserEntity initUser = new UserEntity();
		initUser.setKey(player1.getKey());

		UserRole updateRole = UserRole.MANAGER;
		String updateUserEmail = "rickyyy44@gmail.com";
		String updateAvatar = ":')";
		UserEntity updateUser = new UserEntity();
		updateUser.setKey(player1.getKey());
		updateUser.setRole(updateRole);
		updateUser.setUserEmail(updateUserEmail);
		updateUser.setAvatar(updateAvatar);
		this.userDao.update(updateUser);
		
		Optional<UserEntity> userOp = this.userDao.readById(player1.getKey());

		this.userDao.deleteAll();

		List<UserEntity> listAfterDeletion = this.userDao.readAll();

		// THEN the initially generated users key is not null
		// AND the user read using key is present
		// AND the key of user read is not null
		// AND the list after deletion is empty

		assertThat(initUser.getKey()).isNotNull();

		assertThat(userOp).isPresent().get().extracting("key").containsExactly(player1.getKey());

		assertThat(listAfterDeletion).isEmpty();
	}
}
