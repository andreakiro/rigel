package rigel.coordinates;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static rigel.Preconditions.checkInInterval;
import static rigel.math.Angle.TAU;

import java.util.Locale;

import rigel.math.Angle;
import rigel.math.ClosedInterval;
import rigel.math.Interval;
import rigel.math.RightOpenInterval;

public final class HorizontalCoordinates extends SphericalCoordinates {
	
    private static final Interval AZ_DEG_INTERVAL = RightOpenInterval.of(0, 360);
    private static final Interval ALT_DEG_INTERVAL = ClosedInterval.symmetric(180);

    private static final Interval AZ_INTERVAL = RightOpenInterval.of(0, TAU);
    private static final Interval ALT_INTERVAL = ClosedInterval.symmetric(PI);

    public static HorizontalCoordinates of(double az, double alt) {
        return new HorizontalCoordinates(
                checkInInterval(AZ_INTERVAL, az),
                checkInInterval(ALT_INTERVAL, alt));
    }

    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        return new HorizontalCoordinates(
                Angle.ofDeg(checkInInterval(AZ_DEG_INTERVAL, azDeg)),
                Angle.ofDeg(checkInInterval(ALT_DEG_INTERVAL, altDeg)));
    }

    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    public double az() {
        return super.lon();
    }

    public double azDeg() {
        return super.lonDeg();
    }

    public String azOctantName(String n, String e, String s, String w) {
        // Note: the "- AZ_INTERVAL.low()" is not strictly necessary, but makes the code clearer.
        var octant = (int) Math.round(8d * (az() - AZ_INTERVAL.low()) / AZ_INTERVAL.size());
        switch (octant) {
            case 8:
            case 0: return n;
            case 1: return n+e;
            case 2: return e;
            case 3: return s+e;
            case 4: return s;
            case 5: return s+w;
            case 6: return w;
            case 7: return n+w;
            default: throw new Error();
        }
    }

    public double alt() {
        return super.lat();
    }

    public double altDeg() {
        return super.latDeg();
    }

    public double angularDistanceTo(HorizontalCoordinates that) {
        var azDiff = that.az() - this.az();
        return acos(sin(this.alt()) * sin(that.alt()) + cos(this.alt()) * cos(that.alt()) * cos(azDiff));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }
}
