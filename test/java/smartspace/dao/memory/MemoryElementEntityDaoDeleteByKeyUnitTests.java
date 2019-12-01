package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Location;

public class MemoryElementEntityDaoDeleteByKeyUnitTests {
	private final int NUMBER_OF_ELEMENTAS = 10;

	@Test
	public void testDeleteByValidKey() throws Exception {
		// GIVEN MemoryEntityDao is initialized
		// AND There are elements in the memory
		ElementKey existingKey = new ElementKey("smartspace.test.element","4");
		MemoryElementDao dao = initElementDaoHelper();

		// WHEN an existing key is given to deleteByKey
		dao.deleteByKey(existingKey);

		// THEN the element with the key who was deleted will not be in the memory;
		//AND the size of the elements in the db will be 9
		assertThat(dao.readById(existingKey)).isEmpty();
		assertThat(dao.readAll()).size().isEqualTo(NUMBER_OF_ELEMENTAS -1);

	}

	@Test
	public void testDeleteByNonValidKey() throws Exception {
		// GIVEN MemoryEntityDao is initialized
		// AND There are elements in the memory
		ElementKey notExistingKey = new ElementKey("smartspace.test.element","16");
		MemoryElementDao dao = initElementDaoHelper();

		// WHEN non existing key is given to deleteByKey
		dao.deleteByKey(notExistingKey);

		// THEN the size of the elements will remain 10
		assertThat(dao.readAll().size()).isEqualTo(NUMBER_OF_ELEMENTAS);
	}

	private MemoryElementDao initElementDaoHelper() {

		String smartspace = "smartspace.test.element";
		MemoryElementDao dao = new MemoryElementDao();
		dao.setSmartspace(smartspace);
		int key = 0;
		List<ElementEntity> listOfElements = IntStream.range(0, NUMBER_OF_ELEMENTAS).mapToObj(num -> "dummy #" + num)
				.map(name -> new ElementEntity(new Location(), "2019B.rickyd." + name, "element", new Date(), false,
						smartspace, name + "mail.com", new TreeMap<String, Object>()))
				.collect(Collectors.toList());
		// putting the element list to memory with keys
		for (ElementEntity ee : listOfElements) {
			ee.setKey(new ElementKey(smartspace,key++ + ""));
			dao.getMemory().put(ee.getKey(), ee);
		}
		return dao;
	}
}
