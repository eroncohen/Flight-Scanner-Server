package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Location;

public class MemoryElementEntityDaoUpdateUnitTests {
	private final String SMARTSPACE = "smartspace.test.element";
	private final String NAME = "name";
	private final String KEY = "key";

	@Test
	public void testUpdateElementname() throws Exception {
		String newNAme = "newName";
		// GIVEN MemoryEntityDao is initialized
		// AND element in the Dao

		MemoryElementDao dao = memoryElementHelper();
		ElementEntity element = new ElementEntity(new Location(), "2019B.rickyd." + NAME, "element", new Date(), false,
				SMARTSPACE, NAME + "mail.com", new TreeMap<String, Object>());
		element.setKey(new ElementKey(SMARTSPACE,KEY));
		dao.getMemory().put(element.getKey(), element);

		// WHEN updat element name
		ElementEntity elementUpdate = new ElementEntity();
		elementUpdate.setKey(element.getKey());
		elementUpdate.setName(newNAme);
		dao.update(elementUpdate);

		// THEN the new name updated in the memory
		assertThat(dao.getMemory().get(element.getKey())).isNotNull();
		assertThat(dao.getMemory().get(element.getKey()).getName()).isEqualTo(newNAme);
	}

	@Test
	public void testUpdateElementlocation() throws Exception {
		Location newLocation = new Location(1.0, 2.0);
		// GIVEN MemoryEntityDao is initialized
		// AND element in the Dao

		MemoryElementDao dao = memoryElementHelper();
		ElementEntity element = new ElementEntity(new Location(10.0, 20.0), "2019B.rickyd." + NAME, "element",
				new Date(), false, SMARTSPACE, NAME + "mail.com", new TreeMap<String, Object>());
		element.setKey(new ElementKey(SMARTSPACE,KEY));
		dao.getMemory().put(element.getKey(), element);
		// WHEN update element location
		ElementEntity elementUpdate = new ElementEntity();
		elementUpdate.setKey(element.getKey());
		elementUpdate.setLocation(newLocation);
		dao.update(elementUpdate);

		// THEN the new location updated in the memory
		assertThat(dao.getMemory().get(element.getKey())).isNotNull();
		assertThat(dao.getMemory().get(element.getKey()).getLocation()).isEqualTo(newLocation);
	}

	@Test
	public void testUpdateElementType() throws Exception {
		String newType = "newType";
		String type = "type";
		// GIVEN MemoryEntityDao is initialized
		// AND element in the Dao

		MemoryElementDao dao = memoryElementHelper();
		ElementEntity element = new ElementEntity(new Location(), "2019B.rickyd." + NAME, type, new Date(), false,
				SMARTSPACE, NAME + "mail.com", new TreeMap<String, Object>());
		element.setKey(new ElementKey(SMARTSPACE,KEY));
		dao.getMemory().put(element.getKey(), element);

		// WHEN update element type
		ElementEntity elementUpdate = new ElementEntity();
		elementUpdate.setKey(element.getKey());
		elementUpdate.setType(newType);
		dao.update(elementUpdate);

		// THEN the new type updated in the memory
		assertThat(dao.getMemory().get(element.getKey())).isNotNull();
		assertThat(dao.getMemory().get(element.getKey()).getType()).isEqualTo(newType);
	}

	@Test
	public void testUpdateElementDate() throws Exception {
	

		// GIVEN MemoryEntityDao is initialized
		// AND element in the Dao

		MemoryElementDao dao = memoryElementHelper();
		ElementEntity element = new ElementEntity(new Location(), "2019B.rickyd." + NAME, "element", new Date(), false,
				SMARTSPACE, NAME + "mail.com", new TreeMap<String, Object>());
		element.setKey(new ElementKey(SMARTSPACE,KEY));
		dao.getMemory().put(element.getKey(), element);

		// WHEN update element date
		ElementEntity elementUpdate = new ElementEntity();
		elementUpdate.setKey(element.getKey());
		Date newDate = new Date();
		elementUpdate.setCreationTimeStamp(newDate);
		dao.update(elementUpdate);

		// THEN the new date updated in the memory
		assertThat(dao.getMemory().get(element.getKey())).isNotNull();
		assertThat(dao.getMemory().get(element.getKey()).getCreationTimeStamp()).isEqualTo(newDate);
	}

	@Test
	public void testUpdateCreatorEmail() throws Exception {
		String creatorEmail = "ronen.rozen@gmail.com";
		String newCreatorEmail = "rickyd@gmail.com";

		// GIVEN MemoryEntityDao is initialized
		// AND element in the Dao

		MemoryElementDao dao = memoryElementHelper();
		ElementEntity element = new ElementEntity(new Location(), "2019B.rickyd." + NAME, "element", new Date(), false,
				SMARTSPACE, creatorEmail, new TreeMap<String, Object>());
		element.setKey(new ElementKey(SMARTSPACE,KEY));
		dao.getMemory().put(element.getKey(), element);

		// WHEN update element elementSmartspace
		ElementEntity elementUpdate = new ElementEntity();
		elementUpdate.setKey(element.getKey());
		elementUpdate.setCreatorEmail(newCreatorEmail);
		dao.update(elementUpdate);

		// THEN the new elementSmartspace updated in the memory
		assertThat(dao.getMemory().get(element.getKey())).isNotNull();
		assertThat(dao.getMemory().get(element.getKey()).getCreatorEmail()).isEqualTo(newCreatorEmail);
	}
	
	@Test
	public void testUpdateMoreAttributes() throws Exception {
		Map<String,Object> moreAttributes = new TreeMap<String,Object>();
		moreAttributes.put(KEY, new ElementEntity());
		Map<String,Object> newMoreAttributes = new TreeMap<String,Object>();
		newMoreAttributes.put(KEY, new ElementEntity());

		// GIVEN MemoryEntityDao is initialized
		// AND element in the Dao

		MemoryElementDao dao = memoryElementHelper();
		ElementEntity element = new ElementEntity(new Location(), "2019B.rickyd." + NAME, "element", new Date(), false,
				SMARTSPACE, NAME + "mail.com", moreAttributes);
		element.setKey(new ElementKey(SMARTSPACE,KEY));
		dao.getMemory().put(element.getKey(), element);

		// WHEN update element moreAttributes
		ElementEntity elementUpdate = new ElementEntity();
		elementUpdate.setKey(element.getKey());
		elementUpdate.setMoreAttributes(newMoreAttributes);
		dao.update(elementUpdate);

		// THEN the new moreAttributes updated in the memory
		assertThat(dao.getMemory().get(element.getKey())).isNotNull();
		assertThat(dao.getMemory().get(element.getKey()).getMoreAttributes()).isEqualTo(newMoreAttributes);
	}
	

	private MemoryElementDao memoryElementHelper() {
		MemoryElementDao dao = new MemoryElementDao();
		dao.setSmartspace(SMARTSPACE);
		return dao;

	}
}
