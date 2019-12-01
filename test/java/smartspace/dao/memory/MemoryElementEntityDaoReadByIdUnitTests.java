package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;

import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Location;

public class MemoryElementEntityDaoReadByIdUnitTests {
	@Test
	public void testReadByIdWithValidElement() throws Exception {
		// GIVEN MemoryEntityDao is initialized
		// AND There are elements in the memory

		ElementKey existingKey = new ElementKey("smartspace.test.element","4");
		MemoryElementDao dao = initElementDaoHelper();

		// WHEN an existing key is given to ReadById

		Optional<ElementEntity> memoryElement = dao.readById(existingKey);

		// THEN the returned element exists
		// AND has the key that was given

		assertThat(memoryElement).isPresent().get().extracting("key").containsExactly(existingKey);

	}


	@Test
	public void testReadByIdWithNotExistingIdOnMemory() throws Exception {
		// GIVEN MemoryEntityDao is initialized
		// AND There are elements in the memory
		ElementKey notExistingKey = new ElementKey("smartspace.test.element","20");
		MemoryElementDao dao = initElementDaoHelper();
		// WHEN an existing key is given to ReadById
		Optional<ElementEntity> memoryUser = dao.readById(notExistingKey);

		// THEN the returned user exists
		// AND has the key that was given
		assertThat(memoryUser).isNotPresent();
	}
	

	private MemoryElementDao initElementDaoHelper() {
		int numberOfElements = 10;
		String smartspace = "smartspace.test.element";
		MemoryElementDao dao = new MemoryElementDao();
		dao.setSmartspace(smartspace);

		int key = 0;
		List<ElementEntity> listOfElements = IntStream.range(0, numberOfElements).mapToObj(num -> "dummy #" + num)
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
