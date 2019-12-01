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

import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.logic.ElementService;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class ElementContoller {

	private ElementService elementService;

	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}
	
	@RequestMapping(path = "/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] importElements(
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestBody ElementBoundary[] elements) {
		//convert to list of entities
		List<ElementEntity> elementEntities = Arrays.asList(elements).stream().map(element -> element.convertToEntity())
				.collect(Collectors.toList());

		return this.elementService.importElements(elementEntities,adminSmartspace,adminEmail).stream()
				.map(ElementBoundary::new).collect(Collectors.toList()).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(path = "/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] exportElements(
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		
		return this.elementService.getElements(size, page,adminSmartspace,adminEmail,null).stream()
				.map(ElementBoundary::new).collect(Collectors.toList()).toArray(new ElementBoundary[0]);
		
	}
	
	@RequestMapping(path = "/smartspace/elements/{managerSmartspace}/{managerEmail}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary createElement(
			@PathVariable("managerSmartspace") String managerSmartspace,
			@PathVariable("managerEmail") String managerEmail,
			@RequestBody ElementBoundary elementBoundary) {

		return new ElementBoundary(this.elementService.createElement(elementBoundary.convertToEntity(),managerSmartspace,managerEmail));

	}
	
	@RequestMapping(path = "/smartspace/elements/{managerSmartspace}/{managerEmail}/{elementSmartspace}/{elementId}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(
			@PathVariable("managerSmartspace") String managerSmartspace,
			@PathVariable("managerEmail") String managerEmail,
			@PathVariable("elementSmartspace") String elementSmartspace,
			@PathVariable("elementId") String elementId,
			@RequestBody ElementBoundary update) {

		ElementEntity entity = update.convertToEntity();
		entity.setKey(new ElementKey(elementSmartspace, elementId));
		this.elementService.updateElement(entity,managerSmartspace,managerEmail);
	}
	
	@RequestMapping(path = "/smartspace/elements/{userSmartspace}/{userEmail}/{elementSmartspace}/{elementId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary getElement(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("elementSmartspace") String elementSmartspace,
			@PathVariable("elementId") String elementId) {
		return new ElementBoundary(this.elementService.getElementByKey(elementSmartspace, elementId,userSmartspace,userEmail,null));
	}
	
	@RequestMapping(path = "/smartspace/elements/{userSmartspace}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElements(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.elementService.getElements(size, page,userSmartspace,userEmail,null).stream().map(ElementBoundary::new)
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(path = "/smartspace/elements/{userSmartspace}/{userEmail}",
			params = "search=type",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementByType(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "value") String type,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.elementService.getElementsByType(type, size, page,userSmartspace,userEmail,null).stream().map(ElementBoundary::new)
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(path = "/smartspace/elements/{userSmartspace}/{userEmail}",
			params = "search=name",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementByName(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "value") String name,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		return this.elementService.getElementsByName(name, size, page,userSmartspace,userEmail,null).stream().map(ElementBoundary::new)
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(path = "/smartspace/elements/{userSmartspace}/{userEmail}",
			params = "search=location",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementByNearLocation(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "x") double x,
			@RequestParam(name = "y") double y,
			@RequestParam(name = "distance") double distance,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.elementService.getElementsByLocationOnDistance(x, y, distance, size, page,userSmartspace,userEmail,null).stream()
				.map(ElementBoundary::new).collect(Collectors.toList()).toArray(new ElementBoundary[0]);
	}

//	  { "key":{ "id":"50", "smartspace":"mysmartspace" }, "elementType":"myType",
//	  "name":"demoElement", "expired":false,
//	  "created":"2019-04-13T13:15:00.669+0000", "creator":{
//	  "email":"manager.creating.element@de.mo", "smartspace":"mysmartspace" },
//	  "latlng":{ "lat":32.115, "lng":84.817 }, "elementProperties":{
//	  "key1":"somthing", "key2":1, "lastkey":"Done" } }

}
