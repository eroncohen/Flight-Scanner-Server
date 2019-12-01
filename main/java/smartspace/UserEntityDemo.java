package smartspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import smartspace.dao.UserDao;

import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;
//@Profile("production")
//@Component
public class UserEntityDemo implements CommandLineRunner {

	private EntityFactory factory;
	private UserDao<UserKey> userDao;

	@Autowired
	public UserEntityDemo(EntityFactory factory, UserDao<UserKey> userDao) {
		super();
		this.factory = factory;
		this.userDao = userDao;
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n\n ----------------  User Entity Start: -------------------- \n");
		String username = "Player1";
		UserRole ur = UserRole.PLAYER;
		String userEmail = "eronc@mail.afeka.ac.il";
		String userSmartspace = "2019b.rickyd";
		String avatar = ":)";
		long point = 1;
		String username1 = "Manager";
		UserRole ur1 = UserRole.MANAGER;
		String userEmail1 = "rickyyy44@gmail.com";
		String avatar1 = ":')";
		long point1 = 2;
		UserEntity player1 = factory.createNewUser( userEmail,userSmartspace, username, avatar, ur, point);
		System.err.println("new user:" + player1 + "\n");
		player1 = this.userDao.create(player1);
		System.err.println("stored user:" + player1 + "\n");
		UserEntity managerDemo = factory.createNewUser( userEmail1,userSmartspace, username1, avatar1, ur1, point1);
		System.err.println("new user:" + managerDemo + "\n");
		managerDemo = this.userDao.create(managerDemo);
		System.err.println("stored user:" + managerDemo + "\n");
		this.userDao.deleteAll();
		if (this.userDao.readAll().isEmpty()) {
			System.err.println("\nSuccessfully deleted all users");
		
		} else {
			throw new RuntimeException("Error! there is a user in the memory after deletion");
		}
		System.out.println("\n ----------------  User Entity End -------------------- \n\n");
		
		
		String adminName = "Admin";
		UserRole role = UserRole.ADMIN;
		String adminEmail = "Admin@istator.com";
		String adminSmartspace = "2019b.rickyd";
		String adminAvatar = "8^)";
		long points = 9999;
		UserEntity admin = this.factory.createNewUser(adminEmail, adminSmartspace, adminName, adminAvatar, role, points);
		admin = this.userDao.create(admin);
		
		System.err.println("Admin Created:");
		System.err.println(admin);
		
		String managerName = "Manager";
		UserRole managerRole = UserRole.MANAGER;
		String managerEmail = "mangager@mail.com";
		String managerSmartspace = "2019b.rickyd";
		String managerAvatar = ">8^)";
		long managerPoints = 9999;
		UserEntity manager = this.factory.createNewUser(managerEmail, managerSmartspace, managerName, managerAvatar, managerRole, managerPoints);
		admin = this.userDao.create(manager);
		
		System.err.println("Manager Created:");
		System.err.println(manager);
		
		String playerName = "Player1";
		UserRole playerRole = UserRole.PLAYER;
		String playerEmail = "player@mail.ac.il";
		String playerSmartspace = "2019b.rickyd";
		String playerAvatar = ":@";
		long playerPoints = 2;
		UserEntity player = this.factory.createNewUser(playerEmail, playerSmartspace, playerName, playerAvatar, playerRole,playerPoints);
		admin = this.userDao.create(player);
		
		System.err.println("Player Created:");
		System.err.println(player);
		
		playerName = "Timer";
		playerRole = UserRole.PLAYER;
		playerEmail = "myTimer@Timer";
		playerSmartspace = "2019b.rickyd";
		playerAvatar = "(-__-)";
		playerPoints = 0;
		player = this.factory.createNewUser(playerEmail, playerSmartspace, playerName, playerAvatar, playerRole,playerPoints);
		player = this.userDao.create(player);
		System.err.println("Player Created:");
		System.err.println(player);
		
	}

}