package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.AdvancedActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class AdvancedActionDaoTests {

	private AdvancedActionDao actionDao;
	private EntityFactory factory;

	@Autowired
	public void setDao(AdvancedActionDao actionDao) {
		this.actionDao = actionDao;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@After
	public void teardown() {
		this.actionDao.deleteAll();
	}

	@Test
	public void testReadAllWithPagination() throws Exception {
		// GIVEN the database contains only 20 actions
		IntStream.range(0, 20)
				.mapToObj(i -> this.factory.createNewAction(
						"ElementIdTest #" + i,
						"ElementSmartSpaceTest",
						"ActionTypeTest",
						new Date(),
						"PlayerTest#" + i + "@mail.com",
						"PlayerSmartSpaceTest",
						new HashMap<>()))
				.forEach(this.actionDao::create);

		// WHEN I read 3 actions from page 6
		List<ActionEntity> actual = this.actionDao.readAll(3, 6);

		// THEN I receive 2 actions
		assertThat(actual).hasSize(2);
	}
	
	
	@Test
	public void testReadAllWithPaginationAndSortByElementId() throws Exception{
		// GIVEN the database contains only 10 actions 
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewAction(
				"ElementIdTest #" + i,
				"ElementSmartSpaceTest",
				"ActionTypeTest",
				new Date(),
				"PlayerTest#" + i + "@mail.com",
				"PlayerSmartSpaceTest",
				new HashMap<>()))
		.forEach(this.actionDao::create);
		
		// WHEN I read 2 actions from page 3 and sorting by elementId
		List<ActionEntity> actual = this.actionDao.readAll("elementId", 2, 3);
		
		// THEN I receive actions with text containing: "6","7"
		assertThat(actual)
			.usingElementComparatorOnFields("elementId")
			.containsExactly(
					factory.createNewAction("ElementIdTest #6", null, null, null, null, null, null),
					factory.createNewAction("ElementIdTest #7", null, null, null, null, null, null));
	}
	
	
	@Test
	public void testReadAllWithPaginationFromTheStartAndSortByElementId() throws Exception{
		// GIVEN the database contains only 10 actions 
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewAction(
				"ElementIdTest #" + i,
				"ElementSmartSpaceTest",
				"ActionTypeTest",
				new Date(),
				"PlayerTest#" + i + "@mail.com",
				"PlayerSmartSpaceTest",
				new HashMap<>()))
		.forEach(this.actionDao::create);
		
		// WHEN I read 3 actions from page 0 and sorting by elementId
		List<ActionEntity> actual = this.actionDao.readAll("elementId", 3, 0);
		
		// THEN I receive actions with text containing: "0","1","2"
		assertThat(actual)
			.usingElementComparatorOnFields("elementId")
			.containsExactly(
					factory.createNewAction("ElementIdTest #0", null, null, null, null, null, null),
					factory.createNewAction("ElementIdTest #1", null, null, null, null, null, null),
					factory.createNewAction("ElementIdTest #2", null, null, null, null, null, null));
	}
	
	@Test
	public void testReadAllWithCreationTimeStampInRange() throws Exception{
		// GIVEN the database contain 16 actions from yesterday
		// AND the database contain 5 actions from now
		Date yesterday = new Date(System.currentTimeMillis() - 1000*3600*24);
		IntStream.range(5, 5+16)
		.mapToObj(i -> this.factory.createNewAction(
				"ElementIdTest #" + i,
				"ElementSmartSpaceTest",
				"ActionTypeTest",
				yesterday,
				"PlayerTest#" + i + "@mail.com",
				"PlayerSmartSpaceTest",
				new HashMap<>()))
		.forEach(this.actionDao::create);
		
		IntStream.range(0, 5)
		.mapToObj(i -> this.factory.createNewAction(
				"ElementIdTest #" + i,
				"ElementSmartSpaceTest",
				"ActionTypeTest",
				new Date(),
				"PlayerTest#" + i + "@mail.com",
				"PlayerSmartSpaceTest",
				new HashMap<>()))
		.forEach(this.actionDao::create);
		
		// WHEN I read 5 actions created between two days ago and one hour ago with skipping first 3 pages
		Date twoDaysAgo = new Date(System.currentTimeMillis() - 48*3600000);
		Date oneHourAgo = new Date(System.currentTimeMillis() - 3600000);
		List<ActionEntity> list = this.actionDao
				.readActionsWithCreationTimeStampInRange(
						twoDaysAgo, 
						oneHourAgo,//oneHourAgo, 
						5, 3);
		// THEN I receive 1 ACTION
		assertThat(list)
			.hasSize(1);
	}
	
	@Test
	public void testReadActionsByType() throws Exception{
		String type = "subscribe";
		// GIVEN the database contains 20 actions 
		// AND the first two actions has the type subscribe
		IntStream.range(0, 2)
		.mapToObj(i -> this.factory.createNewAction(
				"ElementIdTest #" + i,
				"ElementSmartSpaceTest",
				type,
				new Date(),
				"PlayerTest#" + i + "@mail.com",
				"PlayerSmartSpaceTest",
				new HashMap<>()))
		.forEach(this.actionDao::create);
		
		IntStream.range(2,2+18)
		.mapToObj(i -> this.factory.createNewAction(
				"ElementIdTest #" + i,
				"ElementSmartSpaceTest",
				"ActionTypeTest",
				new Date(),
				"PlayerTest#" + i + "@mail.com",
				"PlayerSmartSpaceTest",
				new HashMap<>()))
		.forEach(this.actionDao::create);
		
		// WHEN I read the first 3 actions by type
		List<ActionEntity> result = this.actionDao.readActionsByType(type, 3, 0);
		
		// THEN we receive 2 actions 
		// AND The elements has "subscribe" as type
		assertThat(result)
			.hasSize(2);
		
		assertThat(result.get(0).getType())
			.contains(type);
		assertThat(result.get(1).getType())
		.contains(type);

	}
	
	@Test
	public void testReadActionsByPlayerEmail() throws Exception{
		String email = "Ricky@mail.ac.il";
		// GIVEN the database contains 20 ACTIONS 
		// AND the first two ACTIONS has the player email "Ricky@mail.ac.il"
		IntStream.range(0,2)
		.mapToObj(i -> this.factory.createNewAction(
				"ElementIdTest #" + i,
				"ElementSmartSpaceTest",
				"ActionTypeTest",
				new Date(),
				email,
				"PlayerSmartSpaceTest",
				new HashMap<>()))
		.forEach(this.actionDao::create);
		
		
		IntStream.range(2,2+18)
		.mapToObj(i -> this.factory.createNewAction(
				"ElementIdTest #" + i,
				"ElementSmartSpaceTest",
				"ActionTypeTest",
				new Date(),
				"PlayerTest#" + i + "@mail.com",
				"PlayerSmartSpaceTest",
				new HashMap<>()))
		.forEach(this.actionDao::create);
		
		// WHEN I read the first 3 actions by player email 
		List<ActionEntity> result = this.actionDao.readActionsByPlayerEmail(email, 3, 0);
		
		// THEN we receive 2 actions 
		// AND The actions has "Ricky@mail.ac.il" as player email
		assertThat(result)
			.hasSize(2);
		
		assertThat(result.get(0).getPlayerEmail())
			.contains(email);
		assertThat(result.get(1).getPlayerEmail())
		.contains(email);

	}
	
	
	
}
