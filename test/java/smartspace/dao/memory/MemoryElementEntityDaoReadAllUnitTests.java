package smartspace.dao.memory;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Location;

public class MemoryElementEntityDaoReadAllUnitTests {
	private final int numberOfElements = 10;
	
	@Test
	public void testReadAllWithNonEmptyMEmory() throws Exception {
		// GIVEN MemoryEntityDao is initialized
		// AND There are elements in the memory
		MemoryElementDao dao = initElementDaoHelper();
		
		//WHEN i read all elements from memory
		List<ElementEntity> memoryElements = dao.readAll();
		
		//THEN the returned list is not null and the size of the list is equal to numberOfElements
		assertThat(memoryElements).isNotNull();
		assertThat(memoryElements).size().isEqualTo(numberOfElements);
	}
	
	@Test
	public void testReadByAllWithEmptyMemory() throws Exception {
		// GIVEN MemoryEntityDao is initialized
		String smartspace = "smartspace.element.test";
		MemoryElementDao dao = new MemoryElementDao();
		dao.setSmartspace(smartspace);
		
		//WHEN there are no elements in the dao memory
		List<ElementEntity> daoElements = dao.readAll();
		
		//THEN the list is empty
		assertThat(daoElements).isEmpty();
	}
	
	
	private MemoryElementDao initElementDaoHelper() {
		
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
