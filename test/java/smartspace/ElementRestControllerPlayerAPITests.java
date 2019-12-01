package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

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
import smartspace.logic.ElementService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ElementRestControllerPlayerAPITests {

	private int port;
	private String baseUrl;
	private RestTemplate restTemplate;
	private ElementDao<ElementKey> elementDao;
	private UserDao<UserKey> userDao;
	private ElementService elementService;

	@Autowired
	public void setElementDao(ElementDao<ElementKey> elementDao) {
		this.elementDao = elementDao;
	}

	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
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
		UserEntity player = new UserEntity("play@er.test", "2019b.rickyd", "Tester", ">8^)", UserRole.PLAYER, 0);
		this.userDao.create(player);
	}

	@After
	public void tearDown() {
		this.elementDao.deleteAll();
		this.userDao.deleteAll();
	}
	
	
	@Test
	public void testGetExistingElement() throws Exception {
		// GIVEN I have a Player on the DB
		// AND I have 21 elements on the DB
		
		String playerEmail = "play@er.test";
		String playerSmartspace = "2019b.rickyd";
		String playerUrl = this.baseUrl + "/" + playerSmartspace + "/" + playerEmail;
		
		IntStream.range(0, 20)
		.mapToObj(i -> new ElementEntity(
				new Location(i,i+1)
				,"Test #" + i
				, "Test"
				, new Date()
				, false
				, "smartspace"
				,"TestManagerElement#" + i + "@mail.com"
				, new HashMap<>()))
		.forEach(this.elementService::writeElement);
		ElementEntity requestedElement = new ElementEntity(new Location(1, 5), "requested", "requested", new Date(), false,
				"smartspaceTest", "Manag@er.test", new HashMap<>());
		String requestedId = "999";
		String requestedSmartpace = "smartspaceTest";
		requestedElement.setElementSmartspace(requestedSmartpace);
		requestedElement.setElementId(requestedId);
		requestedElement.setKey(new ElementKey( requestedSmartpace,requestedId));
		this.elementService.writeElement(requestedElement);
		
		//WHEN I get element by id
		ElementBoundary results = 
				  this.restTemplate
					.getForObject(
							playerUrl + "/{elementSmartspace}/{elementId}", 
							ElementBoundary.class, 
							requestedSmartpace, requestedId);
		
		// THEN the requested element is not null 
		// AND equal to the requested element		
		assertThat(results.convertToEntity()).isNotNull().isEqualToComparingFieldByField(requestedElement);
	}
		
	@Test(expected = RuntimeException.class)
	public void testGetNotExistingElement() throws Exception {
		// GIVEN I have a Player on the DB
		// AND 20 elements on the DB
		String playerEmail = "play@er.test";
		String playerSmartspace = "2019b.rickyd";
		String playerUrl = this.baseUrl + "/" + playerSmartspace + "/" + playerEmail;
		
		IntStream.range(0, 20)
		.mapToObj(i -> new ElementEntity(
				new Location(i,i+1)
				,"Test #" + i
				, "Test"
				, new Date()
				, false
				, "smartspace"
				,"TestManagerElement#" + i + "@mail.com"
				, new HashMap<>()))
		.forEach(this.elementService::writeElement);
		
		
		
		//WHEN I get element by not existing id
		String requestedId = "999";
		String requestedSmartpace = "smartspaceTest";
		
	
		this.restTemplate.getForObject(playerUrl + "/{elementSmartspace}/{elementId}", ElementBoundary.class,
				requestedSmartpace, requestedId);
		
		//THEN a RuntimeException will be thrown
		
	}
	@Test
	public void testGetElementsByType() throws Exception {
		// GIVEN I have 25 elements on the DB
		// AND 5 of them with type "flight"
		
		String playerEmail = "play@er.test";
		String playerSmartspace = "2019b.rickyd";
		String playerUrl = this.baseUrl + "/" + playerSmartspace + "/" + playerEmail;
		
		IntStream.range(0, 20)
		.mapToObj(i -> new ElementEntity(
				new Location(i,i+1)
				,"Test #" + i
				, "Test"
				, new Date()
				, false
				, "smartspace"
				,"TestManagerElement#" + i + "@mail.com"
				, new HashMap<>()))
		.forEach(this.elementService::writeElement);
		String type = "flight";
		IntStream.range(20, 25)
		.mapToObj(i -> new ElementEntity(
				new Location(i,i+1)
				,"Test #" + i
				, type
				, new Date()
				, false
				, "smartspace"
				,"TestManagerElement#" + i + "@mail.com"
				, new HashMap<>()))
		.forEach(this.elementService::writeElement);
		
		//WHEN I get elements by type "flight" without setting pagination
		ElementBoundary[] results = 
				  this.restTemplate.getForObject(playerUrl + "?search=type&value={type}", ElementBoundary[].class, type);
					
		
		// THEN the response contains 5 elements	
		assertThat(results).hasSize(5);
	}
	
	@Test
	public void testGetElementsByTypeWithNoResult() throws Exception {
		// GIVEN I have 20 elements on the DB
		// AND 0 of them with type "flight"
		
		String playerEmail = "play@er.test";
		String playerSmartspace = "2019b.rickyd";
		String playerUrl = this.baseUrl + "/" + playerSmartspace + "/" + playerEmail;
		
		IntStream.range(0, 20)
		.mapToObj(i -> new ElementEntity(
				new Location(i,i+1)
				,"Test #" + i
				, "Test"
				, new Date()
				, false
				, "smartspace"
				,"TestManagerElement#" + i + "@mail.com"
				, new HashMap<>()))
		.forEach(this.elementService::writeElement);
		String type = "flight";
		
		//WHEN I get elements by type "flight" without setting pagination
		ElementBoundary[] results = 
				  this.restTemplate.getForObject(playerUrl + "?search=type&value={type}", ElementBoundary[].class, type);
					
		
		// THEN the response contains 0 elements	
		assertThat(results).hasSize(0);
	}
	
	@Test
	public void testGetAllElementsUsingPagination() throws Exception {
		// GIVEN I have 20 elements on the DB
		
		String playerEmail = "play@er.test";
		String playerSmartspace = "2019b.rickyd";
		String playerUrl = this.baseUrl + "/" + playerSmartspace + "/" + playerEmail;
		
		IntStream.range(0, 20)
		.mapToObj(i -> new ElementEntity(
				new Location(i,i+1)
				,"Test #" + i
				, "Test"
				, new Date()
				, false
				, "smartspace"
				,"TestManagerElement#" + i + "@mail.com"
				, new HashMap<>()))
		.forEach(this.elementService::writeElement);
		
		//WHEN I get elements on page 0 and size 30
		ElementBoundary[] results = 
				  this.restTemplate.getForObject(playerUrl + "?size={size}&page={page}", ElementBoundary[].class, 30,0);
					
		
		// THEN the response contains 20 elements	
		assertThat(results).hasSize(20);
	}
	
	@Test
	public void testGetAllElementsWithFalseExpiredOnlyUsingPagination() throws Exception {
		// GIVEN I have 20 elements on the DB with true expired
		// AND 10 elements with false expired
		
		String playerEmail = "play@er.test";
		String playerSmartspace = "2019b.rickyd";
		String playerUrl = this.baseUrl + "/" + playerSmartspace + "/" + playerEmail;
		
		IntStream.range(0, 20)
		.mapToObj(i -> new ElementEntity(
				new Location(i,i+1)
				,"Test #" + i
				, "Test"
				, new Date()
				, true
				, "smartspace"
				,"TestManagerElement#" + i + "@mail.com"
				, new HashMap<>()))
		.forEach(this.elementService::writeElement);
		
		IntStream.range(20, 30)
		.mapToObj(i -> new ElementEntity(
				new Location(i,i+1)
				,"Test #" + i
				, "Test"
				, new Date()
				, false
				, "smartspace"
				,"TestManagerElement#" + i + "@mail.com"
				, new HashMap<>()))
		.forEach(this.elementService::writeElement);
		
		
		//WHEN I get elements on page 0 and size 30
		ElementBoundary[] results = 
				  this.restTemplate.getForObject(playerUrl + "?size={size}&page={page}", ElementBoundary[].class, 30,0);
					
		
		// THEN the response contains 10 elements	
		assertThat(results).hasSize(10);
		
	}
}
