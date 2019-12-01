package smartspace.plugin.skyScannerBoundary;

import java.util.Date;

public class Flight {
	private Date departureDate;
	private Date arrivalDate;
	private String origin;
	private String destination;
	private int duration;
	private String carrierCode;
	private String carrierImgUrl;

	public Flight(Date departureDate, Date arrival, String origin, String destination, int duration,
			String carrierCode, String carrierImgUrl) {
		super();
		this.departureDate = departureDate;
		this.arrivalDate = arrival;
		this.origin = origin;
		this.destination = destination;
		this.duration = duration;
		this.carrierCode = carrierCode;
		this.carrierImgUrl = carrierImgUrl;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getCarrierImgUrl() {
		return carrierImgUrl;
	}

	public void setCarrierImgUrl(String carrierImgUrl) {
		this.carrierImgUrl = carrierImgUrl;
	}

	@Override
	public String toString() {
		return "FlightForDisplay [departureDate=" + departureDate + ", arrivalDate=" + arrivalDate + ", origin="
				+ origin + ", destination=" + destination + ", duration=" + duration + ", carrierCode=" + carrierCode
				+ ", carrierImgUrl=" + carrierImgUrl + "]";
	}

}
