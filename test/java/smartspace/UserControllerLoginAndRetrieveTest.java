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

public class UserControllerLoginAndRetrieveTest {
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
		this.url = "http://localhost:" + this.port + "/smartspace/users/login/";
		this.rest = new RestTemplate();
	}
	
	@After
	public void tearDown() {
		this.userDao.deleteAll();
	}
	
	@Test
	public void testLoginAndRetrieveDetails() {
		
	// GIVEN the user eranLouie3@gmail.com is in the database 
	String userEmail = "eranLouie3@gmail.com";
	String smartspace = "2019b.rickyd";
	
	UserEntity user = new UserEntity(userEmail,smartspace, "eran", "", UserRole.PLAYER, 3);
	user=this.userDao.create(user);	
				
	// WHEN eran login
	UserBoundary response = this.rest.getForObject(this.url+smartspace+"/"+userEmail, UserBoundary.class);
	

	// THEN the login seceded
	assertThat(
			response).isNotNull();
	assertThat(response.getRole()).isEqualTo(UserRole.PLAYER.toString());
	}
	
	
	@Test(expected=RuntimeException.class)
	public void testFailLoginAndRetrieveDetails() {
		
	// GIVEN the user eranLouie3@gmail.com is not in the database 
	String userEmail = "eranLouie3@gmail.com";
	String smartspace = "2019b.rickyd";
	
	//UserEntity user = new UserEntity(userEmail,smartspace, "eran", "", UserRole.PLAYER, 3);
				
	// WHEN eran login
	this.rest.getForObject(this.url+smartspace+"/"+userEmail, UserBoundary.class);
	
	// THEN exception is thrown
	
}
	
	
	
	
}
