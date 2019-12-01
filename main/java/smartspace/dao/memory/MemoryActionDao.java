package smartspace.dao.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import smartspace.dao.ActionDao;
//import smartspace.dao.memory.IdGenerator.Holder;
import smartspace.data.ActionEntity;
import smartspace.data.ActionKey;

//@Repository
public class MemoryActionDao implements ActionDao {
	
	private Map<ActionKey, ActionEntity> memory;
	private AtomicLong serial;
	private String smartspace;

	public MemoryActionDao() {
		this.memory = Collections.synchronizedSortedMap(new TreeMap<>());
		this.serial = new AtomicLong(1L);
	}
	
	public String getSmartspace() {
		return smartspace;
	}
	
	@Value("${smartpace.action.name:smartspace}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	protected Map<ActionKey, ActionEntity> getMemory() {
		return memory;
	}

	public AtomicLong getSerial() {
		return serial;
	}

	@Override
	public ActionEntity create(ActionEntity actionEntity) {
		actionEntity.setActionId(String.valueOf(serial.getAndIncrement()));
		actionEntity.setActionSmartspace(this.smartspace);
		actionEntity.setKey(new ActionKey(actionEntity.getActionSmartspace(),actionEntity.getActionId()));
		this.memory.put(actionEntity.getKey(), actionEntity);
		
		
		return actionEntity;
	}

	@Override
	public List<ActionEntity> readAll() {
		
		return new ArrayList<ActionEntity>(this.memory.values());
	}

	@Override
	public void deleteAll() {
		this.memory.clear();

	}

}
