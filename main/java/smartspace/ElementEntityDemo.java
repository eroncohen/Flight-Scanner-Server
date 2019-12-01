package smartspace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import smartspace.data.ElementEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import smartspace.dao.ElementDao;
import smartspace.data.ElementKey;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;

//@Profile("production")
//@Component
public class ElementEntityDemo implements CommandLineRunner {

	private EntityFactory factory;
	private ElementDao<ElementKey> elementDao;
	
	@Autowired
	public ElementEntityDemo(EntityFactory factory, ElementDao<ElementKey> elementDao) {
		this.factory = factory;
		this.elementDao = elementDao;

	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("\n\n ----------------  Element Entity Start: -------------------- \n");
		
		String name = "element1";
		String type = "filght";
		Location location = new Location(1, 5);
		Date creationTimeStamp = new Date();
		String creatorEmail = "Sagiv.asraf@s.afeka.ac.il";
		String creatorSmartspace = "Ricky";
		boolean expired = false;
		Map<String, Object> moreAttributes = null;

		ElementEntity element1 = this.factory.createNewElement(name, type, location, creationTimeStamp, creatorEmail,
				creatorSmartspace, expired, moreAttributes);
		Thread.sleep(100);
		System.err.println("new element:\n" + element1);
		element1 = this.elementDao.create(element1);
		System.err.println("stored element:\n" + element1);
		
		Map<String, Object> updatedMoreAttributes = new HashMap<>();
		updatedMoreAttributes.put("creatorId", "312527777");
		ElementEntity updateElementEntity = new ElementEntity();
		updateElementEntity.setKey(element1.getKey());
		updateElementEntity.setCreatorEmail("Shimon@afeka.com");
		updateElementEntity.setType("NOT FLIGHT");
		updateElementEntity.setName("new element name");
		updateElementEntity.setMoreAttributes(updatedMoreAttributes);

		Optional<ElementEntity> elementOp = this.elementDao.readById(element1.getKey());
		if (elementOp.isPresent()) {
			element1 = elementOp.get();
		} else {
			throw new RuntimeException("Error! element vanished after update");
		}
		
		System.err.println("\nUpdated element:\n" + element1);

		this.elementDao.deleteAll();
		if (this.elementDao.readAll().isEmpty()) {
			System.err.println("\nSuccessfully deleted all elements");
		} else {
			throw new RuntimeException("Error! there is an element in the memory after deletion");
		}
		System.out.println("\n ----------------  Element Entity End -------------------- \n\n");
		
	}
}
