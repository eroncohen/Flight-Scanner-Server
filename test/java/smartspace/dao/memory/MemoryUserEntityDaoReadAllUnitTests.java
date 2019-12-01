package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import smartspace.dao.memory.MemoryUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;

public class MemoryUserEntityDaoReadAllUnitTests {

	@Test
	public void testReadByAllWithNonEmptyMemory() throws Exception {
		int numberOfUsers = 5;
		
		// GIVEN MemoryEntityDao is initialized
		// AND the dao has numberOfUsers userEntities

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
			userEntity.setKey(new UserKey("2019B.rickyd.",key++ + ""));
			dao.getMemory().put(userEntity.getKey(), userEntity);
		}

		// WHEN I read numOfUserEntity in memory
		List<UserEntity> daoUsers = dao.readAll();

		// THEN the returned List is not null and the size of of the list equal to
		// numberOfUsers
		assertThat(daoUsers).isNotNull();
		assertThat(dao.readAll().size()).isEqualTo(numberOfUsers);
	}

	@Test
	public void testReadByAllWithEmptyMemory() throws Exception {

		// GIVEN MemoryEntityDao is initialized
		String smartspace = "smartspace.test";
		MemoryUserDao dao = new MemoryUserDao();
		dao.setSmartspace(smartspace);

		// WHEN I don't have user entity in the dao memory
		List<UserEntity> daoUsers = dao.readAll();

		// THEN the returned ArrayList need to be empty
		assertThat(daoUsers).isEmpty();
		assertThat(daoUsers.size()).isZero();

	}
}
