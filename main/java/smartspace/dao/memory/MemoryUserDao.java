package smartspace.dao.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import smartspace.dao.UserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;

//@Repository 
public class MemoryUserDao implements UserDao<UserKey>{
	private Map<UserKey, UserEntity> memory;
	private String smartspace;
	
	public MemoryUserDao() {
		this.memory = Collections.synchronizedSortedMap(new TreeMap<>());
	}
	@Value("${smartspace.user.name:smartspace}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}
	
	@Override
	public UserEntity create(UserEntity userEntity) {
		userEntity.setKey(new UserKey(this.smartspace,userEntity.getUserEmail()));
		this.memory.put(userEntity.getKey(), userEntity);
		return userEntity;
	}

	@Override
	public Optional<UserEntity> readById(UserKey userKey) {
		UserEntity user = this.memory.get(userKey);
		if (user != null) {
			return Optional.of(user);
		}else {
			return Optional.empty();
		}
	}

	@Override
	public List<UserEntity> readAll() {
		return new ArrayList<>(this.memory.values());
	}

	@Override
	public void update(UserEntity userEntity) {
		UserEntity existing = 
				this.readById(userEntity.getKey())
				.orElseThrow(()->new RuntimeException("no user entity with key: " + userEntity.getKey()));
		
		if(existing.getAvatar() != null) {
			existing.setAvatar(userEntity.getAvatar());
		}
		
		if(existing.getPoints() != -1) {
			existing.setPoints(userEntity.getPoints());
		}
		
		if(existing.getRole() != null) {
			existing.setRole(userEntity.getRole());
		}
			
		if(existing.getUserEmail() != null) {
			existing.setUserEmail(userEntity.getUserEmail());
		}
		
		if(existing.getUsername() != null) {
			existing.setUsername(userEntity.getUsername());
		}
		
	}

	protected Map<UserKey, UserEntity> getMemory() {
		return memory;
	}
	
	@Override
	public void deleteAll() {
		this.memory.clear();
	}



}
