package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.ActionDao;
import smartspace.dao.ElementDao;
import smartspace.dao.UserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ActionKey;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.UserRole;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class EntityKeyGenerationTests {

	private ActionDao actionDao;
	private ElementDao<ElementKey> elementDao;
	private UserDao<UserKey> userDao;
	private EntityFactory factory;

	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}

	@Autowired
	public void setElementDao(ElementDao<ElementKey> elementDao) {
		this.elementDao = elementDao;
	}

	@Autowired
	public void setUserDao(UserDao<UserKey> userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@After
	public void teardown() {
		this.actionDao.deleteAll();
		this.elementDao.deleteAll();
		this.userDao.deleteAll();
	}

	@Test
	public void testCreateElementAndVerifyUniqueKeys() throws Exception {
		// GIVEN clean element database

		// WHEN I create 20 element entities
		int size = 20;
		Set<ElementKey> keysSet = IntStream.range(1, size + 1)//Integer Stream
		.mapToObj(i->this.factory.createNewElement(
				"Test#"+i
				, "Test"
				,new Location(i,i)
				, new Date()
				, i+".test@mail.afeka.ac.il"
				,"smartspace"
				, false
				, new HashMap<>()))//Element stream
		.map(this.elementDao::create)//Element stream
		.map(ElementEntity::getKey)//Key<String> stream
		.collect(Collectors.toSet());//to set
				
		
		// THEN they all have unique keys
		assertThat(keysSet).hasSize(size);
	}

	@Test
	public void testCreateActionAndVerifyUniqueKeys() throws Exception {
		// GIVEN clean database

		// WHEN I create 20 messages
		int size = 20;
		Set<ActionKey> keysSet = IntStream.range(1, size + 1) // Integer Stream
				.mapToObj(i -> this.factory.createNewAction(
						"test#"+i
						, "smartspace"
						, "test"
						, new Date()
						, "test."+i+"@mail.ac.il"
						, "smartspace"
						, new HashMap<>()))
				.map(this.actionDao::create)// ActionEntity stream
				.map(ActionEntity::getKey) // key<String> Stream
				.collect(Collectors.toSet());//to set
		
		// THEN they all have unique keys
		assertThat(keysSet).hasSize(size);
	}

	@Test
	public void testCreateUserAndVerifyUniqueKeys() throws Exception {
		// GIVEN clean database

		// WHEN I create 20 messages
		int size = 20;
		Set<UserKey> keysSet = IntStream.range(1, size + 1) // Integer Stream
				.mapToObj(i -> this.factory.createNewUser(
						"userTest."+i+"@mail.ac.il"
						, "smartSpace"
						, "dummy #"+ i
						, "¯\\_(*-*)_/¯"
						, UserRole.PLAYER
						, i))
				.map(this.userDao::create)// UserEntity Stream
				.map(UserEntity::getKey) // key<String> Stream
				.collect(Collectors.toSet());//to set

		// THEN they all have unique keys
		assertThat(keysSet).hasSize(size);
	}

}
