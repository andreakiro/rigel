package rigel.math;

import static java.lang.Math.floor;
import static rigel.Preconditions.checkArgument;

import java.util.Locale;

public final class RightOpenInterval extends Interval {
    public static RightOpenInterval of(double low, double high) {
        checkArgument(low < high);
        return new RightOpenInterval(low, high);
    }

    public static RightOpenInterval symmetric(double size) {
        checkArgument(size > 0);
        var halfSize = size / 2d;
        return new RightOpenInterval(-halfSize, halfSize);
    }

    private RightOpenInterval(double low, double high) {
        super(low, high);
    }

    @Override
    public boolean contains(double v) {
        return low() <= v && v < high();
    }

    public double reduce(double v) {
        return contains(v) ? v : low() + floorMod(v - low(), size());
    }

    private static double floorMod(double x, double y) {
        return x - y * floor(x / y);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%f;%f[", low(), high());
    }
}