package smartspace.plugin;

import java.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import smartspace.dao.AdvancedElementDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Properties;

@Component
public class FlightRequestPlugin implements SmartspacePlugin {

	private AdvancedElementDao<ElementKey> elementDao;
	private FlightAgentService agent;
	private static double minDistanceBetweenAirports = 0.5;

	@Autowired
	public void setAgent(FlightAgentService agent) {
		this.agent = agent;
	}

	@Autowired
	public void setElementDao(AdvancedElementDao<ElementKey> elementDao) {
		this.elementDao = elementDao;
	}

	// place: "origin"/"destination"
	public String getAirportCode(ActionEntity action, String place) {
		Map<String, Double> locationMap = (Map<String, Double>) action.getMoreAttributes().get(place);
		
		ElementEntity airport = this.elementDao.readElementsByTypeAndXBetweenAndYBetween(Properties.AIRPORT,
				locationMap.get("lng"),locationMap.get("lat"), minDistanceBetweenAirports, 10, 0).get(0);

		return (String) airport.getMoreAttributes().get(Properties.IATA);
	}

	@Override
	public Object process(ActionEntity action) {

		ElementEntity route;

		// get originAirportCode and destAirportCode
		String originAirportCode = this.getAirportCode(action, Properties.ORIGIN);
		String destAirportCode = this.getAirportCode(action, Properties.DESTINATION);

		// get route by the origin and destination
		List<ElementEntity> routeList = this.elementDao.readElementsByTypeAndName(Properties.ROUTE,
				originAirportCode + "_" + destAirportCode, 30, 0);
		Date departureDate = null;
		Date arrivalDate = null;

		if (routeList.isEmpty()) {
			throw new RuntimeException("not supported airport");
		}

		route = new ElementEntity(routeList.get(0)); // copy

		String name = (String) action.getMoreAttributes().get(Properties.CURRENCY);
		ElementEntity currency = (ElementEntity) this.elementDao.readElementsByName(name, 14, 0).get(0);
		route.getMoreAttributes().put(Properties.CURRENCY,currency.getMoreAttributes().get(Properties.SIGN));
		
		// get departure and arrival dates
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy");
		try {

			departureDate = formatter.parse((String) action.getMoreAttributes().get(Properties.DEPARTURE_DATE));
			arrivalDate = formatter.parse((String) action.getMoreAttributes().get(Properties.ARRIVAL_DATE));

		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// set action element and save to database
		action.setElementId(route.getElementId());
		action.setElementSmartspace(route.getElementSmartspace());
		
		return this.agent.FindFlight(arrivalDate, departureDate, originAirportCode, destAirportCode, route,
				(String) currency.getMoreAttributes().get(Properties.CODE));

	}
}
