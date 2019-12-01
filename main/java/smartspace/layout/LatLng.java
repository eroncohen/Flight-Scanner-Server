package smartspace.layout;

public class LatLng {
	private double lat;
	private double lng;

	public LatLng() {

	}

	public LatLng(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof LatLng))
			return false;
		LatLng that = (LatLng) o;
		return getLat() == that.getLat() && getLng() == that.getLng();
	}

	@Override
	public String toString() {
		return "LatLng [lat=" + lat + ", lng=" + lng + "]";
	}
}
