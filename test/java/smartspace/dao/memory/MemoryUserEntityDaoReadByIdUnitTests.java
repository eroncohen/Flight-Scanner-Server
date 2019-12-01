package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import smartspace.dao.memory.MemoryUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;

public class MemoryUserEntityDaoReadByIdUnitTests {

	@Test
	public void testReadByIdWithExistingIdOnMemory() {
		// GIVEN MemoryEntityDao is initialized
		// AND There are users in the memory
		UserKey existingKey = new UserKey("smartspace.test","2");
		MemoryUserDao dao = initUserDaoHelper();

		// WHEN an existing key is given to ReadById
		Optional<UserEntity> memoryUser = dao.readById(existingKey);

		// THEN the returned user exists
		// AND has the key that was given
		assertThat(memoryUser).isPresent().get().extracting("userKey").containsExactly(existingKey);
	}

	@Test
	public void testReadByIdWithNotExistingIdOnMemory() {
		// GIVEN MemoryEntityDao is initialized
		// AND There are users in the memory
		UserKey notExistingKey = new UserKey("smartspace","10");
		MemoryUserDao dao = initUserDaoHelper();
		// WHEN an existing key is given to ReadById
		Optional<UserEntity> memoryUser = dao.readById(notExistingKey);

		// THEN the returned user exists
		// AND has the key that was given
		assertThat(memoryUser).isNotPresent();
	}

	// initialize a dao with numberOfUsers Helper
	private MemoryUserDao initUserDaoHelper() {
		int numberOfUsers = 5;
		String smartspace = "smartspace.test";
		MemoryUserDao dao = new MemoryUserDao();
		dao.setSmartspace(smartspace);

		int key = 0;

		List<UserEntity> listOfUsers = IntStream.range(0, numberOfUsers)// int stream
				.mapToObj(num -> "dummy #" + num)// String stream
				.map(name -> new UserEntity(// user entity stream
						"2019B.rickyd." + name, name + "@mail.com", name, name + " :)", UserRole.PLAYER, 2))
				.collect(Collectors.toList());
		// putting the user list on memory with keys
		for (UserEntity userEntity : listOfUsers) {
			userEntity.setKey(new UserKey(smartspace,key++ + ""));
			dao.getMemory().put(userEntity.getKey(), userEntity);
		}
		return dao;
	}
}
