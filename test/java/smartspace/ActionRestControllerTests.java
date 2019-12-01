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

import smartspace.dao.ActionDao;
import smartspace.dao.UserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ActionKey;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;
import smartspace.layout.ActionBoundary;
import smartspace.layout.ActionAndElementBoundaryKey;
import smartspace.layout.UserBoundaryKey;
import smartspace.logic.ActionService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ActionRestControllerTests {
	private int port;
	private String baseUrl;
	private RestTemplate restTemplate;
	private ActionDao actionDao;
	private UserDao<UserKey> userDao;
	private ActionService actionService;
	private EntityFactory factory;

	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}

	@Autowired
	public void setActioService(ActionService actionService) {
		this.actionService = actionService;
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
		this.baseUrl = "http://localhost:" + port + "/smartspace/admin/actions";
		this.restTemplate = new RestTemplate();
	}

	@Before
	public void setUp() {
		UserEntity admin = this.factory.createNewUser("Admin@istator.test", 
				"2019b.rickyd", "Tester", "8^)",
				UserRole.ADMIN, 9999);
		this.userDao.create(admin);
	}

	@After
	public void tearDown() {
		this.actionDao.deleteAll();
		this.userDao.deleteAll();
	}

	@Test
	public void testImportActions() throws Exception {

		// GIVEN the database has an admin

		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;
		
		// WHEN I post an action as an admin
		ActionBoundary actions[] = {new ActionBoundary()};
		
		actions[0].setActionKey(new ActionAndElementBoundaryKey("smartspace", "33"));
		actions[0].setType("type");
		actions[0].setCreated(new Date());
		actions[0].setElement(new ActionAndElementBoundaryKey("smartspace", "33"));
		actions[0].setPlayer(new UserBoundaryKey("smartspace", "33@mail"));
		actions[0].setProperties(new TreeMap<String, Object>());
		

		ActionBoundary[] response = this.restTemplate.postForObject(
				adminUrl, actions, ActionBoundary[].class);

		// THEN the database contains 1 action
		// AND the return key is not null
		// AND the returned actions is similar to the actions in the database
		// AND has the same key
		assertThat(response[0].getActionKey()).isNotNull();

		assertThat(this.actionDao.readAll()).hasSize(1).usingElementComparatorOnFields("key")
				.containsExactly(response[0].convertToEntity());
		
		assertThat(this.actionDao.readAll().get(0).getKey()).isEqualTo(response[0].convertToEntity().getKey());
	}
	
	
	@Test
	public void testExportActions() throws Exception {

		// GIVEN the database has an admin

		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;
		
		ActionBoundary actions[] = {new ActionBoundary()};
		
		actions[0].setActionKey(new ActionAndElementBoundaryKey("smartspace", "33"));
		actions[0].setType("type");
		actions[0].setCreated(new Date());
		actions[0].setElement(new ActionAndElementBoundaryKey("smartspace", "33"));
		actions[0].setPlayer(new UserBoundaryKey("smartspace", "33@mail"));
		actions[0].setProperties(new TreeMap<String, Object>());

		this.actionDao.create(actions[0].convertToEntity());
		
		// WHEN I export an action as an admin
		
		ActionBoundary[] response = this.restTemplate.getForObject(adminUrl, ActionBoundary[].class);
		

		// THEN the database contains 1 action
		// AND the return key is not null
		// AND the returned actions is similar to the actions in the database
		// AND has the same key

		assertThat(response[0].getActionKey()).isNotNull();

		assertThat(this.actionDao.readAll()).hasSize(1).usingElementComparatorOnFields("key")
				.containsExactly(response[0].convertToEntity());
		
		assertThat(this.actionDao.readAll().get(0).getKey()).isEqualTo(response[0].convertToEntity().getKey());
	}
	
	
	
	
	@Test
	public void testUrlAreOk() throws Exception {

		// GIVEN the database is clean

		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;

		// WHEN I post a new action

		ResponseEntity<String> response = restTemplate.getForEntity(adminUrl, String.class);

		// THEN the database contains 1 action
		// make sure the operation was actually successful

		assertThat(response.getStatusCode().equals(HttpStatus.OK));
	}
	
	@Test
	public void testGetActionsUsingPagination() throws Exception {

		// GIVEN the database contains 38 actions

		int totalSize = 38;
		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;

		List<ActionEntity> all = IntStream.range(1, totalSize + 1)
				.mapToObj(i -> this.factory.createNewAction(
						"ElementIdTest #" + i,
						"ElementSmartSpaceTest",
						"ActionTypeTest",
						new Date(),
						"PlayerTest#" + i + "@mail.com",
						"PlayerSmartSpaceTest",
						new HashMap<>()))
				.collect(Collectors.toList());

		for (int i = 0; i < totalSize; i++) {
			Date newDate = new Date(i + 300);
			all.get(i).setCreationTimeStamp(newDate);
			all.get(i).setActionSmartspace("smartspace");
			all.get(i).setActionId(i + 100 + "");
			all.get(i).setKey(new ActionKey("smartspace", i + 100 + ""));
		}

		all = this.actionService.importActions(all,adminSmartspace,adminEmail);
		List<ActionBoundary> lastEight = all.stream().skip(30).map(ActionBoundary::new).collect(Collectors.toList());

		// WHEN I get actions using page #3 of size 10
		int size = 10;
		int page = 3;
		ActionBoundary[] results = this.restTemplate.getForObject(adminUrl + "?size={size}&page={page}",
				ActionBoundary[].class, size, page);

		// THEN the response contains 8 actions
		assertThat(results).usingElementComparatorOnFields("actionKey").containsExactlyElementsOf(lastEight);
	}

	@Test
	public void testGetActionsUsingPaginationWithNoResult() throws Exception {

		// GIVEN the database contains 27 actions
		// AND I have an admin
		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;
		int totalSize = 27;

		List<ActionEntity> all = IntStream.range(1, totalSize+1)
				.mapToObj(i -> this.factory.createNewAction(
						"ElementIdTest #" + i,
						"ElementSmartSpaceTest",
						"ActionTypeTest",
						new Date(),
						"PlayerTest#" + i + "@mail.com",
						"PlayerSmartSpaceTest",
						new HashMap<>()))
				.collect(Collectors.toList());

		for (int i = 0; i < 27; i++) {
		
			all.get(i).setActionSmartspace("smartspace");
			all.get(i).setActionId(i + 100 + "");
			all.get(i).setKey(new ActionKey("smartspace", i + 100 + ""));
		}

		all = this.actionService.importActions(all,adminSmartspace,adminEmail);

		// WHEN I get actions using page #3 of size 10
		int size = 10;
		int page = 3;
		ActionBoundary[] results = this.restTemplate.getForObject(adminUrl + "?size={size}&page={page}",
				ActionBoundary[].class, size, page);

		// THEN the response contains no actions
		assertThat(results).isEmpty();
	}

	@Test
	public void testGetActionsUsingPaginationOfFirstPage() {

		// GIVEN the database contains 38 actions

		int totalSize = 38;
		String adminEmail = "Admin@istator.test";
		String adminSmartspace = "2019b.rickyd";
		String adminUrl = this.baseUrl + "/" + adminSmartspace + "/" + adminEmail;

		List<ActionEntity> all = IntStream.range(1, totalSize+1)
				.mapToObj(i -> this.factory.createNewAction(
						"ElementIdTest #" + i,
						"ElementSmartSpaceTest",
						"ActionTypeTest",
						new Date(),
						"PlayerTest#" + i + "@mail.com",
						"PlayerSmartSpaceTest",
						new HashMap<>()))
				.collect(Collectors.toList());

		for (int i = 0; i < totalSize; i++) {
			all.get(i).setActionSmartspace("smartspace");
			all.get(i).setActionId(i + 100 + "");
			all.get(i).setKey(new ActionKey("smartspace", i + 100 + ""));
		}

		all = this.actionService.importActions(all,adminSmartspace,adminEmail);

		// WHEN I get action using page #0 of size 100

		int size = 100;
		int page = 0;
		ActionBoundary[] results = this.restTemplate.getForObject(adminUrl + "?size={size}&page={page}",
				ActionBoundary[].class, size, page);

		// THEN the response contains 38 actions
		assertThat(results).hasSize(totalSize);
	}
	
	

}