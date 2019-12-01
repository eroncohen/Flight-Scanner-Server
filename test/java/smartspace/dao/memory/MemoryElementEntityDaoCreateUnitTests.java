package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;

public class MemoryElementEntityDaoCreateUnitTests {

	@Test
	public void testCreateWithValidElement() throws Exception {
		String smartspace = "smartspace";
		String name = "Unit-Test";
		MemoryElementDao dao = new MemoryElementDao(); 
		dao.setSmartspace(smartspace);
		
		ElementEntity newElementEntity = new ElementEntity();
		newElementEntity.setName(name);
		ElementEntity createdElement = dao.create(newElementEntity);
		
		assertThat(createdElement.getKey().toString())
			.isNotNull()
			.startsWith(smartspace);
		assertThat(dao.getMemory().values())
			.usingElementComparatorOnFields("name")
			.contains(newElementEntity);
	
	}
	
	@Test
	public void testCreateTwoElements() throws Exception {
		String smartspace = "smartspace";
		MemoryElementDao dao = new MemoryElementDao(); 
		dao.setSmartspace(smartspace);
		
		List<ElementEntity> list = 
				Stream.of("test1","test2")
				.map(x -> new ElementEntity())
				.map(entity -> dao.create(entity))
				.collect(Collectors.toList());
		
		assertThat(dao.getMemory().values())
			.hasSize(2)
			.containsExactlyElementsOf(list);
		
		ElementKey key1 = list.get(0).getKey();
		ElementKey key2 = list.get(1).getKey();
		
		assertThat(key1).isNotEqualTo(key2);
		
	}
	
}
