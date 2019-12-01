package smartspace.dao.rdb;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;

public interface ElementCrud extends PagingAndSortingRepository<ElementEntity, ElementKey> {

	public List<ElementEntity> findAllByCreationTimeStampBetween(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate, Pageable pageable);

	public List<ElementEntity> findAllByCreatorEmailLike(@Param("email") String email, Pageable pageable);

	public List<ElementEntity> findAllByCreatorSmartspaceLike(@Param("smartSpace") String smartSpace,
			Pageable pageable);

	public List<ElementEntity> findAllByTypeLike(@Param("type") String type, Pageable pageable);

	public List<ElementEntity> findAllByNameLike(@Param("name") String name, Pageable pageable);

	public List<ElementEntity> findAllByLocationXBetweenAndLocationYBetween(@Param("fromX") double fromX,
			@Param("toX") double toX, @Param("fromY") double fromY, @Param("toY") double toY, Pageable pageable);
	
	public List<ElementEntity> findAllByTypeLikeAndLocationXBetweenAndLocationYBetween(
			@Param("type") String type,
			@Param("fromX") double fromX,
			@Param("toX") double toX, 
			@Param("fromY") double fromY, 
			@Param("toY") double toY, 
			Pageable pageable);
	
	public List<ElementEntity> findAllByTypeLikeAndNameLike(@Param("type") String type,@Param("name") String name,Pageable pageable);
}
