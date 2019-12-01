package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import smartspace.data.ActionEntity;

public class MemoryActionEntityDaoReadAllUnitTests {
	
	@Test
	public void testReadAll() throws Exception {

		// GIVEN MemoryActionDao is initialized 
		MemoryActionDao dao = new MemoryActionDao();
		String daoSmartface = "dao_smartspace";
		dao.setSmartspace(daoSmartface);

		// WHEN I create with 3 Valid ActionEntity 
		ArrayList<ActionEntity> list = new ArrayList<ActionEntity>(
				Arrays.asList(
						dao.create(new ActionEntity()), 
						dao.create(new ActionEntity()),
						dao.create(new ActionEntity())));
		

		// THEN the creates actions are the same as in the db
		assertThat(dao.readAll())
		.usingElementComparatorOnFields("key")
		.containsExactlyElementsOf(list);

	}

}
