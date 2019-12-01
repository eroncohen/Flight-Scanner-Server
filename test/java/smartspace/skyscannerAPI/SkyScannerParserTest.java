package smartspace.skyscannerAPI;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import smartspace.plugin.skyScannerBoundary.Carrier;
import smartspace.plugin.skyScannerBoundary.Flight;
import smartspace.plugin.skyScannerBoundary.Leg;
import smartspace.plugin.skyScannerBoundary.Place;
import smartspace.plugin.skyScannerBoundary.Route;
import smartspace.plugin.skyScannerBoundary.Segment;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })

public class SkyScannerParserTest {

	@Test
	public void testParsingBodyResponse() throws Exception {
		
		String[] APIkey = { 
				"829af922c7mshb81cfc1a0f08c2fp13565ejsna07216e7de10",
				"7fa5858dcfmshd98f8fad2777c6cp1c7f54jsncbd98348b863",
				"57e4777b3fmsh99c19fe9fa25aa5p16ae04jsn617bad3df955",
				"aae4ef1091msh498cd400a0ed953p1182afjsnd2f89de056e4"};
		Random rand = new Random();

		/**
		 * GIVEN I created Session with Sky Scanner (POST) // AND I have the sessionKey
		 */
		HttpResponse<JsonNode> response = null;
		String sessionKey = null;
		int n;
		
		do {	// "PLASTER"
			
			do {
				n = rand.nextInt(100) % 4;
				response = createSessionWithSkyScanner(APIkey[n]);
				//countRequests++;
			} while (response == null || response.getStatus() == HttpStatus.TOO_MANY_REQUESTS.value());// 429 too many
																										// requests
			sessionKey = this.getSessionKey(response.getHeaders().getFirst("Location"));
			response = this.createPollSession(sessionKey,APIkey[n]);
			//countRequests++;
		} while (response == null);
		
		/** WHEN I parse the Response body */
		
		TreeMap<Double, Route> itineraries = null;// used TreeMap to sort the itineraries by price
		itineraries = convertJsonToFlights(response.getBody().getObject());
		
		/**
		 * THEN the fields of the created objects are not null 
		 * AND the same size of the JSON Array 		
		 */
		// ### get the first value of the map "min_price"
		Map.Entry<Double, Route> entry = itineraries.entrySet().iterator().next();
		Double key = entry.getKey();
		Route value = entry.getValue();
		System.err.println(key);
		System.err.println(value);
		
		assertThat(itineraries).isNotNull();
	}
	
	private TreeMap<Double, Route> convertJsonToFlights(JSONObject jsonObject) throws JSONException, ParseException {
		
		HashMap<Integer, Carrier> carriers = null;
		HashMap<Integer, Place> places = null;
		HashMap<String, Leg> legs = null;
		HashMap<Integer, Segment> segments = null;
		TreeMap<Double, Route> itineraries = null;// used TreeMap to sort the itineraries by price
		
		//parsing Carriers
		carriers = this.parseCarriers(jsonObject.getJSONArray("Carriers"));
		
		//parsing Places
		places = this.parsePlaces(jsonObject.getJSONArray("Places"));
		
		//parsing Legs
		legs = this.parseLegs(jsonObject.getJSONArray("Legs"));
		
		//parsing Segments
		segments = this.parseSegments(jsonObject.getJSONArray("Segments"));
		
		//parsing Itineraries
		itineraries = this.parseItiniraries(jsonObject.getJSONArray("Itineraries"), legs, segments,
				places, carriers);

		return itineraries;
	}
	
	//################ SkyScanner connectivity ################
	private HttpResponse<JsonNode> createSessionWithSkyScanner(String APIkey) {

		try {
			return Unirest.post("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/v1.0")
					.header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
					.header("X-RapidAPI-Key", APIkey)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.field("inboundDate", "2019-09-10")
					.field("cabinClass", "business")
					.field("children", 0)
					.field("infants", 0)
					.field("country", "US")
					.field("currency", "USD")
					.field("locale", "en-US")
					.field("originPlace", "SFO-sky")
					.field("destinationPlace", "LHR-sky")
					.field("outboundDate", "2019-09-01")
					.field("adults", 1).asJson();
		} catch (Exception e) {
			return null;
		}
		
	}

	private String getSessionKey(String location) {
		
		final String notNeededpath = "http://partners.api.skyscanner.net/apiservices/pricing/uk2/v1.0/";
		return location.replace(notNeededpath, "");
		
	}

	private HttpResponse<JsonNode> createPollSession(String sessionKey, String APIkey) {
		try {
			return Unirest
					.get("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/uk2/v1.0/"
							+ sessionKey + "?pageIndex=0&pageSize=10")
					.header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
					.header("X-RapidAPI-Key", APIkey).asJson();
		} catch (UnirestException e) {
			return null;
		}
	}
	//################ SkyScanner connectivity ################
	
	
	// ########################## PARSING ##########################

	private HashMap<Integer, Carrier> parseCarriers(JSONArray carrier_asJson) throws JSONException {
		HashMap<Integer, Carrier> carriers = new HashMap<Integer, Carrier>();
		for (int i = 0; i < carrier_asJson.length(); i++) {

			JSONObject temp = (JSONObject) carrier_asJson.get(i);
			carriers.put(temp.getInt("Id"), new Carrier(temp.getInt("Id"), temp.getString("Code"),
					temp.getString("Name"), temp.getString("ImageUrl"), temp.getString("DisplayCode")));
		}
		return carriers;

	}

