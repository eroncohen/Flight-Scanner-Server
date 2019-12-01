package smartspace.plugin.skyScannerBoundary;

import java.util.Date;

public class Segment {
	private int Id;
	private int OriginStation;
	private int DestinationStation;
	private Date DepartureDateTime;
	private Date DateArrivalDateTimer;
	private int Duartion;
	private String JourneyMode;
	private int Carrier;
	private int OperationCarrier;
	private String Directionality;
	private int FlightNumber;

	public Segment() {

	}

	public Segment(int id, int originStation, int destinationStation, Date departureDateTime,
			Date dateArrivalDateTimer, int duartion, String journeyMode, int carrier, int operationCarrier,
			String directionality, int flightNumber) {
		super();
		Id = id;
		OriginStation = originStation;
		DestinationStation = destinationStation;
		DepartureDateTime = departureDateTime;
		DateArrivalDateTimer = dateArrivalDateTimer;
		Duartion = duartion;
		JourneyMode = journeyMode;
		Carrier = carrier;
		OperationCarrier = operationCarrier;
		Directionality = directionality;
		FlightNumber = flightNumber;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getOriginStation() {
		return OriginStation;
	}

	public void setOriginStation(int originStation) {
		OriginStation = originStation;
	}

	public int getDestinationStation() {
		return DestinationStation;
	}

	public void setDestinationStation(int destinationStation) {
		DestinationStation = destinationStation;
	}

	public Date getDepartureDateTime() {
		return DepartureDateTime;
	}

	public void setDepartureDateTime(Date departureDateTime) {
		DepartureDateTime = departureDateTime;
	}

	public Date getDateArrivalDateTimer() {
		return DateArrivalDateTimer;
	}

	public void setDateArrivalDateTimer(Date dateArrivalDateTimer) {
		DateArrivalDateTimer = dateArrivalDateTimer;
	}

	public int getDuartion() {
		return Duartion;
	}

	public void setDuartion(int duartion) {
		Duartion = duartion;
	}

	public String getJourneyMode() {
		return JourneyMode;
	}

	public void setJourneyMode(String journeyMode) {
		JourneyMode = journeyMode;
	}

	public int getCarrier() {
		return Carrier;
	}

	public void setCarrier(int carrier) {
		Carrier = carrier;
	}

	public int getOperationCarrier() {
		return OperationCarrier;
	}

	public void setOperationCarrier(int operationCarrier) {
		OperationCarrier = operationCarrier;
	}

	public String getDirectionality() {
		return Directionality;
	}

	public void setDirectionality(String directionality) {
		Directionality = directionality;
	}

	public int getFlightNumber() {
		return FlightNumber;
	}

	public void setFlightNumber(int flightNumber) {
		FlightNumber = flightNumber;
	}

	@Override
	public String toString() {
		return "Segment [Id=" + Id + ", OriginStation=" + OriginStation + ", DestinationStation=" + DestinationStation
				+ ", DepartureDateTime=" + DepartureDateTime + ", DateArrivalDateTimer=" + DateArrivalDateTimer
				+ ", Duartion=" + Duartion + ", JourneyMode=" + JourneyMode + ", Carrier=" + Carrier
				+ ", OperationCarrier=" + OperationCarrier + ", Directionality=" + Directionality + ", FlightNumber="
				+ FlightNumber + "]";
	}

}
