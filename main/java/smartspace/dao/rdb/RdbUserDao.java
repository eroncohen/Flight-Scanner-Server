package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.AdvancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.data.UserRole;

@Repository
public class RdbUserDao implements AdvancedUserDao<UserKey> {

	private UserCrud userCrud;

	@Autowired
	public RdbUserDao(UserCrud userCrud, GeneratorIdCrud generatorIdCrud) {
		this.userCrud = userCrud;
	}

	@Override
	@Transactional
	public UserEntity create(UserEntity userEntity) {
		userEntity.setKey(new UserKey(userEntity.getUserSmartspace(), userEntity.getUserEmail()));

		// SQL: INSERT
		if (!this.userCrud.existsById(userEntity.getKey())) {
			UserEntity rv = this.userCrud.save(userEntity);
			setSmartspaceAndEmail(rv);
			return rv;
		} else {
			throw new RuntimeException("User already exists with key: " + userEntity.getKey());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserEntity> readById(UserKey userKey) {
		Optional<UserEntity> rv = this.userCrud.findById(userKey);
		if (rv.isPresent())
			setSmartspaceAndEmail(rv.get());
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> readAll() {
		List<UserEntity> rv = new ArrayList<>();
		// SQL: SELECT
		this.userCrud.findAll().forEach(rv::add);
		setSmartspaceAndEmail(rv);
		return rv;
	}

	@Override
	@Transactional
	public void update(UserEntity userEntity) {
		UserEntity existing = this.readById(userEntity.getKey())
				.orElseThrow(() -> new RuntimeException("no user entity with key: " + userEntity.getKey()));

		if (existing.getAvatar() != null) {
			existing.setAvatar(userEntity.getAvatar());
		}


		if (existing.getRole() != null) {
			existing.setRole(userEntity.getRole());
		}


		if (existing.getUsername() != null) {
			existing.setUsername(userEntity.getUsername());
		}
		// SQL: UPDATE
		this.userCrud.save(existing);
	}

	@Override
	@Transactional
	public void deleteAll() {
		// SQL: DELETE
		this.userCrud.deleteAll();

	}

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> readAll(int size, int page) {
		List<UserEntity> rv = this.userCrud.findAll(PageRequest.of(page, size)).getContent();
		setSmartspaceAndEmail(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> readAll(String sortBy, int size, int page) {
		List<UserEntity> rv = this.userCrud.findAll(PageRequest.of(page, size, Direction.ASC, sortBy)).getContent();
		setSmartspaceAndEmail(rv);
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> readUserByRole(UserRole userRole, int size, int page) {
		List<UserEntity> rv = this.userCrud.findAllByRoleLike(userRole, PageRequest.of(page, size));
		setSmartspaceAndEmail(rv);
		return rv;
	}

	public void setSmartspaceAndEmail(UserEntity user) {
		user.setUserSmartspace(user.getKey().getUserSmartspace());
		user.setUserEmail(user.getKey().getUserEmail());
	}

	public void setSmartspaceAndEmail(List<UserEntity> userList) {
		for (UserEntity userEntity : userList)
			setSmartspaceAndEmail(userEntity);
	}
}
