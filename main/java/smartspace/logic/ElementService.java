package smartspace.logic;

import java.util.List;

import smartspace.data.ElementEntity;
import smartspace.data.UserRole;

public interface ElementService {
	public ElementEntity writeElement(ElementEntity entity);
	
	public List<ElementEntity> importElements(List<ElementEntity> elementEntities, String adminSmartspace,
			String adminEmail);
	
	//public List<ElementEntity> exportElements(int size, int page, String adminSmartspace, String adminEmail);
	
	public ElementEntity createElement(ElementEntity convertToEntity, String managerSmartspace, String managerEmail);

	public void updateElement(ElementEntity entity, String managerSmartspace, String managerEmail);

	public List<ElementEntity> getElements(int size, int page, String userSmartspace, String userEmail,UserRole userRole);

	public ElementEntity getElementByKey(String elementSmartspace, String elementId, String userSmartspace,
			String userEmail,UserRole userRole);

	public List<ElementEntity> getElementsByType(String type, int size, int page, String userSmartspace,
			String userEmail,UserRole userRole);

	public List<ElementEntity> getElementsByName(String name, int size, int page, String userSmartspace,
			String userEmail,UserRole userRole);

	public List<ElementEntity> getElementsByLocationOnDistance(double x, double y, double distance, int size,
			int page, String userSmartspace, String userEmail,UserRole userRole);

}
