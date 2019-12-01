package smartspace.dao;

import java.util.Date;
import java.util.List;

import smartspace.data.ElementEntity;

public interface AdvancedElementDao<ElementKey> extends ElementDao<ElementKey> {
	
	public List<ElementEntity> readAll(int size, int page);

	public List<ElementEntity> readAll(String sortBy, int size, int page);
	
	public List<ElementEntity> readElementsWithcreationTimeStampInRange(Date fromDate, Date toDate, int size, int page);

	public List<ElementEntity> readElementsByType(String type, int size, int page);
	
	public List<ElementEntity> readElementsByCreatorEmail(String email, int size, int page);
	
	public List<ElementEntity> readElementsByCreatorSmartspace(String smartspace, int size, int page);
	
	public List<ElementEntity> readElementsByName(String name, int size, int page);

	public List<ElementEntity> readElementsByXBetweenAndYBetween(double x, double y,double distance,int size,int page);
	
	public List<ElementEntity> readElementsByTypeAndXBetweenAndYBetween(String type,double x, double y,double distance,int size,int page);

	public List<ElementEntity> readElementsByTypeAndName(String type, String name, int size, int page);
}
