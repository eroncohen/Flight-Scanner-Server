package smartspace.plugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import smartspace.data.ElementEntity;
import smartspace.data.Properties;
import smartspace.plugin.skyScannerBoundary.Carrier;

import smartspace.plugin.skyScannerBoundary.Leg;
import smartspace.plugin.skyScannerBoundary.Place;
import smartspace.plugin.skyScannerBoundary.Route;
import smartspace.plugin.skyScannerBoundary.Segment;

@Service
public class FlightAgentServiceSkyScanner implements FlightAgentService {

	private String[] APIkey = { "829af922c7mshb81cfc1a0f08c2fp13565ejsna07216e7de10",
			"7fa5858dcfmshd98f8fad2777c6cp1c7f54jsncbd98348b863", "57e4777b3fmsh99c19fe9fa25aa5p16ae04jsn617bad3df955",
			"aae4ef1091msh498cd400a0ed953p1182afjsnd2f89de056e4" };

	@Override
	public ElementEntity FindFlight(Date inbound, Date outbound, String origin, String dest, ElementEntity route,
			String currency) {

		Random rand = new Random();
		HttpResponse<JsonNode> response = null;
		String sessionKey = null;
		int index;
		
		do {
			
			do {
				index = rand.nextInt(this.APIkey.length);
				response = createSessionWithSkyScanner(this.APIkey[index], formatDate(inbound), formatDate(outbound),
						currency, origin, dest);
				sessionKey = response.getHeaders().getFirst("Location");

				if (sessionKey != null)
					sessionKey = this.getSessionKey(sessionKey);
			} while (sessionKey == null || response.getStatus() == HttpStatus.TOO_MANY_REQUESTS.value());// 429 too many
																										 // requests
			response = this.createPollSession(sessionKey, this.APIkey[index]);

		} while (response == null);

		TreeMap<Double, Route> flight = convertJsonToFlights(response.getBody().getObject());

		route.getMoreAttributes().put(Properties.FLIGHTS, flight.values().toArray());

		Map.Entry<Double, Route> entry = flight.entrySet().iterator().next();
		route.getMoreAttributes().put(Properties.MIN_PRICE, entry.getKey());

		return route;
	}

	/**
	 * cheaperFlight() this function creates Session with Sky scanner if the updated
	 * price are cheaper return true else return false
	 */

	@Override
	public double cheaperFlight(Date inbound, Date outbound, String origin, String dest, String currency) {
		HttpResponse<JsonNode> response;

		do {
			response = this.createQuoteSession(formatDate(inbound), formatDate(inbound), origin, dest, currency);
		} while (response == null);
		
		double updatedPrice;
		try {
			updatedPrice = SkyScannerParser.parseQuotesMinPrice(response.getBody().getObject());
		} catch (Exception e) {
			return -1.0;
		}

		return updatedPrice;
	}

	private HttpResponse<JsonNode> createSessionWithSkyScanner(String APIkey, String inboundDate, String outboundDate,
			String currency, String origin, String destination) {
		try {
			return Unirest
					.post("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/v1.0")
					.header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
					.header("X-RapidAPI-Key", APIkey).header("Content-Type", "application/x-www-form-urlencoded")
					.header("Accept", "application/json").field("inboundDate", inboundDate)
					.field("cabinClass", "business").field("children", 0).field("infants", 0).field("country", "IL")
					.field("currency", currency).field("locale", "en-US").field("originPlace", origin + "-sky")
					.field("destinationPlace", destination + "-sky").field("outboundDate", outboundDate)
					.field("adults", 1).asJson();
		} catch (Exception e) {
			return null;
		}

	}

	private HttpResponse<JsonNode> createPollSession(String sessionKey, String APIkey) {
		int j = 1;
		try {
			HttpResponse<JsonNode> ans = null;
			do {
				for (int i = 0; i < 100000; i++);
				ans = Unirest.get(
						"https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/uk2/v1.0/"
								+ sessionKey + "?pageIndex=0&pageSize=10")
						.header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
						.header("X-RapidAPI-Key", APIkey).asJson();

				System.err.println(ans.getBody().getObject().getString("Status") + "...." + j++);

			} while (!ans.getBody().getObject().getString("Status").equals("UpdatesComplete"));
			return ans;
		} catch (UnirestException e) {
			return null;
		}
	}

	private HttpResponse<JsonNode> createQuoteSession(String inboundDate, String outboundDate, String origin,
			String dest, String currency) {
		Random rand = new Random();
		try {
			return Unirest.get(
					"https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browsequotes/v1.0/IL/"
							+ currency + "/en-US/" + origin + "-sky/" + dest + "-sky/" + outboundDate
							+ "?inboundpartialdate=" + inboundDate)
					.header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
					.header("X-RapidAPI-Key", this.APIkey[rand.nextInt(this.APIkey.length)]).asJson();
		} catch (UnirestException e) {
			return null;
		}
	}

	private String getSessionKey(String location) {
		final String notNeededpath = "http://partners.api.skyscanner.net/apiservices/pricing/uk2/v1.0/";

		return location.replace(notNeededpath, "");
	}

	private TreeMap<Double, Route> convertJsonToFlights(JSONObject response_asJson) {

		HashMap<Integer, Carrier> carriers = null;
		HashMap<Integer, Place> places = null;
		HashMap<String, Leg> legs = null;
		HashMap<Integer, Segment> segments = null;

		try {
			// parsing Carriers
			carriers = SkyScannerParser.parseCarriers(response_asJson.getJSONArray("Carriers"));
			// parsing Places
			places = SkyScannerParser.parsePlaces(response_asJson.getJSONArray("Places"));
			// parsing Legs
			legs = SkyScannerParser.parseLegs(response_asJson.getJSONArray("Legs"));
			// parsing Segments
			segments = SkyScannerParser.parseSegments(response_asJson.getJSONArray("Segments"));

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// parsing Itineraries used TreeMap to sort the itineraries by price
		TreeMap<Double, Route> minPrice_itineraries = SkyScannerParser
				.parseItiniraries(response_asJson.getJSONArray("Itineraries"), legs, segments, places, carriers);

		return minPrice_itineraries;
	}

	private String formatDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
}
