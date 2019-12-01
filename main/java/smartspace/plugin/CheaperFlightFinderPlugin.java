package smartspace.plugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import smartspace.dao.AdvancedActionDao;
import smartspace.dao.AdvancedElementDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.ElementKey;
import smartspace.data.Properties;

@Component
public class CheaperFlightFinderPlugin implements SmartspacePlugin {

	private AdvancedActionDao actionDao;
	private AdvancedElementDao<ElementKey> elementDao;
	private FlightAgentService agent;
	private final double minDistanceBetweenAirports = 0.2;
	private final String timerEmail = "myTimer@Timer";
	private String smartspace = "2019b.rickyd";

	@Autowired
	public CheaperFlightFinderPlugin(AdvancedActionDao actionDao, AdvancedElementDao<ElementKey> elementDao,
			FlightAgentService agent) {
		this.actionDao = actionDao;
		this.elementDao = elementDao;
		this.agent = agent;
	}

	@Override
	public Object process(ActionEntity action) {
		int i = 0;
		int size = 10;

		validateTimerKey(action);

		List<ActionEntity> subscribers;
		Date inbound = null;
		Date outbound = null;

		do {
			subscribers = this.actionDao.readActionsByType(Properties.SUBSCRIBE, size, i++);
			
			for (int j = 0; j < subscribers.size(); j++) {

				ActionEntity subscriber = subscribers.get(j);

				inbound = this.getBoundDate((String) subscriber.getMoreAttributes().get(Properties.ARRIVAL_DATE));

				outbound = this.getBoundDate((String) subscriber.getMoreAttributes().get(Properties.DEPARTURE_DATE));

				if (validateFlight(outbound, inbound, subscriber.getElementId(),
						subscribers.get(j).getElementSmartspace())) {

					String origin = (String) subscriber.getMoreAttributes().get(Properties.ORIGIN);

					String destination = (String) subscriber.getMoreAttributes().get(Properties.DESTINATION);

					String currency = (String) this.elementDao
							.readElementsByName((String) subscriber.getMoreAttributes().get(Properties.CURRENCY), 1, 0)
							.get(0).getMoreAttributes().get(Properties.CODE);
					Double minPrice = Double
							.parseDouble((String) subscriber.getMoreAttributes().get(Properties.MIN_PRICE));// the minimum price of the subscription
					Double updatedPrice = this.agent.cheaperFlight(inbound, outbound, origin, destination, currency);// get updated minimum price

					if (updatedPrice != -1 && updatedPrice < minPrice) {
						ElementKey key = new ElementKey(subscriber.getElementSmartspace(), subscriber.getElementId());
						ElementEntity route = new ElementEntity(this.elementDao.readById(key).get());
						this.updateSubscription(subscriber, updatedPrice);
						this.sendMail(route, inbound, outbound, subscriber);
					}
					
				} else {
					ActionEntity expierdSubscriber = new ActionEntity();
					expierdSubscriber.setKey(subscriber.getKey());
					expierdSubscriber.setType(Properties.EXPIRED_SUBCRIPTION);
					this.actionDao.update(expierdSubscriber);
				}
			}

		} while (subscribers.size() == size);

		return null;
	}

	private void updateSubscription(ActionEntity subscriber, double updatedPrice) {

		ActionEntity updated_sub = new ActionEntity();
		updated_sub.setKey(subscriber.getKey());
		updated_sub.setMoreAttributes(subscriber.getMoreAttributes());
		updated_sub.getMoreAttributes().put(Properties.MIN_PRICE, updatedPrice + "");
		updated_sub.setType(subscriber.getType());
		this.actionDao.update(updated_sub);

	}

	private void sendMail(ElementEntity route, Date inbound, Date outbound, ActionEntity subscriber) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy");

		Map<String, Double> origLoc = (Map<String, Double>) route.getMoreAttributes().get("origLoc");
		Map<String, Double> destLoc = (Map<String, Double>) route.getMoreAttributes().get("destLoc");

		String cityOrig = this.getMunicipality(origLoc);
		String cityDest = this.getMunicipality(destLoc);

		String url = "http:/" + "/localhost:3000/player.html?longitudeInputOrig=" + origLoc.get("x")
				+ "&latitudeInputOrig=" + origLoc.get("y") + "&cityOrig=" + cityOrig + "&datepickerDepart="
				+ formatter.format(outbound) + "&longitudeInputDest=" + destLoc.get("x") + "&latitudeInputDest="
				+ destLoc.get("y") + "&cityDest=" + cityDest + "&datepickerReturn=" + formatter.format(inbound);
		url=url.replace(' ', '_');
		SendMail.sendFromGMail(subscriber.getPlayerEmail(), url);

		// when email is sent then a new action will be created
		ActionEntity sendMailAction = new ActionEntity();
		sendMailAction.setCreationTimeStamp(new Date());
		sendMailAction.setElementId(subscriber.getElementId());
		sendMailAction.setElementSmartspace(subscriber.getElementSmartspace());
		sendMailAction.setPlayerEmail(subscriber.getPlayerEmail());
		sendMailAction.setPlayerSmartspace(subscriber.getElementSmartspace());
		sendMailAction.setType(Properties.SEND_MAIL);
		this.actionDao.create(sendMailAction);

	}

	private String getMunicipality(Map<String, Double> location) {

		ElementEntity airport = (ElementEntity) elementDao.readElementsByTypeAndXBetweenAndYBetween(Properties.AIRPORT,
				location.get("x"), location.get("y"), this.minDistanceBetweenAirports, 30, 0).get(0);
		return (String) airport.getMoreAttributes().get(Properties.MUNICIPLAITY);
	}

	private void validateTimerKey(ActionEntity action) {
		if (!action.getPlayerEmail().equals(this.timerEmail) || !action.getPlayerSmartspace().equals(this.smartspace))
			throw new RuntimeException("YOU ARE NOT AUTHORIZED!");

	}

	private boolean validateFlight(Date outbound, Date inbound, String elementId, String elementSmartspace) {

		Date current = new Date();
		if (outbound.before(current) || inbound.before(current) || inbound.before(outbound))
			return false;
		else {
			ElementEntity element = this.elementDao.readById(new ElementKey(elementSmartspace, elementId)).get();
			if (element != null)
				return !element.isExpired();
			else
				return true;
		}
	}

	private Date getBoundDate(String date) {
		try {
			return new SimpleDateFormat("EEE MMM dd yyyy").parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
