package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.AdvancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;

@Repository
public class RdbElementDao implements AdvancedElementDao<ElementKey> {

	private ElementCrud elementCrud;
	private GeneratorIdCrud generatorIdCrud;
	private String smartspace;

	@Autowired
	public RdbElementDao(ElementCrud elementCrud, GeneratorIdCrud generatorIdCrud) {
		this.elementCrud = elementCrud;
		this.generatorIdCrud = generatorIdCrud;
	}

	@Override
	@Transactional
	public ElementEntity create(ElementEntity elementEntity) {
		if (elementEntity.getKey() == null) {
			GeneratorId idEntity = this.generatorIdCrud.save(new GeneratorId());
			elementEntity.setElementId(idEntity.getId() + "");
			elementEntity.setElementSmartspace(getSmartspace());
			elementEntity.setKey(new ElementKey(this.smartspace, idEntity.getId() + ""));

			this.generatorIdCrud.delete(idEntity);
		}
		// SQL: INSERT
		if (!this.elementCrud.existsById(elementEntity.getKey())) {
			ElementEntity rv = this.elementCrud.save(elementEntity);
			setSmartspaceAndId(rv);
			return rv;
		} else {
			throw new RuntimeException("Element already exist with key: " + elementEntity.getKey());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ElementEntity> readById(ElementKey elementKey) {
		Optional<ElementEntity> rv = this.elementCrud.findById(elementKey);
		if (rv.isPresent())
			setSmartspaceAndId(rv.get());
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readElementsByXBetweenAndYBetween(double x, double y, double distance, int size,
			int page) {
		List<ElementEntity> elements = this.elementCrud.findAllByLocationXBetweenAndLocationYBetween(x - distance,
				x + distance, y - distance, y + distance, PageRequest.of(page, size));
		setSmartspaceAndId(elements);
		return elements;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readAll() {
		List<ElementEntity> elements = new ArrayList<>();
		this.elementCrud.findAll().forEach(element -> elements.add(element));
		// another way: this.elementCrud.findAll().forEach(elements::add);
		setSmartspaceAndId(elements);
		return elements;
	}

	@Override
	@Transactional
	public void update(ElementEntity elementEntity) {
		ElementEntity existing = this.readById(elementEntity.getKey())
				.orElseThrow(() -> new RuntimeException("No element with the key: " + elementEntity.getKey()));

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

		this.elementCrud.save(existing);

	}

	@Override
	public void deleteByKey(ElementKey elementKey) {
		if (this.elementCrud.existsById(elementKey)) {
			this.elementCrud.deleteById(elementKey);
		} else {
			throw new RuntimeException("There is no element with key: " + elementKey + " in the repository");
		}
	}

	@Override
	public void delete(ElementEntity elementEntity) {
		if (this.elementCrud.existsById(elementEntity.getKey())) {
			this.elementCrud.delete(elementEntity);
		} else {
			throw new RuntimeException("The element you requested to delete does not exist in the repository");
		}
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.elementCrud.deleteAll();
	}

	public String getSmartspace() {
		return smartspace;
	}

	@Value("${smartspace.name:smartspace.element}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readAll(int size, int page) {
		List<ElementEntity> rv = this.elementCrud.findAll(PageRequest.of(page, size)).getContent();
		setSmartspaceAndId(rv);
		return rv;

	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readAll(String sortBy, int size, int page) {
		List<ElementEntity> rv = this.elementCrud.findAll(PageRequest.of(page, size, Direction.ASC, sortBy))
				.getContent();
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readElementsWithcreationTimeStampInRange(Date fromDate, Date toDate, int size,
			int page) {
		List<ElementEntity> rv = this.elementCrud.findAllByCreationTimeStampBetween(fromDate, toDate,
				PageRequest.of(page, size));
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readElementsByType(String type, int size, int page) {
		List<ElementEntity> rv = this.elementCrud.findAllByTypeLike(type, PageRequest.of(page, size));
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readElementsByCreatorEmail(String email, int size, int page) {
		List<ElementEntity> rv = this.elementCrud.findAllByCreatorEmailLike(email, PageRequest.of(page, size));
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readElementsByCreatorSmartspace(String smartspace, int size, int page) {
		List<ElementEntity> rv = this.elementCrud.findAllByCreatorSmartspaceLike(smartspace,
				PageRequest.of(page, size));
		setSmartspaceAndId(rv);
		return rv;
	}

	public void setSmartspaceAndId(ElementEntity elementEntity) {
		elementEntity.setElementSmartspace(elementEntity.getKey().getElementSmartspace());
		elementEntity.setElementId(elementEntity.getKey().getElementId());
	}

	public void setSmartspaceAndId(List<ElementEntity> elementList) {
		for (ElementEntity elementEntity : elementList)
			setSmartspaceAndId(elementEntity);
	}

	@Override
	public List<ElementEntity> readElementsByName(String name, int size, int page) {
		List<ElementEntity> rv = this.elementCrud.findAllByNameLike("%" + name + "%", PageRequest.of(page, size));
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readElementsByTypeAndXBetweenAndYBetween(String type, double x, double y,
			double distance, int size, int page) {
		List<ElementEntity> elements = this.elementCrud.findAllByTypeLikeAndLocationXBetweenAndLocationYBetween(type, x - distance,
				x + distance, y - distance, y + distance, PageRequest.of(page, size));
		setSmartspaceAndId(elements);
		return elements;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readElementsByTypeAndName(String type,String name, int size, int page) {
		List<ElementEntity> elements = this.elementCrud.findAllByTypeLikeAndNameLike(type, name, PageRequest.of(page, size));
		setSmartspaceAndId(elements);
		return elements;
	}
	
	
}
