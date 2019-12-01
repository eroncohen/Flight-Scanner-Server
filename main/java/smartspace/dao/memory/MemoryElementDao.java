package smartspace.dao.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;

//@Repository
public class MemoryElementDao implements ElementDao<ElementKey> {

	private Map<ElementKey, ElementEntity> memory;
	private AtomicLong serial;
	private String smartspace;

	public Map<ElementKey, ElementEntity> getMemory() {
		return memory;
	}

	public void setMemory(Map<ElementKey, ElementEntity> memory) {
		this.memory = memory;
	}

	public AtomicLong getSerial() {
		return serial;
	}

	public void setSerial(AtomicLong serial) {
		this.serial = serial;
	}

	public String getSmartspace() {
		return smartspace;
	}

	@Value("${smartspace.element.name:smartspace}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	public MemoryElementDao() {
		this.memory = Collections.synchronizedSortedMap(new TreeMap<>());
		this.serial = new AtomicLong(1L);
		
	}

	@Override
	public ElementEntity create(ElementEntity elementEntity) {
		elementEntity.setElementId(String.valueOf(serial.getAndIncrement()));
		elementEntity.setKey(new ElementKey(getSmartspace(),elementEntity.getElementId()));
		elementEntity.setElementSmartspace(getSmartspace());
		this.memory.put(elementEntity.getKey(), elementEntity);
		return elementEntity;
	}

	@Override
	public Optional<ElementEntity> readById(ElementKey elementKey) {
		ElementEntity element = this.memory.get(elementKey);
		return element != null ? Optional.of(element) : Optional.empty();
	}

	@Override
	public List<ElementEntity> readAll() {
		return new ArrayList<>(this.memory.values());
	}

	@Override
	public void update(ElementEntity elementEntity) {
		ElementEntity existing = this.readById(elementEntity.getKey())
				.orElseThrow(() -> new RuntimeException("no element entity with key: " + elementEntity.getKey()));

		if (elementEntity.getLocation() != null) {
			existing.setLocation(elementEntity.getLocation());
		}
		if (elementEntity.getName() != null) {
			existing.setName(elementEntity.getName());
		}
		if (elementEntity.getType() != null) {
			existing.setType(elementEntity.getType());
		}
		if (elementEntity.getCreatorEmail() != null) {
			existing.setCreatorEmail(elementEntity.getCreatorEmail());
		}
		if (elementEntity.getMoreAttributes() != null) {
			existing.setMoreAttributes(elementEntity.getMoreAttributes());
		}
		/* boolean can't be null. Just false or true (primitive type) */
		existing.setExpired(elementEntity.isExpired());
	}

	@Override
	public void deleteByKey(ElementKey elementKey) {
		this.memory.remove(elementKey);
	}

	@Override
	public void delete(ElementEntity elementEntity) {
		this.memory.get(elementEntity.getKey()).setExpired(true);
	}

	@Override
	public void deleteAll() {
		this.memory.clear();
	}

}
