package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.AdvancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class AdvancedUserDaoTests {
	private AdvancedUserDao<UserKey> userDao;
	private EntityFactory factory;

	@Autowired
	public void setAdvancedUserDao(AdvancedUserDao<UserKey> userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setEntityFactiory(EntityFactory factory) {
		this.factory = factory;
	}
	

	@After
	public void tearDown() {
		this.userDao.deleteAll();
	}
	
	@Test
	public void testReadAllWithPagination() throws Exception{
		//GIVEN the db contains 20 users
		IntStream.range(0, 20)
			.mapToObj(i ->this.factory.createNewUser(
					"ronenr"+i+"@mail.afeka.ac.il",
					"smartspace",
					"username",
					"avatar",
					UserRole.PLAYER,
					2))
					.forEach(this.userDao::create);
					
					
		
		//WHEN i read 3 users from page 6
		List<UserEntity> actual = this.userDao.readAll(3, 6);
		
		//THEN i recive 2 messages
		assertThat(actual).hasSize(2);
	}
	@Test
	public void testReadAllPaginationAndSortByUserName() throws Exception{
		//GIVEN the database contains only 10 users
		IntStream.range(0, 10)
		.mapToObj(i ->this.factory.createNewUser(
				i+"ronenr4@mail.afeka.ac.il",
				"smartspace",
				"username #"+i,
				"avatar",
				UserRole.PLAYER,
				2))
				.forEach(this.userDao::create);
		
		
		
		//WHEN i read 2 messages from page 3 and sorting by name
		List<UserEntity> actual = userDao.readAll("username", 2, 3);
		
		//THEN i get users with username containing : "6" ,"7"
		assertThat(actual)
			.usingElementComparatorOnFields("username")
			.containsExactly(
					factory.createNewUser(null, null, "username #6", null, null, 0),
					factory.createNewUser(null, null, "username #7", null, null, 0));
	}
	
	@Test
	public void testReadAllWithPaginationFromTheStartAndSortByText() throws Exception{
		//GIVEN the db contains only 10 users
		IntStream.range(0, 10)
		.mapToObj(i ->this.factory.createNewUser(
				i+"ronenr4@mail.afeka.ac.il",
				"smartspace",
				"username #"+i,
				"avatar",
				UserRole.PLAYER,
				2))
				.forEach(this.userDao::create);
		
		//WHEN i read 3 meesages from page 0 and sorting by name
		
		List<UserEntity> actual = userDao.readAll("username", 3, 0);
		
		//Then i get the 3 users from the start with numbers - 0,1,2
		assertThat(actual)
			.usingElementComparatorOnFields("username")
			.containsExactly(
					factory.createNewUser(null, null, "username #0", null, null, 0),
					factory.createNewUser(null, null, "username #1", null, null, 0),
					factory.createNewUser(null, null, "username #2", null, null, 0));
			
	}
	
	
	@Test 
	public void testReadUserByRole() throws Exception{
		//GIVEN the db contains only 10 users, 9 of them has role of player and 1 of admin
		
		IntStream.range(0, 10)
		.mapToObj(i ->this.factory.createNewUser(
				i+"ronenr4@mail.afeka.ac.il",
				"smartspace",
				"username",
				"avatar",
				UserRole.PLAYER,
				2))
				.forEach(this.userDao::create);
		
		UserEntity user = this.factory.createNewUser(
				"ronenr4@mail.afeka.ac.il",
				"smartspace",
				"username",
				"avatar",
				UserRole.ADMIN,
				2);
		this.userDao.create(user);
		
		//WHEN i read all with role admin with size 3 and page 0
		
		List<UserEntity> actual = this.userDao.readUserByRole(UserRole.ADMIN, 3, 0);
		
		//THEN i receive 1 user
		assertThat(actual).hasSize(1);
		
		
		assertThat(actual)
			.usingElementComparatorOnFields("role")
			.containsExactly(
					factory.createNewUser(null, null, null, null, UserRole.ADMIN, 0));
		
		
	}

}
