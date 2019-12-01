package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import smartspace.dao.memory.MemoryUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public class MemoryUserEntityDaoCreateUnitTests {
	@Test
	public void testCreateWithValidUserEntity() throws Exception {

		// GIVEN MemoryEntityDao is initialized
		String smartspace = "2019B.rickyd";
		MemoryUserDao dao = new MemoryUserDao();
		dao.setSmartspace(smartspace);

		// WHEN I create a UserEntity
		UserEntity newUserEntity = new UserEntity(smartspace, "dummy@mail.il", "test", ":)", UserRole.PLAYER, 1);
		UserEntity createdUser = dao.create(newUserEntity);

		// THEN the returned UserEntity has a valid key
		// AND the dao is added with the user
		// AND no exception is thrown
		assertThat(createdUser.getKey().toString()).isNotNull().startsWith(smartspace);
		assertThat(dao.getMemory().values()).usingElementComparatorOnFields("userKey").contains(newUserEntity);

	}

	@Test
	public void testCreateTwoUsersEntity() throws Exception {

		// GIVEN MemoryMessageDao is initialized

		String smartspace = "2019B.rickyd";
		MemoryUserDao dao = new MemoryUserDao();
		dao.setSmartspace(smartspace);

		// WHEN I create numOfUserEntity valid UserEntity
		List<UserEntity> list = IntStream.range(1, 3)// int stream
				.mapToObj(num -> "dummy #" + num)// String stream
				.map(name -> new UserEntity(// entity stream
						name + "@mail.com", "2019B.rickyd." + name, name, name + " :)", UserRole.PLAYER, 2))
				.map(dao::create).collect(Collectors.toList());

		// THEN the dao contains exactly 2 Users with inserted username
		// AND the Users' Keys are different
		assertThat(dao.getMemory().values()).hasSize(2).usingElementComparatorOnFields("username")
				.containsExactlyElementsOf(list);
		assertThat(list.get(0)).usingComparatorForFields((String k1, String k2) -> k1.compareTo(k2), "userKey",
				"userSmartspace", "userEmail", "username", "avatar").isNotEqualTo(list.get(1));

	}

}
