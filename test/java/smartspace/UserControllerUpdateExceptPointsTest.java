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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class UserControllerUpdateExceptPointsTest {	
	private int port;
	private String url;
	private RestTemplate rest;
	private UserDao<UserKey> userDao;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@Autowired
	public void setUserDao(UserDao<UserKey> userDao) {
		this.userDao = userDao;
	}
	
	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + this.port + "/smartspace/users/login/{userSmartspace}/{userEmail}";
		this.rest = new RestTemplate();
	}
	
	@After
	public void tearDown() {
		this.userDao.deleteAll();
	}
	
	
	@Test
	public void testUpdateUserAvtar() {
		// GIVEN the database contains a user to update
		
		UserEntity userToUpdate = new UserEntity();
		userToUpdate.setUserSmartspace("rickyd.2019");
		userToUpdate.setUserEmail("ricky@mail.il");
		userToUpdate.setRole(UserRole.PLAYER);
		userToUpdate.setUsername("Ricky");
		userToUpdate.setAvatar("avtar");
		userToUpdate=this.userDao.create(userToUpdate);

		
		// WHEN I UPDATE the user avtar
				String newAvtar = ":)";
				UserBoundary update = new UserBoundary();
				update.setAvatar(newAvtar);
				this.rest
					.put(
						this.url, 
						update, 
						userToUpdate.getUserSmartspace(),userToUpdate.getUserEmail());

				// THEN the database contains an updated user avtar
				assertThat(this.userDao.readById(userToUpdate.getKey()))
					.isPresent()
					.get()
					.extracting("avatar")
					.containsExactly(newAvtar);

	}
	
	@Test
	public void testUpdateUserPoints() {
		// GIVEN the database contains a user to update with 100 points
		
		UserEntity userToUpdate = new UserEntity();
		userToUpdate.setUserSmartspace("rickyd.2019");
		userToUpdate.setUserEmail("ricky@mail.il");
		userToUpdate.setRole(UserRole.PLAYER);
		userToUpdate.setUsername("Ricky");
		userToUpdate.setAvatar("avtar");
		long pointsBeforeUpdate=100;
		userToUpdate.setPoints(pointsBeforeUpdate);
		userToUpdate=this.userDao.create(userToUpdate);

		
		// WHEN I UPDATE the user points to 50
				long newPoints=50;
				UserBoundary update = new UserBoundary();
				update.setPoints(newPoints);
				this.rest
					.put(
						this.url, 
						update, 
						userToUpdate.getUserSmartspace(),userToUpdate.getUserEmail());

				// THEN the database contains an updated user points with 100
				assertThat(this.userDao.readById(userToUpdate.getKey()))
					.isPresent()
					.get()
					.extracting("points")
					.containsExactly(pointsBeforeUpdate);
	}
	
	@Test
	public void testUpdateUserNameAndPoints() {
		// GIVEN the database contains a user to update with 100 points
		
		UserEntity userToUpdate = new UserEntity();
		userToUpdate.setUserSmartspace("rickyd.2019");
		userToUpdate.setUserEmail("ricky@mail.il");
		userToUpdate.setRole(UserRole.PLAYER);
		userToUpdate.setUsername("Ricky");
		userToUpdate.setAvatar("avtar");
		long pointsBeforeUpdate=100;
		userToUpdate.setPoints(pointsBeforeUpdate);
		userToUpdate=this.userDao.create(userToUpdate);

		
		// WHEN I UPDATE the user points and user name
				long newPoints=50;
				UserBoundary updateUser = new UserBoundary();
				updateUser.setPoints(newPoints);
				String newUserName="LiranTest";
				updateUser.setUsername(newUserName);
				this.rest
					.put(
						this.url, 
						updateUser, 
						userToUpdate.getUserSmartspace(),userToUpdate.getUserEmail());

				// THEN the database contains an updated user points with 100
				// THEN the database contains an updated user name 
				assertThat(this.userDao.readById(userToUpdate.getKey()))
					.isPresent()
					.get()
					.extracting("points")
					.containsExactly(pointsBeforeUpdate);
				
				assertThat(this.userDao.readById(userToUpdate.getKey()))
				.isPresent()
				.get()
				.extracting("username")
				.containsExactly(newUserName);
}
	
	
	@Test(expected = RuntimeException.class)
	public void testUpdateWithOutExistUser() {
		// GIVEN an empty database 
		UserEntity userToUpdate = new UserEntity();


		// WHEN I UPDATE  user name
				UserBoundary updateUser = new UserBoundary();
				String newUserName="LiranTest";
				updateUser.setUsername(newUserName);
				this.rest
					.put(
						this.url, 
						updateUser, 
						userToUpdate.getUserSmartspace(),userToUpdate.getUserEmail());

				// THEN we will get Run time Exception
				
}
	
	

					

}
