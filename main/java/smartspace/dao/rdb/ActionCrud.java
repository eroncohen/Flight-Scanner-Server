package smartspace.dao.rdb;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.PagingAndSortingRepository;

import smartspace.data.ActionEntity;
import smartspace.data.ActionKey;

public interface ActionCrud extends PagingAndSortingRepository<ActionEntity, ActionKey> {

	List<ActionEntity> findAllByTypeLike(@Param("type")String type, Pageable pageable);

	List<ActionEntity> findAllByPlayerEmailLike(@Param("playerEmail")String playerEmail,  Pageable pageable);
	
	public List<ActionEntity> findAllByCreationTimeStampBetween(
			@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate,
			Pageable pageable);
	
	
}