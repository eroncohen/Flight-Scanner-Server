package smartspace.plugin.skyScannerBoundary;

import java.util.Arrays;
import java.util.Date;

public class Leg {

	private String Id;
	private int[] SegmentIds;
	private int OriginStation;
	private int DestinationStation;
	private Date Departure;
	private Date Arrival;
	private int[] Stops;
	private int[] OperationCarriers;
	private String Directionality;
	
	private int Duartion;// is needed?
	private String JourneyMode;// is needed?
	private int segId[];//Duplicate?
	
	public Leg(String id, int[] segmentIds, int originStation, int destinationStation, Date departure, Date arrival,
			 int[] stops, int[] operationCarriers, String directionality) {
		super();
		this.Id = id;
		this.SegmentIds = segmentIds;
		this.OriginStation = originStation;
		this.DestinationStation = destinationStation;
		this.Departure = departure;
		this.Arrival = arrival;
		this.Stops = stops;
		this.OperationCarriers = operationCarriers;
		this.Directionality = directionality;
	}

	//################################ DELETE? ################################
	public Leg(String id, int originId, int destinationId, Date depart, Date arrival, int[] stops, int[] cary,
			int[] segId, String Directionality) {

		this.Id = id;
		this.OriginStation = originId;
		this.DestinationStation = destinationId;
		this.Arrival = arrival;
		this.Departure = depart;
		this.Directionality = Directionality;
		this.Stops = stops;
		this.OperationCarriers = cary;
		this.segId = segId;
	}
	
	public Leg(String id, int[] segmentIds, int originStation, int destinationStation, Date departure, Date arrival,
			int duartion, String journeyMode, int[] stops, int[] operationCarriers, int[] segId,
			String directionality) {
		super();
		this.Id = id;
		this.SegmentIds = segmentIds;
		this.OriginStation = originStation;
		this.DestinationStation = destinationStation;
		this.Departure = departure;
		this.Arrival = arrival;
		this.Duartion = duartion;
		this.JourneyMode = journeyMode;
		this.Stops = stops;
		this.OperationCarriers = operationCarriers;
		this.Directionality = directionality;
		this.segId = segId;
	}
	//#######################################################################
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		this.Id = id;
	}

	public int[] getSegmentIds() {
		return this.SegmentIds;
	}

	public void setSegmentIds(int[] segmentIds) {
		this.SegmentIds = segmentIds;
	}

	public int getOriginStation() {
		return this.OriginStation;
	}

	public void setOriginStation(int originStation) {
		this.OriginStation = originStation;
	}

	public int getDestinationStation() {
		return this.DestinationStation;
	}

	public void setDestinationStation(int destinationStation) {
		this.DestinationStation = destinationStation;
	}

	public Date getDeparture() {
		return this.Departure;
	}

	public void setDeparture(Date departure) {
		this.Departure = departure;
	}

	public Date getArrival() {
		return this.Arrival;
	}

	public void setArrival(Date arrival) {
		this.Arrival = arrival;
	}


	public int[] getStops() {
		return this.Stops;
	}

	public void setStops(int[] stops) {
		this.Stops = stops;
	}

	public int[] getOperationCarriers() {
		return this.OperationCarriers;
	}

	public void setOperationCarriers(int[] operationCarriers) {
		this.OperationCarriers = operationCarriers;
	}

	public String getDirectionality() {
		return this.Directionality;
	}

	public void setDirectionality(String directionality) {
		this.Directionality = directionality;
	}
	
	@Override
	public String toString() {
		return "Leg [Id=" + Id + ","
				+ " SegmentIds=" + Arrays.toString(SegmentIds) 
				+  ", OriginStation=" + OriginStation
				+ ", DestinationStation=" + DestinationStation 
				+ ", Departure=" + Departure 
				+ ", Arrival=" + Arrival
				+ ", Stops=" + Arrays.toString(Stops)
				+ ", OperationCarriers=" + Arrays.toString(OperationCarriers) 
				+ ", Directionality=" + Directionality;
	}

//	@Override
//	public String toString() {
//		return "Leg [Id=" + Id + ", SegmentIds=" + Arrays.toString(SegmentIds) + ", OriginStation=" + OriginStation
//				+ ", DestinationStation=" + DestinationStation + ", Departure=" + Departure + ", Arrival=" + Arrival
//				+ ", Duartion=" + Duartion + ", JourneyMode=" + JourneyMode + ", Stops=" + Arrays.toString(Stops)
//				+ ", OperationCarriers=" + Arrays.toString(OperationCarriers) + ", Directionality=" + Directionality;
//	}

	public int[] getSegId() {
		return segId;
	}

	public void setSegId(int[] segId) {
		this.segId = segId;
	}

	
	public int getDuartion() {
		return this.Duartion;
	}

	public void setDuartion(int duartion) {
		this.Duartion = duartion;
	}
	
	public String getJourneyMode() {
		return this.JourneyMode;
	}

	public void setJourneyMode(String journeyMode) {
		this.JourneyMode = journeyMode;
	}
}
