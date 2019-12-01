package smartspace.dao.memory;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Location;

public class MemoryElementEntityDaoDeleteUnitTests {
	private final int NUMBER_OF_ELEMENTAS = 10;

	@Test
	public void testDeleteElement() throws Exception {
		// GIVEN MemoryEntityDao is initialized
		// AND There are elements in the memory
		ElementKey existingKey = new ElementKey("smartspace.test.element","4");
		MemoryElementDao dao = initElementDaoHelper();

		// WHEN i delete the element
		dao.delete(dao.getMemory().get(existingKey));

		// THEN the element with the key expired is set to true
		// AND the others is still not expired
		assertThat(dao.getMemory().get(existingKey).isExpired()).isTrue();

		for (int i = 0; i < NUMBER_OF_ELEMENTAS; i++) {
			if (!(i+"").equals("4"))
				assertThat(dao.getMemory().get(new ElementKey("smartspace.test.element",i+"")).isExpired()).isFalse();
		}
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
