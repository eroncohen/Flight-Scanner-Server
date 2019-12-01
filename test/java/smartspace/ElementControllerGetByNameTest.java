package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.assertj.core.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import smartspace.dao.ElementDao;
import smartspace.dao.UserDao;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;
import smartspace.layout.ElementBoundary;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ElementControllerGetByNameTest {
	
	private int port;
	private String baseUrl;
	private RestTemplate restTemplate;
	private ElementDao<ElementKey> elementDao;
	private UserDao<UserKey> userDao;
	private String searchUrl = "search=name&";

	@Autowired
	public void setElementDao(ElementDao<ElementKey> elementDao) {
		this.elementDao = elementDao;
	}

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
		this.baseUrl = "http://localhost:" + port + "/smartspace/elements";
		this.restTemplate = new RestTemplate();
	}

	@Before
	public void setUp() {
		UserEntity player = new UserEntity("player@er.test", "smartspaceTest", "Tester", ">8^)", UserRole.PLAYER,
				9999);
		this.userDao.create(player);
	}

	@After
	public void tearDown() {
		this.elementDao.deleteAll();
		this.userDao.deleteAll();
	}
	
	@Test
	public void testGetAllElementsByName() throws Exception {
		
		// GIVEN the database has 30 elements with the name "groove"
		String name="groove";
		String smartspace = "2019b.rickyd";
		String playerEmail = "player@er.test";
		String playerSmartspace = "smartspaceTest";
		int size = 30,page=0;
		String playerUrl = this.baseUrl + "/" + playerSmartspace + "/" + playerEmail+"?"+searchUrl+"value="+name+"&size="+size+"&page="+page;
		IntStream.range(1, 31).mapToObj(i-> "element" + i).
		map(type-> this.elementDao.create(new ElementEntity(new Location(), name, type, new Date(), false, 
				smartspace, playerEmail, new HashMap<String, Object>()))).collect(Collectors.toList());
				
		// WHEN I send a get request 
		ElementBoundary[] response = this.restTemplate.getForObject(playerUrl, ElementBoundary[].class);

		// THEN the response will contain 30
		assertThat(response).hasSize(30);
	}
	
	@Test
	public void testGetElementsByNameWithPagination() throws Exception {
		
		// GIVEN the database has 30 elements with the name "groove"
		String name="groove";
		String smartspace = "2019b.rickyd";
		String playerEmail = "player@er.test";
		String playerSmartspace = "smartspaceTest";
		int size = 20,page=1;
		String playerUrl = this.baseUrl + "/" + playerSmartspace + "/" + playerEmail+"?"+searchUrl+"value="+name+"&size="+size+"&page="+page;
		IntStream.range(1, 31).mapToObj(i-> "element" + i).
		map(type-> this.elementDao.create(new ElementEntity(new Location(), name, type, new Date(), false, 
				smartspace, playerEmail, new HashMap<String, Object>()))).collect(Collectors.toList());
				
		// WHEN I send a get request 
		ElementBoundary[] response = this.restTemplate.getForObject(playerUrl, ElementBoundary[].class);

		// THEN the response will contain 10 elements
		assertThat(response).hasSize(10);
	}
	
	@Test
	public void testGetElementByName() throws Exception {
		
		// GIVEN the database has 30 elements with different names
		String smartspace = "2019b.rickyd";
		String playerEmail = "player@er.test";
		String playerSmartspace = "smartspaceTest";
		String playerUrl = this.baseUrl + "/" + playerSmartspace + "/" + playerEmail+"?"+searchUrl+"value="+"element4";
		IntStream.range(1, 31).mapToObj(i-> "element" + i).
		map(name-> this.elementDao.create(new ElementEntity(new Location(), name, "type", new Date(), false, 
				smartspace, playerEmail, new HashMap<String, Object>()))).collect(Collectors.toList());
				
		// WHEN I send a get request for element with name "element4"
		ElementBoundary[] response = this.restTemplate.getForObject(playerUrl, ElementBoundary[].class);

		// THEN the response will contain 1 element with name "element4"
		ElementBoundary eb = new ElementBoundary();
		eb.setName("element4");
		
		assertThat(new ArrayList<>(Arrays.asList(response))).hasSize(1)
		.usingElementComparatorOnFields("name")
		.containsExactly(eb);
	}


}
