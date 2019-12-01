package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import smartspace.data.util.EntityFactory;
import smartspace.layout.ActionAndElementBoundaryKey;
import smartspace.layout.ElementBoundary;
import smartspace.layout.LatLng;
import smartspace.layout.UserBoundaryKey;
import smartspace.logic.ElementService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ElementRestControllerAdminAPITests {
	private int port;
	private String baseUrl;
	private RestTemplate restTemplate;
	private ElementDao<ElementKey> elementDao;
	private UserDao<UserKey> userDao;
	private ElementService elementService;
	private EntityFactory factory;

	@Autowired
	public void setElementDao(ElementDao<ElementKey> elementDao) {
		this.elementDao = elementDao;
	}

	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
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
		this.baseUrl = "http://localhost:" + port + "/smartspace/admin/elements";
		this.restTemplate = new RestTemplate();
	}

	@Before
	public void setUp() {
		UserEntity admin = this.factory.createNewUser("Admin@istator.test", "2019b.rickyd", "Tester", "8^)",
				UserRole.ADMIN, 9999);
		this.userDao.create(admin);
	}

	@After
	public void tearDown() {
		this.elementDao.deleteAll();
		this.userDao.deleteAll();
	}
	@Test
	public void testImportElement() throws Exception {

		// GIVEN the database is clean

		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;
		String name = "Unit-Test";

		// WHEN I post a new element

		ElementBoundary eb[] = { new ElementBoundary() };
		eb[0].setKey(new ActionAndElementBoundaryKey("smartspace", "33"));
		eb[0].setElementType("type");
		eb[0].setName(name);
		eb[0].setCreated(new Date());
		eb[0].setCreator(new UserBoundaryKey("smartspace", "crea@tor"));
		eb[0].setLatlng(new LatLng(1.0, 2.3));
		eb[0].setElementProperties(new TreeMap<String, Object>());
		eb[0].setExpired(false);
	

		ElementBoundary[] response = this.restTemplate.postForObject(adminUrl, eb, ElementBoundary[].class);

		// THEN the database contains 1 element
		// the return key is not null
		// AND the returned element is similar to the element in the database
		assertThat(response[0].getKey()).isNotNull();

		assertThat(this.elementDao.readAll()).hasSize(1).usingElementComparatorOnFields("key")
				.containsExactly(response[0].convertToEntity());
	}
	@Test
	public void testExportElement() throws Exception {

		// GIVEN the database has 1 admin and no Elements 

		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;
		String name = "Unit-Test";

		ElementBoundary eb[] = { new ElementBoundary() };
		eb[0].setKey(new ActionAndElementBoundaryKey("smartspace", "33"));
		eb[0].setElementType("type");
		eb[0].setName(name);
		eb[0].setCreated(new Date());
		eb[0].setCreator(new UserBoundaryKey("smartspace", "crea@tor"));
		eb[0].setLatlng(new LatLng(1.0, 2.3));
		eb[0].setElementProperties(new TreeMap<String, Object>());
		eb[0].setExpired(false);
		this.elementDao.create(eb[0].convertToEntity());
		
		
		// WHEN I export a one elements
		ElementBoundary[] response = this.restTemplate.getForObject(adminUrl, ElementBoundary[].class);
				
		


		// THEN the database contains 1 element
		// the return key is not null
		// AND the returned element is similar to the element in the database
		assertThat(response[0].getKey()).isNotNull();

		assertThat(this.elementDao.readAll()).hasSize(1).usingElementComparatorOnFields("key")
				.containsExactly(response[0].convertToEntity());
	}

	@Test
	public void testUrlAreOk() throws Exception {

		// GIVEN the database is clean

		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;

		// WHEN I post a new element

		ResponseEntity<String> response = restTemplate.getForEntity(adminUrl, String.class);

		// THEN the database contains 1 element
		// make sure the operation was actually successful

		assertThat(response.getStatusCode().equals(HttpStatus.OK));
	}

	@Test
	public void testGetElementsAsAdminUsingPagination() throws Exception {

		// GIVEN the database contains 38 Elements

		int totalSize = 38;
		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;

		List<ElementEntity> all = IntStream.range(1, totalSize + 1).mapToObj(i -> "element" + (char) (i))
				.map(name -> this.factory.createNewElement(name, "Test", new Location(10, 15), new Date(),
						"TestElement#@mail.com", "smartSpace", false, new HashMap<>()))
				.collect(Collectors.toList());

		for (int i = 0; i < totalSize; i++) {
			Date newDate = new Date(i + 300);
			all.get(i).setCreationTimeStamp(newDate);
			all.get(i).setElementSmartspace("smartspace");
			all.get(i).setElementId(i + 100 + "");
			all.get(i).setKey(new ElementKey("smartspace", i + 100 + ""));
		}

		all = this.elementService.importElements(all, adminSmartspace, adminEmail);
		List<ElementBoundary> lastEight = all.stream().skip(30).map(ElementBoundary::new).collect(Collectors.toList());

		// WHEN I get Elements using page #3 of size 10
		int size = 10;
		int page = 3;
		ElementBoundary[] results = this.restTemplate.getForObject(adminUrl + "?size={size}&page={page}",
				ElementBoundary[].class, size, page);

		// THEN the response contains 8 elemnts
		assertThat(results).usingElementComparatorOnFields("key").containsExactlyElementsOf(lastEight);
	}

	@Test
	public void testGetElementsAsAdminUsingPaginationWithNoResult() throws Exception {

		// GIVEN the database contains 27 elements
		// AND I have an admin
		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;
		int totalSize = 27;

		List<ElementEntity> all = IntStream.range(1, totalSize + 1).mapToObj(i -> "element" + (i))
				.map(name -> this.factory.createNewElement(name, "Test", new Location(10, 15), new Date(),
						"TestElement#@mail.com", "smartSpace", false, new HashMap<>()))
				.collect(Collectors.toList());

		for (int i = 0; i < 27; i++) {
			all.get(i).setElementSmartspace("smartspace");
			all.get(i).setElementId(i + 100 + "");
			all.get(i).setKey(new ElementKey("smartspace", i + 100 + ""));
		}

		all = this.elementService.importElements(all, adminSmartspace, adminEmail);

		// WHEN I get Elements using page #3 of size 10
		int size = 10;
		int page = 3;
		ElementBoundary[] results = this.restTemplate.getForObject(adminUrl + "?size={size}&page={page}",
				ElementBoundary[].class, size, page);

		// THEN the response contains no elements
		assertThat(results).isEmpty();
	}

	@Test
	public void testGetElementsUsingPaginationOfFirstPage() {

		// GIVEN the database contains 38 elements

		int totalSize = 38;
		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;

		List<ElementEntity> all = IntStream.range(1, totalSize + 1).mapToObj(i -> "element" + (i))
				.map(name -> this.factory.createNewElement(name, "Test", new Location(10, 15), new Date(),
						"TestElement#@mail.com", "smartSpace", false, new HashMap<>()))
				.collect(Collectors.toList());

		for (int i = 0; i < totalSize; i++) {
			all.get(i).setElementSmartspace("smartspace");
			all.get(i).setElementId(i + 100 + "");
			all.get(i).setKey(new ElementKey("smartspace", i + 100 + ""));
		}

		all = this.elementService.importElements(all, adminSmartspace, adminEmail);
		// WHEN I get element using page #0 of size 100

		int size = 100;
		int page = 0;
		ElementBoundary[] results = this.restTemplate.getForObject(adminUrl + "?size={size}&page={page}",
				ElementBoundary[].class, size, page);

		// THEN the response contains 38 elements
		assertThat(results).hasSize(totalSize);
	}
	
	@Test
	public void GetAllElementsWithExpiredTrueAndFalseUsingPagination() throws Exception {
		//GIVEN I have 10 elements with expired true
		//AND 10 elements with expired false
		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;
		
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
						  this.restTemplate.getForObject(adminUrl + "?size={size}&page={page}", ElementBoundary[].class, 30,0);
							
				
		// THEN the response contains 20 elements	
		assertThat(results).hasSize(20);
	}

}