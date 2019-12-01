package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class RdbElementDaoIntegrationTest {
	private ElementDao<ElementKey> dao;
	private EntityFactory factory;
	
	@Autowired
	public void setDao(ElementDao<ElementKey> dao) {
		this.dao = dao;
	}
	
	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@After
	public void teardown() {
		this.dao.deleteAll();
	}
	
	@Test
	public void createSimpleElement() throws Exception {
		// GIVEN the db is clean
		
		// WHEN we create a new element and store it in DB
		String name = "Element1";
		ElementEntity newElement = this.factory.createNewElement(name, "flight", new Location(3,3), new Date(), 
				"eronc@mail.afeka.ac.il", "2019B.rickyd.smartspace.element", false, new HashMap<String, Object>());
		ElementEntity actual = this.dao.create(newElement);
		// THEN the element is stored
		assertThat(this.dao.readById(actual.getKey()))
			.isPresent()
			.get()
			.extracting("key","name")
			.containsExactly(actual.getKey(),name);
	}
	
	@Test
	public void createReadByIdUpdateElement() throws Exception {
		// GIVEN we have a dao
		// AND we have a factory
		
		// WHEN I create a new message
		// AND I update the message
		// AND I read the message by key
		String name = "Element1";
		ElementEntity newElement = this.factory.createNewElement(name, "flight", new Location(3,3), new Date(), 
				"eronc@mail.afeka.ac.il", "2019B.rickyd.smartspace.element", false, new HashMap<String, Object>());
		ElementEntity actual = this.dao.create(newElement);
		ElementEntity updateElement = new ElementEntity();
		updateElement.setKey(actual.getKey());
		String newName = "Element2";
		String newType = "check-in";
		updateElement.setName(newName);
		updateElement.setType(newType);
		this.dao.update(updateElement);
		// THEN the element read using key is present
		// AND the element is updated
		assertThat(this.dao.readById(actual.getKey()))
			.isPresent()
			.get()
			.extracting("name","type")
			.containsExactly(newName,newType);
	}
}
