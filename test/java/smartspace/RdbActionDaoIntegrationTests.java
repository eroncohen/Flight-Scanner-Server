package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.ActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class RdbActionDaoIntegrationTests {

	private ActionDao actionDao;
	private EntityFactory factory;


	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}


	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@Before
	public void setup() {
		this.actionDao.deleteAll();
	}


	@After
	public void teardown() {
		this.actionDao.deleteAll();
	}
	
	public Map<String, Object> createAttributesMap(int numOfAttributes) {
		
		Map<String, Object> moreAttributes = new HashMap<String, Object>();
		//keys list
		List<String> keysList=IntStream.range(0, numOfAttributes)
				.mapToObj(num-> "key_"+num)
				.collect(Collectors.toList());
		//values list
		List<Object> valuesList=IntStream.range(0, numOfAttributes)
				.mapToObj(num-> "value_"+num)
				.collect(Collectors.toList());
		//map
		for(int i=0;i<numOfAttributes;i++) {
			moreAttributes.put(keysList.get(i),valuesList.get(i));
		}
	
		return moreAttributes;
		
	}
	
	
	@Test
	public void createManyActions() throws Exception {

		// GIVEN the database is clean

		// WHEN we create multiple actions and store it in DB
		int numberOfActions = 2;
		int numberOfAdditionalAttributes = 3;
		
		
		List<ActionEntity> listOfActions = IntStream.range(0, numberOfActions)// int stream
				.mapToObj(num -> this.factory.createNewAction("elementId_" + num, "elementSmartspace", "actionType",
						new Date(), "player_" + num + "@gmail.com", "player_" + num + "Smartspace", createAttributesMap(numberOfAdditionalAttributes)))// ActionEntity
																													// map
				.map(action -> this.actionDao.create(action)) // ActionEntity map
				.collect(Collectors.toList());

		// THEN the actions are stored
		assertThat(this.actionDao.readAll())
		.hasSize(numberOfActions)
		.usingElementComparatorOnFields("key")
		.containsAll(listOfActions);

	}
	
	@Test
	public void testCreateDeleteAllReadAll() throws Exception {
	
		// GIVEN we have a clear database
		// AND we have a factory

		// WHEN I create an action
		// AND I delete all
		// AND I read all
		ActionEntity actionEntity = this.factory.createNewAction("elementId", "elementSmartspace", "actionType",
				new Date(), "playerEmail", "playerSmartspace", new HashMap<>());
		actionDao.create(actionEntity);
		ArrayList<ActionEntity> dbAfterCreate = new ArrayList<ActionEntity>(actionDao.readAll());

		this.actionDao.deleteAll();
		ArrayList<ActionEntity> dbAfterDelete = new ArrayList<ActionEntity>(actionDao.readAll());

		// THEN dbAfterCreate contains the action
		// AND dbAfterDelete is empty
		assertThat(dbAfterCreate).usingElementComparatorOnFields("key").contains(actionEntity);

		assertThat(dbAfterDelete).isEmpty();
	}
	

}
