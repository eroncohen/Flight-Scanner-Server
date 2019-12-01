package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import smartspace.data.ActionEntity;

public class MemoryActiontEntityDaoCreateUnitTests {

	final static int maxCharsInAttribute = 255;


	@Test
	public void testCreateWithValidActionId() throws Exception {

		// GIVEN MemoryActionDao is initialized
		String smartspace = "smartspace";

		// RdbActionDao dao = new RdbActionDao(actionCrud);
		MemoryActionDao dao = new MemoryActionDao();

		dao.setSmartspace(smartspace);

		// WHEN I create with Valid ActionEntity
		ActionEntity newActionEntity = new ActionEntity();
		ActionEntity createdAction = dao.create(newActionEntity);

		// THEN the returned ActionEntity has '1' as actionId value
		// AND no exception is thrown
		assertThat(createdAction.getActionId()).isEqualTo("1");
	}

	@Test
	public void testCreateWithValidActionSmartface() throws Exception {

		// GIVEN MemoryActionDao is initialized
		String smartspace = "smartspace";

		MemoryActionDao dao = new MemoryActionDao();

		dao.setSmartspace(smartspace);

		// WHEN I create with Valid ActionEntity
		ActionEntity actionEntity = new ActionEntity();
		ActionEntity createdActionEntity = dao.create(actionEntity);

		// THEN the returned ActionEntity has the same smartspace has the dao
		// AND no exception is thrown
		assertThat(createdActionEntity.getActionSmartspace()).isNotNull().isEqualTo(dao.getSmartspace());
	}

	@Test
	public void testCreateWithValidKey() throws Exception {

		// GIVEN MemoryActionDao is initialized
		String smartspace = "smartspace";

		MemoryActionDao dao = new MemoryActionDao();
		dao.setSmartspace(smartspace);

		// WHEN I create with Valid ActionEntity
		ActionEntity actionEntity = new ActionEntity();
		ActionEntity createdActionEntity = dao.create(actionEntity);

		// THEN the returned ActionEntity has a valid key
		// AND no exception is thrown
		String expectedKey = smartspace + "@" + createdActionEntity.getActionId();
		assertThat(createdActionEntity.getKey().toString()).isNotNull().isEqualTo(expectedKey);

	}

	@Test
	public void testCreateTwoConsecutiveElementsKeys() throws Exception {

		// GIVEN MemoryActionDao is initialized
		String smartspace = "smartspace";
		MemoryActionDao dao = new MemoryActionDao();
		dao.setSmartspace(smartspace);

		// WHEN I create 2 Valid Consecutive ActionEntities
		ArrayList<ActionEntity> list = new ArrayList<ActionEntity>(
				Arrays.asList(dao.create(new ActionEntity()), dao.create(new ActionEntity())));

		// THEN the returned ActionEntities has a valid key
		// AND the actionId is consecutive
		// AND no exception is thrown
		String expectedKey = smartspace + "@";
		assertThat(list.get(0).getKey().toString()).isEqualTo(expectedKey + '1');

		assertThat(list.get(1).getKey().toString()).isEqualTo(expectedKey + '2');

	}

}
