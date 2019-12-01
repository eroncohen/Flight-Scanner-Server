package smartspace;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import smartspace.dao.ActionDao;
import smartspace.dao.ElementDao;
import smartspace.dao.UserDao;
import smartspace.data.ElementKey;
import smartspace.data.Location;
import smartspace.data.Properties;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

//@Profile("production")
//@Component
public class InitializeDataBase implements CommandLineRunner {

	private EntityFactory factory;
	private UserDao<UserKey> userDao;
	private ActionDao actionDao;
	private ElementDao<ElementKey> elementDao;

	public EntityFactory getFactory() {
		return factory;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	public UserDao<UserKey> getUserDao() {
		return userDao;
	}

	@Autowired
	public void setUserDao(UserDao<UserKey> userDao) {
		this.userDao = userDao;
	}

	public ActionDao getActionDao() {
		return actionDao;
	}

	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}

	public ElementDao<ElementKey> getElementDao() {
		return elementDao;
	}

	@Autowired
	public void setElementDao(ElementDao<ElementKey> elementDao) {
		this.elementDao = elementDao;
	}

	public void createForeignUsers() {
		String smartspace = "foreign_smartspace";
		for (int i = 1; i < 3; i++) {
			// admin
			UserRole admin = UserRole.ADMIN;
			String adminName = "foreign_" + admin.toString().toLowerCase() + i;
			String adminEmail = adminName + "@gmail.com";
			String adminAvater = adminName + "_avatar";
			UserEntity adminUser = this.factory.createNewUser(adminEmail, smartspace, adminName, adminAvater, admin,
					9000);
			userDao.create(adminUser);

			// manager
			UserRole manager = UserRole.MANAGER;
			String managerName = "foreign_" + manager.toString().toLowerCase() + i;
			String managerEmail = managerName + "@gmail.com";
			String managerAvater = managerName + "_avatar";
			UserEntity managerUser = this.factory.createNewUser(managerEmail, smartspace, managerName, managerAvater,
					manager, 500);
			userDao.create(managerUser);

			// player
			UserRole player = UserRole.PLAYER;
			String playerName = "foreign_" + player.toString().toLowerCase() + i;
			String playerEmail = playerName + "@gmail.com";
			String playerAvater = playerName + "_avatar";
			UserEntity playerUser = this.factory.createNewUser(playerEmail, smartspace, playerName, playerAvater,
					player, 5);
			userDao.create(playerUser);

		}
	}

	public void createUsers() {
		String smartspace = "2019b.rickyd";
		for (int i = 1; i < 3; i++) {
			// admin
			UserRole admin = UserRole.ADMIN;
			String adminName = admin.toString().toLowerCase() + i;
			String adminEmail = adminName + "@gmail.com";
			String adminAvater = adminName + "_avatar";
			UserEntity adminUser = this.factory.createNewUser(adminEmail, smartspace, adminName, adminAvater, admin,
					9000);
			userDao.create(adminUser);

			// manager
			UserRole manager = UserRole.MANAGER;
			String managerName = manager.toString().toLowerCase() + i;
			String managerEmail = managerName + "@gmail.com";
			String managerAvater = managerName + "_avatar";
			UserEntity managerUser = this.factory.createNewUser(managerEmail, smartspace, managerName, managerAvater,
					manager, 500);
			userDao.create(managerUser);

			// player
			UserRole player = UserRole.PLAYER;
			String playerName = player.toString().toLowerCase() + i;
			String playerEmail = playerName + "@gmail.com";
			String playerAvater = playerName + "_avatar";
			UserEntity playerUser = this.factory.createNewUser(playerEmail, smartspace, playerName, playerAvater,
					player, 5);
			userDao.create(playerUser);

		}
	}

