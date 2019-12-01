package smartspace.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import smartspace.data.ActionEntity;



public interface AdvancedActionDao extends ActionDao{


	public List<ActionEntity> readAll(int size, int page);
	public List<ActionEntity> readAll(String sortBy, int size, int page);
	public List<ActionEntity> readActionsByType(String type, int size, int page);
	public List<ActionEntity> readActionsByPlayerEmail(String playerEmail, int size, int page);
	public List<ActionEntity> readActionsWithCreationTimeStampInRange(Date fromDate, Date toDate, int size, int page);
	void update(ActionEntity actionEntity);
	Optional<ActionEntity> readById(ActionEntity actionEntity);	
	
}
