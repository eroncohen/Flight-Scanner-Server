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

import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.UserEntity;
import smartspace.logic.ActionService;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class ActionContoller {

	private ActionService actionService;

	@Autowired
	public void setActionService(ActionService actionService) {
		this.actionService = actionService;
	}

	@RequestMapping(path = "/smartspace/admin/actions/{adminSmartspace}/{adminEmail}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] importActions(@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail, @RequestBody ActionBoundary[] actions) {
		List<ActionEntity> actionsEntities = Arrays.asList(actions).stream().map(user -> user.convertToEntity())
				.collect(Collectors.toList());

		return this.actionService.importActions(actionsEntities, adminSmartspace, adminEmail).stream()
				.map(ActionBoundary::new).collect(Collectors.toList()).toArray(new ActionBoundary[0]);

	}

	@RequestMapping(path = "/smartspace/admin/actions/{adminSmartspace}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] exportActions(@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.actionService.exportActions(size, page, adminSmartspace, adminEmail).stream()
				.map(ActionBoundary::new).collect(Collectors.toList()).toArray(new ActionBoundary[0]);
	}

	@RequestMapping(path = "/smartspace/actions",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeAction(@RequestBody ActionBoundary action) {

		Object rv = this.actionService.handleAction(action.convertToEntity(), action.getPlayer().getSmartspace(),
				action.getPlayer().getEmail(),null);
		if (rv instanceof ElementEntity)
			return new ElementBoundary((ElementEntity) rv);
		else if (rv instanceof UserEntity)
			return new UserBoundary((UserEntity) rv);
		else
			return new ActionBoundary((ActionEntity) rv);

	}

}
