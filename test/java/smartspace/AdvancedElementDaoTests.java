package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import java.util.Date;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.AdvancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.util.EntityFactory;
import smartspace.data.Location;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class AdvancedElementDaoTests {

	private AdvancedElementDao<ElementKey> elementDao;
	private EntityFactory factory;

	@Autowired
	public void setDao(AdvancedElementDao<ElementKey> elementDao) {
		this.elementDao = elementDao;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@After
	public void teardown() {
		this.elementDao.deleteAll();
	}

	@Test
	public void testReadAllWithPagination() throws Exception {
		String smartSpace = "2019b.rickyd";
		// GIVEN the database contains only 20 elements
		IntStream.range(0, 20)
				.mapToObj(i -> this.factory.createNewElement(
						"Test #" + i
						, "Test"
						, new Location(i,i+1)
						, new Date()
						,"TestElement#" + i + "@mail.com"
						, smartSpace
						, false
						, new HashMap<>()))
				.forEach(this.elementDao::create);

		// WHEN I read 3 elements from page 6
		List<ElementEntity> actual = this.elementDao.readAll(3, 6);

		// THEN I receive 2 elements
		assertThat(actual).hasSize(2);
	}
	
	
	@Test
	public void testReadAllWithPaginationAndSortByName() throws Exception{
		String smartSpace = "2019b.rickyd";
		// GIVEN the database contains only 10 elements 
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		// WHEN I read 2 elements from page 3 and sorting by name
		List<ElementEntity> actual = this.elementDao.readAll("name", 2, 3);
		
		// THEN I receive elements with text containing: "6","7"
		assertThat(actual)
			.usingElementComparatorOnFields("name")
			.containsExactly(
					factory.createNewElement("Test #6", null, null, null, null, null, false, null),
					factory.createNewElement("Test #7", null, null, null, null, null, false, null));
	}
	
	
	@Test
	public void testReadAllWithPaginationFromTheStartAndSortByName() throws Exception{
		String smartSpace = "2019b.rickyd";
		// GIVEN the database contains only 10 elements 
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		// WHEN I read 3 elements from page 0 and sorting by name
		List<ElementEntity> actual = this.elementDao.readAll("name", 3, 0);
		
		// THEN I receive elements with text containing: "0","1","2"
		assertThat(actual)
			.usingElementComparatorOnFields("name")
			.containsExactly(
					factory.createNewElement("Test #0", null, null, null, null, null, false, null),
					factory.createNewElement("Test #1", null, null, null, null, null, false, null),
					factory.createNewElement("Test #2", null, null, null, null, null, false, null));
	}
	
	@Test
	public void testReadAllWithcreationTimeStampInRange() throws Exception{
		String smartSpace = "2019b.rickyd";
		// GIVEN the database contain 16 elements from yesterday
		// AND the database contain 5 elements from now
		Date yesterday = new Date(System.currentTimeMillis() - 1000*3600*24);
		IntStream.range(5, 5+16)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, yesterday
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);

		IntStream.range(0, 5)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		// WHEN I read 5 elements created between two days ago and one hour ago with skipping first 3 pages
		Date twoDaysAgo = new Date(System.currentTimeMillis() - 48*3600000);
		Date oneHourAgo = new Date(System.currentTimeMillis() - 3600000);
		List<ElementEntity> list = this.elementDao
				.readElementsWithcreationTimeStampInRange(
						twoDaysAgo, 
						oneHourAgo,//oneHourAgo, 
						5, 3);
		// THEN I receive 1 element
		assertThat(list)
			.hasSize(1);
	}
	
	@Test
	public void testreadElementsByType() throws Exception{
		String smartSpace = "2019b.rickyd";
		String type = "flight";
		// GIVEN the database contains 20 elements 
		// AND the first two elements has the type flight
		IntStream.range(0,2)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, type
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		IntStream.range(2,2+18)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		// WHEN I read the first 3 elements by type
		List<ElementEntity> result = this.elementDao.readElementsByType(type, 3, 0);
		
		// THEN we receive 2 elements 
		// AND The elements has "flight" as type
		assertThat(result)
			.hasSize(2);
		
		assertThat(result.get(0).getType())
			.contains(type);
		assertThat(result.get(1).getType())
		.contains(type);

	}
	
	@Test
	public void testreadElementsByCreatorEmail() throws Exception{
		String smartSpace = "2019b.rickyd";
		String email = "Ricky@mail.ac.il";
		// GIVEN the database contains 20 elements 
		// AND the first two elemnts has the creator email "Ricky@mail.ac.il"
		IntStream.range(0,2)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,email
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		IntStream.range(2,2+18)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		// WHEN I read the first 3 elements by creator email 
		List<ElementEntity> result = this.elementDao.readElementsByCreatorEmail(email, 3, 0);
		
		// THEN we receive 2 elements 
		// AND The elements has "Ricky@mail.ac.il" as mail
		assertThat(result)
			.hasSize(2);
		
		assertThat(result.get(0).getCreatorEmail())
			.contains(email);
		assertThat(result.get(1).getCreatorEmail())
		.contains(email);

	}
	
	
	@Test
	public void testreadElementsByCreatorSmartspace() throws Exception{
		String smartSpace = "2019b.rickyd";
		// GIVEN the database contains 20 elements 
		// AND the first two elements has "2019B.rickyd" as smartspace creator
		IntStream.range(0,2)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		IntStream.range(2,2+18)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, "TestElement.smartspace"
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		// WHEN I read the first 3 elements by creatorSmartSpace
		List<ElementEntity> result = this.elementDao.readElementsByCreatorSmartspace(smartSpace, 3, 0);

		// THEN we receive 2 elements 
		// AND The elements has "2019B.rickyd" as smartspace creator
		assertThat(result)
			.hasSize(2);
		
		assertThat(result.get(0).getCreatorSmartspace())
			.contains(smartSpace);
		assertThat(result.get(1).getElementSmartspace())
		.contains(smartSpace);

	}
	
	@Test
	public void testreadElementsByName() throws Exception{
		String smartSpace = "2019b.rickyd";

		String name = "name Test";
		// GIVEN the database contains 20 elements 
		// AND the first two elements has the name name test 
		IntStream.range(0,2)
		.mapToObj(i -> this.factory.createNewElement(
				name + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		IntStream.range(2,2+18)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i,i+1)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		// WHEN I read the first 3 elements by name
		List<ElementEntity> result = this.elementDao.readElementsByName("name", 3, 0);
		
		// THEN we receive 2 elements 
		// AND The elements has the name name test int them
		assertThat(result)
			.hasSize(2);
		
		assertThat(result.get(0).getName())
			.contains(name+0+"");
		assertThat(result.get(1).getName())
		.contains(name+1+"");

	}
	@Test
	public void testReadAllWithPaginationNearByLocation() throws Exception{
		String smartSpace = "2019b.rickyd";
		// GIVEN the database contains only 10 elements 
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewElement(
				"Test #" + i
				, "Test"
				, new Location(i+10,i+10)
				, new Date()
				,"TestElement#" + i + "@mail.com"
				, smartSpace
				, false
				, new HashMap<>()))
		.forEach(this.elementDao::create);
		
		// WHEN I read 3 elements from page 0 and sorting by name
		List<ElementEntity> actual = this.elementDao.readElementsByXBetweenAndYBetween(13, 13, 2, 10, 0);
		// THEN I receive elements with text containing: "0","1","2"
		assertThat(actual)
			.hasSize(5);
	}

}
