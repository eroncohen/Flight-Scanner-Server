package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import smartspace.dao.UserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;
import smartspace.layout.UserBoundary;
import smartspace.layout.UserForm;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class UserControllerCreateTest {
	private int port;
	private String url;
	private RestTemplate rest;
	private UserDao<UserKey> userDao;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@Autowired
	public void setMessageDao(UserDao<UserKey> userDao) {
		this.userDao = userDao;
	}
	
	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + this.port + "/smartspace/users";
		this.rest = new RestTemplate();
	}
	
	@After
	public void tearDown() {
		this.userDao.deleteAll();
	}
	
	@Test
	public void testCreateNewUserWithEmptyDB() {
		
		// GIVEN the database is clean
				
		// WHEN I create a new user with validate details UserEntity
		UserForm ub =  new UserForm() ;
		ub.setAvatar("avtar");
		ub.setRole("ADMIN");
		ub.setUsername("Ricky");
		ub.setEmail("ricky@mail.afeka.ac.il");	
		

		UserBoundary response = this.rest.postForObject(this.url, ub, UserBoundary.class);
		

		// THEN the database contains 1 user
		// AND the returned user is similar to the user in the database

		assertThat(
				this.userDao.readAll())
			.hasSize(1)
			.usingElementComparatorOnFields("key")
			.containsExactly(response.convertToEntity());
	}
	
	
	
	@Test
	public void testCreateNewUserWithNoEmptyDB() {
		
		// GIVEN the db has one user
		UserEntity firstUser= new UserEntity();
		firstUser.setUserEmail("firstUser@istator.test");
		firstUser.setUserSmartspace("2019b.rickyd");
		firstUser.setAvatar("=]");
		firstUser.setRole(UserRole.PLAYER);
		firstUser=this.userDao.create(firstUser);

		
		// WHEN I create a new user with validate details UserEntity
		UserForm ub =  new UserForm() ;
		ub.setAvatar("avtar");
		ub.setRole("ADMIN");
		ub.setUsername("Ricky");
		ub.setEmail("Liran@gmail.il");	
		

		this.rest.postForObject(this.url, ub, UserBoundary.class);
		

		// THEN the database contains 2 user
		// AND the returned user is similar to the user in the database

		assertThat(
				this.userDao.readAll())
			.hasSize(2);	
	}
	@Test(expected = RuntimeException.class)
	public void testCreateNewUserNotValidEmail() {
		
		// GIVEN the database is clean
				
		// WHEN I create a new user with non valid email 
		UserForm ub =  new UserForm() ;
		ub.setAvatar("avtar");
		ub.setRole("ADMIN");
		ub.setUsername("Ricky");
		ub.setEmail("@mail.afeka.ac.il");	
		

		this.rest.postForObject(this.url, ub, UserBoundary.class);

		// THEN we will get Run time Exception
	}

		@Test(expected = RuntimeException.class)
		public void testCreateNewUserNotValidRole() {
			
			// GIVEN the database is clean
					
			// WHEN I create a new user with empty not valid role
			UserForm ub =  new UserForm() ;
			ub.setAvatar("avtar");
			ub.setRole("");
			ub.setUsername("Ricky");
			ub.setEmail("@mail.afeka.ac.il");	
			

			this.rest.postForObject(this.url, ub, UserBoundary.class);

			// THEN we will get Run time Exception

	}
}
