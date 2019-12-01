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

import smartspace.dao.AdvancedActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.ActionKey;

@Repository
public class RdbActionDao implements AdvancedActionDao {
	private ActionCrud actionCrud;
	private GeneratorIdCrud generatorIdCrud;
	private String smartspace;

	@Autowired
	public RdbActionDao(ActionCrud actionCrud, GeneratorIdCrud generatorIdCrud) {
		this.actionCrud = actionCrud;
		this.generatorIdCrud = generatorIdCrud;
	}

	@Value("${smartspace.name:smartSpaceAction}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Override
	@Transactional
	public ActionEntity create(ActionEntity actionEntity) {
		if (actionEntity.getKey() == null) {
			GeneratorId idEntity = this.generatorIdCrud.save(new GeneratorId());
			actionEntity.setActionId(idEntity.getId() + "");
			actionEntity.setActionSmartspace(this.smartspace);
			actionEntity.setKey(new ActionKey(smartspace, idEntity.getId() + ""));
			this.generatorIdCrud.delete(idEntity);
		}
		// SQL: INSERT
		if (!this.actionCrud.existsById(actionEntity.getKey())) {
			ActionEntity rv = this.actionCrud.save(actionEntity);
			setSmartspaceAndId(rv);
			return rv;
		} else {
			throw new RuntimeException("Action already exists with key: " + actionEntity.getKey());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readAll() {
		List<ActionEntity> rv = new ArrayList<>();
		// SQL: SELECT
		this.actionCrud.findAll().forEach(rv::add);
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	public void deleteAll() {

		this.actionCrud.deleteAll();
		// SQL: DELETE

	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readAll(int size, int page) {
		List<ActionEntity> rv = this.actionCrud.findAll(PageRequest.of(page, size)).getContent();
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readAll(String sortBy, int size, int page) {
		List<ActionEntity> rv = this.actionCrud.findAll(PageRequest.of(page, size, Direction.ASC, sortBy)).getContent();
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readActionsByType(String type, int size, int page) {
		List<ActionEntity> rv = this.actionCrud.findAllByTypeLike(type, PageRequest.of(page, size));
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readActionsByPlayerEmail(String playerEmail, int size, int page) {
		List<ActionEntity> rv = this.actionCrud.findAllByPlayerEmailLike(playerEmail, PageRequest.of(page, size));
		setSmartspaceAndId(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readActionsWithCreationTimeStampInRange(Date fromDate, Date toDate, int size, int page) {
		List<ActionEntity> rv = this.actionCrud.findAllByCreationTimeStampBetween(fromDate, toDate,
				PageRequest.of(page, size));
		setSmartspaceAndId(rv);
		return rv;
	}

	public void setSmartspaceAndId(ActionEntity actionEntity) {
		actionEntity.setActionSmartspace(actionEntity.getKey().getActionSmartspace());
		actionEntity.setActionId(actionEntity.getKey().getActionId());
	}

	public void setSmartspaceAndId(List<ActionEntity> actionList) {
		for (ActionEntity actionEntity : actionList)
			setSmartspaceAndId(actionEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ActionEntity> readById(ActionEntity actionEntity) {
		Optional<ActionEntity> rv = this.actionCrud.findById(actionEntity.getKey());
		if (rv.isPresent())
			setSmartspaceAndId(rv.get());
		return rv;
	}

	@Override
	@Transactional
	public void update(ActionEntity actionEntity) {
		ActionEntity existing = this.readById(actionEntity)
				.orElseThrow(() -> new RuntimeException("No element with the key: " + actionEntity.getKey()));

		if (actionEntity.getMoreAttributes() != null) {
			existing.setMoreAttributes(actionEntity.getMoreAttributes());
		}
		if (actionEntity.getType() != null) {
			existing.setType(actionEntity.getType());
		}
		if (actionEntity.getMoreAttributes() != null) {
			existing.setType(actionEntity.getType());
		}
		this.actionCrud.save(existing);

	}

}