	public void createElements() {
		Date creationTimeStamp = new Date();
		String creatorEmail = "manager1@gmail.com";
		String creatorSmartspace = "2019b.rickyd";
		boolean expired = false;
		// add all routes
		// "CDG->LHR"
		HashMap<String, Object> atrr = new HashMap<String, Object>();
		atrr.put("origLoc", new Location(2.55, 49.01));
		atrr.put("destLoc", new Location(-0.46, 51.47));
		String typeRoute = "route";
		elementDao.create(this.factory.createNewElement("CDG_LHR", typeRoute, new Location(0, 0), creationTimeStamp,
				creatorEmail, creatorSmartspace, expired, atrr));
		// LHR->CDG
		atrr = new HashMap<String, Object>();
		atrr.put("origLoc", new Location(-0.46, 51.47));
		atrr.put("destLoc", new Location(2.55,49.01 ));
		elementDao.create(this.factory.createNewElement("LHR_CDG", typeRoute, new Location(0, 0), creationTimeStamp,
				creatorEmail, creatorSmartspace, expired, atrr));
		// CDG->TLV
		atrr = new HashMap<String, Object>();
		atrr.put("origLoc", new Location( 2.55,49.01));
		atrr.put("destLoc", new Location(34.88, 32.01));
		elementDao.create(this.factory.createNewElement("CDG_TLV", typeRoute, new Location(0, 0), creationTimeStamp,
				creatorEmail, creatorSmartspace, expired, atrr));

		// LHR->TLV
		atrr = new HashMap<String, Object>();
		atrr.put("origLoc", new Location(-0.46, 51.47));
		atrr.put("destLoc", new Location(34.88, 32.01));
		elementDao.create(this.factory.createNewElement("LHR_TLV", typeRoute, new Location(0, 0), creationTimeStamp,
				creatorEmail, creatorSmartspace, expired, atrr));

		System.err.println("finish elements");

	}

	public void createForeignElements() {

		int amount = 3;
		for (int i = 1; i < amount + 1; i++) {
			String name = "foreign_name" + i;
			String type = "foreign_type";
			Location location = new Location(i, i);
			Date creationTimeStamp = new Date();
			String creatorEmail = "foreign_manager1@gmail.com";
			String creatorSmartspace = "foreign_smartspace";
			boolean expired = false;
			Map<String, Object> moreAttributes = new HashMap<String, Object>();
			elementDao.create(this.factory.createNewElement(name, type, location, creationTimeStamp, creatorEmail,
					creatorSmartspace, expired, moreAttributes));

		}
	}

	public void createActions() {

		// FlightRequest
		Map<String, Object> moreAttributes = new HashMap<String, Object>();
		Map<String, Object> origin = new HashMap<String, Object>();
		Map<String, Object> destination = new HashMap<String, Object>();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String departureDateString = "2019-07-15";
		String arrivalDateString = "2019-07-25";
		Date departureDate = null;
		Date arrivaleDate = null;

		try {
			departureDate = formatter.parse(departureDateString);
			arrivaleDate = formatter.parse(departureDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		origin.put("lat", 50);
		origin.put("lng", 50);
		destination.put("lat", 100);
		destination.put("lng", 100);

		moreAttributes.put(Properties.ORIGIN, origin);
		moreAttributes.put(Properties.DESTINATION, destination);
		moreAttributes.put(Properties.DEPARTURE_DATE, departureDate);
		moreAttributes.put(Properties.ARRIVAL_DATE, arrivaleDate);

		// actionDao.create(this.factory.createNewAction("10", "2019b.rickyd",
		// "FlightRequest", new Date(),
		// "player1@gmail.com", "2019b.rickyd", moreAttributes));

		// Subscription
		Map<String, Object> moreAttributes2 = new HashMap<String, Object>();
		moreAttributes2.put(Properties.DEPARTURE_DATE, departureDateString);
		moreAttributes2.put(Properties.ARRIVAL_DATE, arrivalDateString);

		actionDao.create(this.factory.createNewAction("1461", "2019b.rickyd", "Subscription", new Date(),
				"player1@gmail.com", "2019b.rickyd", moreAttributes2));

		System.err.println("finish action");
	}

	@Override
	public void run(String... args) throws Exception {

		// createUsers();
		// createForeignUsers();
		// createForeignElements();
		createElements();
		//createActions();

	}

}
