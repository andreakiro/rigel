package rigel.coordinates;

import static rigel.Preconditions.checkInInterval;

import java.util.Locale;

import rigel.math.Angle;
import rigel.math.ClosedInterval;
import rigel.math.Interval;
import rigel.math.RightOpenInterval;

public final class GeographicCoordinates extends SphericalCoordinates {
	
    private static final Interval LON_DEG_INTERVAL = RightOpenInterval.symmetric(360);
    private static final Interval LAT_DEG_INTERVAL = ClosedInterval.symmetric(180);

    public static boolean isValidLonDeg(double lonDeg) {
        return LON_DEG_INTERVAL.contains(lonDeg);
    }

    public static boolean isValidLatDeg(double latDeg) {
        return LAT_DEG_INTERVAL.contains(latDeg);
    }

    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg) {
        return new GeographicCoordinates(
                Angle.ofDeg(checkInInterval(LON_DEG_INTERVAL, lonDeg)),
                Angle.ofDeg(checkInInterval(LAT_DEG_INTERVAL, latDeg)));
    }

    private GeographicCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    @Override
    public double lon() {
        return super.lon();
    }

    @Override
    public double lonDeg() {
        return super.lonDeg();
    }

    @Override
    public double lat() {
        return super.lat();
    }

    @Override
    public double latDeg() {
        return super.latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }
}
