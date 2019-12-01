package smartspace.logic;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartspace.aop.UserValidator;
import smartspace.dao.AdvancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.UserRole;

@Service
public class ElementServiceImpl implements ElementService {

	private AdvancedElementDao<ElementKey> elements;
	private String smartspace;

	@Value("${smartspace.name:smartspace.element}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Autowired
	public void setElements(AdvancedElementDao<ElementKey> elements) {
		this.elements = elements;
	}

	@Transactional
	@Override
	public ElementEntity writeElement(ElementEntity elementEntity) {
		if (validate(elementEntity)) {
			return this.elements.create(elementEntity);
		} else {
			throw new RuntimeException("invalid element input");
		}
	}

	@UserValidator
	@Transactional
	@Override
	public List<ElementEntity> importElements(List<ElementEntity> elementEntities, String adminSmartspace,
			String adminEmail) {
		elementEntities.stream().forEach(element -> {
			if (validatKey(element.getKey()))
				element = writeElement(element);
			else
				throw new RuntimeException("invalid element key");

		});
		return elementEntities;
	}

	@UserValidator
	@Transactional
	@Override
	public ElementEntity createElement(ElementEntity elementEntity, String managerSmartspace, String managerEmail) {
		if (elementEntity.getKey() == null) {
			elementEntity.setCreationTimeStamp(new Date());
			elementEntity.setCreatorSmartspace(managerSmartspace);
			elementEntity.setCreatorEmail(managerEmail);
			return writeElement(elementEntity);
		} else {
			throw new RuntimeException("invalid element key must be null");
		}
	}

	@UserValidator
	@Override
	public void updateElement(ElementEntity entity, String managerSmartspace, String managerEmail) {
		this.elements.update(entity);
	}

	@UserValidator
	@Override
	public List<ElementEntity> getElements(int size, int page, String userSmartspace, String userEmail,
			UserRole userRole) {
		if(userRole == UserRole.ADMIN || userRole == UserRole.MANAGER)
			return this.elements.readAll("creationTimeStamp", size, page);
		else
			return filterExpiredElements(this.elements.readAll("creationTimeStamp", size, page));
	}

	@UserValidator
	@Override
	public ElementEntity getElementByKey(String elementSmartspace, String elementId, String userSmartspace,
			String userEmail, UserRole userRole) {
		
		ElementKey elementKey = new ElementKey(elementSmartspace, elementId);
		ElementEntity elementEntity =  this.elements.readById(elementKey)
				.orElseThrow(() -> new RuntimeException("No element with the key" + elementKey));
		
		if(userRole == UserRole.ADMIN || userRole == UserRole.MANAGER)
			return elementEntity;
		else
			if(elementEntity.isExpired())
				throw new RuntimeException("No element with the key" + elementKey);
			else 
				return elementEntity;
	}

	@UserValidator
	@Override
	public List<ElementEntity> getElementsByType(String type, int size, int page, String userSmartspace,
			String userEmail, UserRole userRole) {
		if(userRole == UserRole.ADMIN || userRole == UserRole.MANAGER)
			return this.elements.readElementsByType(type, size, page);
		else
			return filterExpiredElements(this.elements.readElementsByType(type, size, page));
	}

	@UserValidator
	@Override
	public List<ElementEntity> getElementsByName(String name, int size, int page, String userSmartspace,
			String userEmail, UserRole userRole) {
		if(userRole == UserRole.ADMIN || userRole == UserRole.MANAGER)
			return this.elements.readElementsByName(name, size, page);
		else
			return filterExpiredElements(this.elements.readElementsByName(name, size, page));
	}

	@UserValidator
	@Override
	public List<ElementEntity> getElementsByLocationOnDistance(double x, double y, double distance, int size, int page,
			String userSmartspace, String userEmail, UserRole userRole) {
		if(userRole == UserRole.ADMIN || userRole == UserRole.MANAGER)
			return this.elements.readElementsByXBetweenAndYBetween(x, y, distance, size, page);
		else
			return filterExpiredElements(this.elements.readElementsByXBetweenAndYBetween(x, y, distance, size, page));
	}

	private boolean validate(ElementEntity elementEntity) {
		
		return 
				elementEntity.getLocation() != null && elementEntity.getName() != null
				&& !elementEntity.getName().trim().isEmpty() && elementEntity.getType() != null
				&& !elementEntity.getType().trim().isEmpty() && elementEntity.getCreationTimeStamp() != null
				&& elementEntity.getCreatorEmail() != null && !elementEntity.getCreatorEmail().trim().isEmpty()
				&& elementEntity.getCreatorSmartspace() != null
				&& !elementEntity.getCreatorSmartspace().trim().isEmpty();
	}

	private boolean validatKey(ElementKey elementKey) {
		return elementKey.getElementSmartspace() != null && !elementKey.getElementSmartspace().trim().isEmpty()
				&& !elementKey.getElementSmartspace().equals(this.smartspace) && elementKey.getElementId() != null
				&& !elementKey.getElementId().trim().isEmpty();
	}
	
	private List<ElementEntity> filterExpiredElements(List<ElementEntity> elements){
		return elements.stream()                 // element entity steam
        .filter(element -> !element.isExpired()) // we want elements that are not expired only
        .collect(Collectors.toList());  
	}
}
