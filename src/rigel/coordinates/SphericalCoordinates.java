package rigel.coordinates;

import static rigel.math.Angle.toDeg;

public abstract class SphericalCoordinates {
	
	private final double lon, lat;

    SphericalCoordinates(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    double lon() {
        return lon;
    }

    double lonDeg() {
        return toDeg(lon());
    }

    double lat() {
        return lat;
    }

    double latDeg() {
        return toDeg(lat());
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object that) {
        throw new UnsupportedOperationException();
    }
}