	private HashMap<Integer, Place> parsePlaces(JSONArray places_asJson) throws JSONException {
		HashMap<Integer, Place> places = new HashMap<Integer, Place>();

		for (int i = 0; i < places_asJson.length(); i++) {
			JSONObject temp = (JSONObject) places_asJson.get(i);
			places.put(temp.getInt("Id"),
					new Place(temp.getInt("Id"), temp.has("ParentId") ? temp.getInt("ParentId") : 0,
							temp.getString("Code"), temp.getString("Type"), temp.getString("Name")));
		}
		return places;
	}

	private HashMap<String, Leg> parseLegs(JSONArray legs_asJson) throws JSONException, ParseException {

		HashMap<String, Leg> legs = new HashMap<String, Leg>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		for (int i = 0; i < legs_asJson.length(); i++) {
			JSONObject temp = (JSONObject) legs_asJson.get(i);
			int[] stops = parseStops(temp);
			int[] carriersIds = parseCarriersId(temp.getJSONArray("Carriers"));
			int[] segmentsIds = parseSegmentIds(temp.getJSONArray("SegmentIds"));
			String legID = temp.getString("Id");

			// USING a new constructor
			legs.put(legID,
					new Leg(legID, segmentsIds, temp.getInt("OriginStation"), temp.getInt("DestinationStation"),
							formatter.parse(temp.getString("Departure")), formatter.parse(temp.getString("Arrival")),
							stops, carriersIds, temp.getString("Directionality")));
		}

		return legs;
	}

	private HashMap<Integer, Segment> parseSegments(JSONArray segments_asJson) throws JSONException, ParseException {

		HashMap<Integer, Segment> segment = new HashMap<Integer, Segment>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		for (int i = 0; i < segments_asJson.length(); i++) {
			JSONObject temp = (JSONObject) segments_asJson.get(i);

			segment.put(temp.getInt("Id"),
					new Segment(temp.getInt("Id"), temp.getInt("OriginStation"), temp.getInt("DestinationStation"),
							formatter.parse(temp.getString("DepartureDateTime")),
							formatter.parse(temp.getString("ArrivalDateTime")), temp.getInt("Duration"), "flight",
							temp.getInt("Carrier"), temp.getInt("OperatingCarrier"), temp.getString("Directionality"),
							temp.getInt("FlightNumber")));
		}
		return segment;
	}

	// ############################ ADDED ########################################

	private TreeMap<Double, Route> parseItiniraries(JSONArray itineraries_asJson, HashMap<String, Leg> legs,
			HashMap<Integer, Segment> segments, HashMap<Integer, Place> places, HashMap<Integer, Carrier> carriers) {
		
		TreeMap<Double, Route> minprice_itiniraries = new TreeMap<>();

		for (int i = 0; i < itineraries_asJson.length(); i++) {

			HashMap<Double, String> price = new HashMap<Double, String>();

			JSONObject temp = (JSONObject) itineraries_asJson.get(i);
			String outLegId = temp.getString("OutboundLegId");
			String inLegId = temp.getString("InboundLegId");

			JSONArray pricingOptions = temp.getJSONArray("PricingOptions");

			for (int j = 0; j < pricingOptions.length(); j++) {
				JSONObject pricingOptionsIter = (JSONObject) pricingOptions.get(j);
				price.put(pricingOptionsIter.getDouble("Price"), pricingOptionsIter.getString("DeeplinkUrl"));
			}

			SortedSet<Double> keys = new TreeSet<Double>(price.keySet());

			Leg currentOut = legs.get(outLegId);
			Leg currentIn = legs.get(inLegId);

			int[] inSegs = currentIn.getSegmentIds();
			int[] outSegs = currentOut.getSegmentIds();

			Flight[] out = getSegments(outSegs, segments, places, carriers);
			Flight[] in = getSegments(inSegs, segments, places, carriers);

			Route ans = new Route(out, in, out.length - 1, in.length - 1,
					keys.first().doubleValue(), price.get(keys.first()));
			minprice_itiniraries.put(ans.getPrice(), ans);
		}
		return minprice_itiniraries;
	}

	private int[] parseSegmentIds(JSONArray SegmentIds_asJson) {
		int segmentsId[] = new int[SegmentIds_asJson.length()];

		for (int j = 0; j < segmentsId.length; j++)
			segmentsId[j] = (int) SegmentIds_asJson.get(j);

		return segmentsId;
	}

	private int[] parseCarriersId(JSONArray carriersLegs) {

		int[] legCarriers = new int[carriersLegs.length()];

		for (int j = 0; j < carriersLegs.length(); j++) {
			legCarriers[j] = (int) carriersLegs.get(j);
		}
		return legCarriers;
	}

	private int[] parseStops(JSONObject jsonObject) {

		JSONArray stops_asJson = jsonObject.getJSONArray("Stops");
		int[] stops = new int[stops_asJson.length()];

		for (int j = 0; j < stops.length; j++)
			stops[j] = stops_asJson.getInt(j);

		return stops;
	}

	private Flight[] getSegments(int[] SegmentIds, HashMap<Integer, Segment> segments,
			HashMap<Integer, Place> places, HashMap<Integer, Carrier> carriers) {
		Flight[] segs = new Flight[SegmentIds.length];

		for (int j = 0; j < segs.length; j++) {

			Segment currentSeg = segments.get(SegmentIds[j]);
			Place origin = places.get(currentSeg.getOriginStation());
			Place destination = places.get(currentSeg.getDestinationStation());
			Carrier currentCarry = carriers.get(currentSeg.getCarrier());

			segs[j] = new Flight(currentSeg.getDepartureDateTime(), currentSeg.getDateArrivalDateTimer(),
					origin.getCode(), destination.getCode(), currentSeg.getDuartion(), currentCarry.getCode(),
					currentCarry.getImageUrl());
		}

		return segs;
	}

	// ############################ ADDED ########################################
}
