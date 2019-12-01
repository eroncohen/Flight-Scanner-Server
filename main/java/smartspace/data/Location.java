package smartspace.data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Location {

	private double x;
	private double y;

	public Location() {

	}

	public Location(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	@Column(name="x")
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	@Column(name="y")
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Location [x=" + x + ", y=" + y + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Location))
			return false;
		Location that = (Location) o;
		return getX() == that.getX()
				&& getY() == that.getY();
	}
}
