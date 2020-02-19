package rigel.coordinates;

import static java.lang.Math.PI;
import static rigel.Preconditions.checkInInterval;
import static rigel.math.Angle.TAU;

import java.util.Locale;

import rigel.math.Angle;
import rigel.math.ClosedInterval;
import rigel.math.Interval;
import rigel.math.RightOpenInterval;

public final class EquatorialCoordinates extends SphericalCoordinates {
	
    private static final Interval RA_INTERVAL = RightOpenInterval.of(0, TAU);
    private static final Interval DEC_INTERVAL = ClosedInterval.symmetric(PI);

    public static EquatorialCoordinates of(double ra, double dec) {
        return new EquatorialCoordinates(
                checkInInterval(RA_INTERVAL, ra),
                checkInInterval(DEC_INTERVAL, dec));
    }

    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
    }

    public double ra() {
        return super.lon();
    }

    public double raDeg() {
        return super.lonDeg();
    }

    public double raHr() {
        return Angle.toHr(ra());
    }

    public double dec() {
        return super.lat();
    }

    public double decDeg() {
        return super.latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }
}
