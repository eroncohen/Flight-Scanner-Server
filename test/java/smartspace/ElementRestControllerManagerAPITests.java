package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
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
import smartspace.layout.LatLng;
import smartspace.layout.UserBoundaryKey;
import smartspace.logic.ElementService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ElementRestControllerManagerAPITests {
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
		UserEntity manager = new UserEntity("Manag@er.test", "smartspaceTest", "Tester", ">8^)", UserRole.MANAGER,
				9999);
		this.userDao.create(manager);
	}

	@After
	public void tearDown() {
		this.elementDao.deleteAll();
		this.userDao.deleteAll();
	}

	@Test
	public void testCreateElement() throws Exception {
		String smartspace = "2019b.rickyd";
		tearDown();
		setUp();
		// GIVEN the database has a manager

		String managerEmail = "Manag@er.test";
		String managerSmartspace = "smartspaceTest";
		String managerUrl = this.baseUrl + "/" + managerSmartspace + "/" + managerEmail;
		// WHEN I post a new element with a null key

		ElementBoundary eb = new ElementBoundary();
		eb.setKey(null);
		eb.setElementType("type");
		eb.setName("Test");
		eb.setCreated(new Date());
		eb.setCreator(new UserBoundaryKey("smartspaceTest", "Manag@er.test"));
		eb.setLatlng(new LatLng(1.0, 2.3));
		eb.setElementProperties(new TreeMap<String, Object>());
		eb.setExpired(false);

		ElementBoundary response = this.restTemplate.postForObject(managerUrl, eb, ElementBoundary.class);

		// THEN the database contains 1 element
		// the return key is not null
		assertThat(this.elementDao.readAll()).hasSize(1);
		assertThat(response.getKey().toString()).isNotNull().startsWith(smartspace);
	}

	@Test
	public void testUpdateElement() throws Exception {
		// GIVEN the database has a manager
		// AND an element

		String managerEmail = "Manag@er.test";
		String managerSmartspace = "smartspaceTest";

		String updateVal = "update";

		ElementEntity element1 = new ElementEntity(new Location(1, 5), "element1", "filght", new Date(), false,
				"smartspaceTest", "Manag@er.test", new HashMap<>());
		element1 = this.elementService.createElement(element1, managerSmartspace, managerEmail);

		String managerUrl = this.baseUrl + "/" + managerSmartspace + "/" + managerEmail + "/"
				+ element1.getElementSmartspace() + "/" + element1.getElementId();

		// WHEN I update an element with a type and name

		ElementBoundary elementUpdate = new ElementBoundary();
		elementUpdate.setName(updateVal);
		elementUpdate.setElementType(updateVal);
		this.restTemplate.put(managerUrl, elementUpdate, ElementBoundary.class);

		// THEN the database contains an updated element name and type
		assertThat(this.elementDao.readById(element1.getKey())).isPresent().get().extracting("name", "type")
				.containsExactly(updateVal, updateVal);
	}
	
	@Test
	public void GetAllElementsWithExpiredTrueAndFalseUsingPagination() throws Exception {
		//GIVEN I have 10 elements with expired true
		//AND 10 elements with expired false
		String managerEmail = "Manag@er.test";
		String managerSmartspace = "smartspaceTest";
		String managerUrl = this.baseUrl + "/" + managerSmartspace + "/" + managerEmail;
		
		IntStream.range(0, 10)
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
		
		IntStream.range(10,20)
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
						  this.restTemplate.getForObject(managerUrl + "?size={size}&page={page}", ElementBoundary[].class, 30,0);
							
				
		// THEN the response contains 20 elements	
		assertThat(results).hasSize(20);
	}
}
