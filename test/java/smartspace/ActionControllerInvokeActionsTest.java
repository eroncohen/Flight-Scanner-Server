package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import smartspace.dao.AdvancedActionDao;
import smartspace.dao.AdvancedElementDao;
import smartspace.dao.UserDao;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Location;
import smartspace.data.Properties;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;
import smartspace.layout.ActionAndElementBoundaryKey;
import smartspace.layout.ActionBoundary;
import smartspace.layout.ElementBoundary;
import smartspace.layout.LatLng;
import smartspace.layout.UserBoundaryKey;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ActionControllerInvokeActionsTest {
	
	private int port;
	private String baseUrl;
	private RestTemplate restTemplate;
	private AdvancedActionDao actionDao;
	private UserDao<UserKey> userDao;
	private EntityFactory factory;
	private AdvancedElementDao<ElementKey> elementDao;
	
	private UserEntity player;
	private ElementEntity route;
	
	@Autowired
	public void setActionDao(AdvancedActionDao actionDao, AdvancedElementDao<ElementKey> elementDao) {
		this.actionDao = actionDao;
		this.elementDao = elementDao;
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
		this.baseUrl = "http://localhost:" + port + "/smartspace/actions";
		this.restTemplate = new RestTemplate();
	}

	@After
	public void tearDown() {
		this.actionDao.deleteAll();
		this.userDao.deleteAll();
	}
	
	@Before
	public void setUp() {
		
		this.actionDao.deleteAll();
		this.userDao.deleteAll();
		
		HashMap<String, Object> attr = new HashMap<String, Object>();
		attr.put(Properties.IATA, "LHR");
		attr.put(Properties.MUNICIPLAITY,"London");
		ElementEntity airportLo = factory.createNewElement("London Heathrow Airport", "Airport",
				new Location(-0.46, 51.47), new Date(), "mangager@mail.com", "2019b.rickyd", false, attr);
		this.elementDao.create(airportLo);

		attr.put(Properties.IATA, "TLV");
		attr.put(Properties.MUNICIPLAITY,"Tel Aviv");
		ElementEntity airportIs = factory.createNewElement("Ben-Gurion", "Airport",new Location(34.88, 32.01),
				new Date(), "mangager@mail.com", "2019b.rickyd", false, attr);
		this.elementDao.create(airportIs);
		
		attr = new HashMap<String, Object>();
		attr.put("code", "USD");
		attr.put("sign", "$");
		ElementEntity currency = factory.createNewElement("Dollar", Properties.CURRENCY, null,
				new Date(), "mangager@mail.com", "2019b.rickyd", false, attr);
		this.elementDao.create(currency);
		
		attr = new HashMap<String, Object>();
		attr.put("destLoc", new Location(-0.46, 51.47));
		attr.put("origLoc",  new Location(34.88, 32.01));
		
		this.route = factory.createNewElement("TLV_LHR", Properties.ROUTE, new Location(0, 0), new Date(),
				"mangager@mail.com", "2019b.rickyd", false, attr);
		this.route = this.elementDao.create(route);
		
		String playerName = "Player1";
		UserRole playerRole = UserRole.PLAYER;
		String playerEmail = "player@mail.ac.il";
		String playerSmartspace = "2019b.rickyd";
		String playerAvatar = ":@";
		long playerPoints = 2;
		this.player = this.factory.createNewUser(playerEmail, playerSmartspace, playerName, playerAvatar, playerRole,playerPoints);
		this.player = this.userDao.create(player);
		
		
	}

	@Test
	public void testInvokeActionsGetFlights() throws Exception {

		// GIVEN the database has 2 airports 
		// AND 1 route
		// AND 1 player

		
		System.err.println(this.elementDao.readAll());
		// WHEN a player invoke an action with type "RequestFlights"
		ActionBoundary actionBoundary = new ActionBoundary();
		actionBoundary.setType("FlightRequest");
		
		actionBoundary.setPlayer(new UserBoundaryKey("2019b.rickyd", "player@mail.ac.il"));
		
		Map<String, Object> properties = new HashMap<>();
		
		properties.put(Properties.DESTINATION, new LatLng(51.47, -0.46));
		properties.put(Properties.ORIGIN, new LatLng(32.01, 34.88));
		properties.put(Properties.DEPARTURE_DATE, "SUN SEP 01 2019");
		properties.put(Properties.ARRIVAL_DATE, "SUN SEP 10 2019");
		properties.put(Properties.MIN_PRICE, "3000.12");
		properties.put(Properties.CURRENCY, "Dollar");
		actionBoundary.setProperties(properties);
		
		ElementBoundary eb = this.restTemplate.postForObject(this.baseUrl, actionBoundary, ElementBoundary.class);
		
		// THEN the database contains 1 action
		// AND the return key is not null
		// AND the returned actions has type flights
		// AND THE ROUTE element properties doesn't change in the DB
		//System.err.println(eb);
		List<ElementEntity> el = this.elementDao.readElementsByType(Properties.ROUTE, 10, 0);
		assertThat(el.get(0).getMoreAttributes()).hasSize(2);
		assertThat(this.actionDao.readAll()).hasSize(1);
		assertThat(this.actionDao.readAll().get(0)).isNotNull();
		assertThat(eb.getElementType()).isEqualTo(Properties.ROUTE);
	}
	

	@Test
	public void testInvokeActionsSubscribe() throws Exception {

		// GIVEN the database has 2 airports 
		// AND 1 route
		// AND 1 player


		
		// WHEN I invoke an action "subscribe"
		HashMap<String, Object> attr = new HashMap<String, Object>();
		attr.put(Properties.DESTINATION, "LHR");
		attr.put(Properties.ORIGIN, "TLV");
		attr.put(Properties.DEPARTURE_DATE, "SUN SEP 01 2019");
		attr.put(Properties.ARRIVAL_DATE, "SUN SEP 10 2019");
		attr.put(Properties.MIN_PRICE, "3000.12");
		attr.put(Properties.CURRENCY, "Dollar");
		
		ActionBoundary action = new ActionBoundary();
		action.setActionKey(null);
		action.setCreated(new Date());
		action.setElement(new ActionAndElementBoundaryKey(this.route.getKey().getElementSmartspace(),this.route.getKey().getElementId()));
		action.setPlayer(new UserBoundaryKey(this.player.getUserSmartspace(),this.player.getUserEmail()));
		action.setProperties(attr);
		action.setType(Properties.SUBSCRIBE);
		
		this.restTemplate.postForObject(this.baseUrl, action, Object.class);

		// THEN the database contains 1 action
		// AND the return key is not null
		// AND the returned actions is similar to the actions in the database
		// AND has the same key
		
		//System.err.println(this.actionDao.readAll());
		assertThat(this.actionDao.readActionsByType(Properties.SUBSCRIBE, 2, 0)).isNotNull();

		assertThat(this.actionDao.readAll()).hasSize(1);
	}
	
	
	@Test
	public void testInvokeActionsSendMail() throws Exception {

		// GIVEN the database has 2 airports 
		// AND 1 route
		// AND 1 player
		// AND 1 Timer player
		// AND 1 Subscription
		String playerName = "Timer";
		UserRole playerRole = UserRole.PLAYER;
		String playerEmail = "myTimer@Timer";
		String playerSmartspace = "2019b.rickyd";
		String playerAvatar = ":@";
		long playerPoints = 2;
		UserEntity player1 = this.factory.createNewUser(playerEmail, playerSmartspace, playerName, playerAvatar, playerRole,playerPoints);
		this.userDao.create(player1);
		
		HashMap<String, Object> attr = new HashMap<String, Object>();
		attr.put(Properties.DESTINATION, "LHR");
		attr.put(Properties.ORIGIN, "TLV");
		attr.put(Properties.DEPARTURE_DATE, "SUN SEP 01 2019");
		attr.put(Properties.ARRIVAL_DATE, "SUN SEP 10 2019");
		attr.put(Properties.MIN_PRICE, "3000.12");
		attr.put(Properties.CURRENCY, "Dollar");
		
		ActionBoundary subscription = new ActionBoundary();
		subscription.setActionKey(null);
		subscription.setCreated(new Date());
		subscription.setElement(new ActionAndElementBoundaryKey(this.route.getKey().getElementSmartspace(),this.route.getKey().getElementId()));
		subscription.setPlayer(new UserBoundaryKey(this.player.getUserSmartspace(),this.player.getUserEmail()));
		subscription.setProperties(attr);
		subscription.setType(Properties.SUBSCRIBE);
		this.actionDao.create(subscription.convertToEntity());
		// WHEN Timer invoke an action "SendMail"
		// AND there is a cheaper flight
		
		ActionBoundary boundary = new ActionBoundary();
		boundary.setActionKey(null);
		boundary.setPlayer(new UserBoundaryKey("2019b.rickyd", "myTimer@Timer"));
		boundary.setType("cheaperFlightFinder");

		this.restTemplate.postForObject(this.baseUrl, boundary, Object.class);

		// THEN the database contains action with the type Send_Mail
		//System.err.println(this.actionDao.readAll());
		assertThat(this.actionDao.readActionsByType(Properties.SUBSCRIBE, 10, 0)).hasSize(1);
	}
	
}
