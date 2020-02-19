package rigel.coordinates;

import static java.lang.Math.PI;
import static rigel.Preconditions.checkInInterval;
import static rigel.math.Angle.TAU;

import java.util.Locale;

import rigel.math.ClosedInterval;
import rigel.math.Interval;
import rigel.math.RightOpenInterval;

public final class EclipticCoordinates extends SphericalCoordinates {
	
    private static final Interval LON_INTERVAL = RightOpenInterval.of(0, TAU);
    private static final Interval LAT_INTERVAL = ClosedInterval.symmetric(PI);

    public static EclipticCoordinates of(double lon, double lat) {
        return new EclipticCoordinates(
                checkInInterval(LON_INTERVAL, lon),
                checkInInterval(LAT_INTERVAL, lat));
    }

    private EclipticCoordinates(double lon, double lat) {
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
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(), latDeg());
    }
}
