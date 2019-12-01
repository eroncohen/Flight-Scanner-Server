package smartspace.plugin.skyScannerBoundary;

public class Route {
	private Flight[] outFlights;
	private Flight[] inFlights;
	private int outStops;
	private int inStops;
	private double price;
	private String bookingUrl;

	public Route() {
		super();
	}

	public Route(Flight[] outFlights, Flight[] inFlights, int outStops, int inStops,
			double price, String bookingUrl) {
		super();
		this.outFlights = outFlights;
		this.inFlights = inFlights;
		this.outStops = outStops;
		this.inStops = inStops;
		this.price = price;
		this.bookingUrl = bookingUrl;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getBookingUrl() {
		return bookingUrl;
	}

	public void setBookingUrl(String bookingUrl) {
		this.bookingUrl = bookingUrl;
	}

	public Flight[] getOutFlights() {
		return outFlights;
	}

	public void setOutFlights(Flight[] outFlights) {
		this.outFlights = outFlights;
	}

	public Flight[] getInFlights() {
		return inFlights;
	}

	public void setInFlights(Flight[] inFlights) {
		this.inFlights = inFlights;
	}

	public int getOutStops() {
		return outStops;
	}

	public void setOutStops(int outStops) {
		this.outStops = outStops;
	}

	public int getInStops() {
		return inStops;
	}

	public void setInStops(int inStops) {
		this.inStops = inStops;
	}

}
