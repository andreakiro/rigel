package rigel.math;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static rigel.Preconditions.checkArgument;

import java.util.Locale;

public final class ClosedInterval extends Interval {
	
    public static ClosedInterval of(double low, double high) {
        checkArgument(low < high);
        return new ClosedInterval(low, high);
    }

    public static ClosedInterval symmetric(double size) {
        checkArgument(size > 0);
        var halfSize = size / 2d;
        return new ClosedInterval(-halfSize, halfSize);
    }

    private ClosedInterval(double low, double high) {
        super(low, high);
    }

    @Override
    public boolean contains(double v) {
        return low() <= v && v <= high();
    }

    public double clip(double v) {
        return max(low(), min(v, high()));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%f;%f]", low(), high());
    }
}
