package smartspace.dao.rdb;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;

public interface UserCrud extends 
	PagingAndSortingRepository<UserEntity, UserKey>{
	public List<UserEntity> findAllByRoleLike(
			@Param("userRole") UserRole userRole,
			Pageable pageable);
}
