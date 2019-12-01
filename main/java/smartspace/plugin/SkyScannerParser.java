package smartspace.plugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import smartspace.plugin.skyScannerBoundary.Carrier;
import smartspace.plugin.skyScannerBoundary.Flight;
import smartspace.plugin.skyScannerBoundary.Leg;
import smartspace.plugin.skyScannerBoundary.Place;
import smartspace.plugin.skyScannerBoundary.Route;
import smartspace.plugin.skyScannerBoundary.Segment;

public class SkyScannerParser {

	public static HashMap<Integer, Carrier> parseCarriers(JSONArray carrier_asJson)  {
		HashMap<Integer, Carrier> carriers = new HashMap<Integer, Carrier>();
		for (int i = 0; i < carrier_asJson.length(); i++) {

			JSONObject temp = (JSONObject) carrier_asJson.get(i);
			carriers.put(temp.getInt("Id"), new Carrier(temp.getInt("Id"), temp.getString("Code"),
					temp.getString("Name"), temp.getString("ImageUrl"), temp.getString("DisplayCode")));
		}
		return carriers;

	}

	public static HashMap<Integer, Place> parsePlaces(JSONArray places_asJson) {
		HashMap<Integer, Place> places = new HashMap<Integer, Place>();

		for (int i = 0; i < places_asJson.length(); i++) {
			JSONObject temp = (JSONObject) places_asJson.get(i);
			places.put(temp.getInt("Id"),
					new Place(temp.getInt("Id"), temp.has("ParentId") ? temp.getInt("ParentId") : 0,
							temp.getString("Code"), temp.getString("Type"), temp.getString("Name")));
		}
		return places;
	}

	public static HashMap<String, Leg> parseLegs(JSONArray legs_asJson) throws ParseException {

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

	public static HashMap<Integer, Segment> parseSegments(JSONArray segments_asJson) throws ParseException {

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

	public static TreeMap<Double, Route> parseItiniraries(JSONArray itineraries_asJson, HashMap<String, Leg> legs,
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

	private static int[] parseSegmentIds(JSONArray SegmentIds_asJson) {
		int segmentsId[] = new int[SegmentIds_asJson.length()];

		for (int j = 0; j < segmentsId.length; j++)
			segmentsId[j] = (int) SegmentIds_asJson.get(j);

		return segmentsId;
	}

	private static int[] parseCarriersId(JSONArray carriersLegs) {

		int[] legCarriers = new int[carriersLegs.length()];

		for (int j = 0; j < carriersLegs.length(); j++) {
			legCarriers[j] = (int) carriersLegs.get(j);
		}
		return legCarriers;
	}

	private static int[] parseStops(JSONObject jsonObject) {

		JSONArray stops_asJson = jsonObject.getJSONArray("Stops");
		int[] stops = new int[stops_asJson.length()];

		for (int j = 0; j < stops.length; j++)
			stops[j] = stops_asJson.getInt(j);

		return stops;
	}

	private static Flight[] getSegments(int[] SegmentIds, HashMap<Integer, Segment> segments,
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

	public static double parseQuotesMinPrice(JSONObject json) throws Exception{
		JSONArray quotes = json.getJSONArray("Quotes");
		JSONObject quote = (JSONObject) quotes.get(0);
		double price = quote.getDouble("MinPrice");
		return price;
	}


}
