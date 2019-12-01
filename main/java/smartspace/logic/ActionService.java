package smartspace.logic;

import java.util.List;

import smartspace.data.ActionEntity;
import smartspace.data.UserRole;

public interface ActionService {

	public ActionEntity writeAction(ActionEntity actionEntity);

	public List<ActionEntity> importActions(List<ActionEntity> actionsEntities, String adminSmartspace,
			String adminEmail);

	public List<ActionEntity> exportActions(int size, int page, String adminSmartspace, String adminEmail);

	public Object handleAction(ActionEntity action,String userSmartspace,
			String userEmail,UserRole userRole);
}
