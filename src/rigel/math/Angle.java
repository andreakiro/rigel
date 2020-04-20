package rigel.math;

import static java.lang.Math.*;
import static rigel.Preconditions.checkArgument;

public final class Angle {
    public static final double TAU = scalb(PI, 1);

    private static final RightOpenInterval TURN_INTERVAL = RightOpenInterval.of(0, TAU);

    private static final double HR_PER_RAD = 24d / TAU;
    private static final double RAD_PER_HR = TAU / 24d;

    private static final int MIN_PER_DEG = 60;
    private static final int SEC_PER_MIN = 60;

    private static final double DEG_PER_MIN = 1d / MIN_PER_DEG;
    private static final double DEG_PER_SEC = 1d / (MIN_PER_DEG * SEC_PER_MIN);

    private Angle() {}

    public static double normalizePositive(double rad) {
        return TURN_INTERVAL.reduce(rad);
    }

    public static double ofArcsec(double sec) {
        return ofDeg(sec * DEG_PER_SEC);
    }

    public static double ofDMS(int deg, int min, double sec) {
        checkArgument(0 <= deg);
        checkArgument(0 <= min && min < MIN_PER_DEG);
        checkArgument(0 <= sec && sec < SEC_PER_MIN);
        return ofDeg(deg + min * DEG_PER_MIN + sec * DEG_PER_SEC);
    }

    public static double ofDeg(double deg) {
        return toRadians(deg);
    }

    public static double toDeg(double rad) {
        return toDegrees(rad);
    }

    public static double ofHr(double hr) {
        return hr * RAD_PER_HR;
    }

    public static double toHr(double rad) {
        return rad * HR_PER_RAD;
    }
}
