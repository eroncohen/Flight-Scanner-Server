package smartspace.layout;

import java.util.Arrays;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import smartspace.data.UserEntity;
import smartspace.data.UserKey;
import smartspace.logic.UserService;

@CrossOrigin(origins = "http://localhost:3000", maxAge=3600)
@RestController
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping(
			path = "/smartspace/admin/users/{adminSmartspace}/{adminEmail}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] importUsers(
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestBody UserBoundary[] users) {
		List<UserEntity> userEntities = Arrays.asList(users).stream().map(user -> user.convertToEntity())
				.collect(Collectors.toList());

		return this.userService.importUsers(userEntities,adminSmartspace,adminEmail).stream()
				.map(UserBoundary::new).collect(Collectors.toList()).toArray(new UserBoundary[0]);
	}
	
	@RequestMapping(
			path = "/smartspace/admin/users/{adminSmartspace}/{adminEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportUsers(
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		
		return this.userService.exportUsers(size, page,adminSmartspace,adminEmail).stream()
				.map(UserBoundary::new).collect(Collectors.toList()).toArray(new UserBoundary[0]);
	}
	
	@RequestMapping(
			path = "/smartspace/users",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createNewUser(@RequestBody UserForm user){
		return new UserBoundary(this.userService.createUser(user.convertToEntity()));
	}

	@RequestMapping(
			path="/smartspace/users/login/{userSmartspace}/{userEmail}",
			method=RequestMethod.PUT,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateUserExceptTheirPoints (
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestBody UserBoundary updateUser) {
			
		
		UserEntity entity = updateUser.convertToEntity();
		UserKey key= new UserKey(userSmartspace,userEmail);
		entity.setKey(key);
		this.userService
			.update(entity,userSmartspace,userEmail);
	}
	
	@RequestMapping(
			path="/smartspace/users/login/{userSmartspace}/{userEmail}",
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary userLogin (
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail) {
		return new UserBoundary(this.userService.getUser(userSmartspace,userEmail));
	}
	

}
