package rigel.math;

public abstract class Interval {
    private final double low, high;

    protected Interval(double low, double high) {
        this.low = low;
        this.high = high;
    }

    public double low() {
        return low;
    }

    public double high() {
        return high;
    }

    public double size() {
        return high - low;
    }

    public abstract boolean contains(double v);

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object that) {
        throw new UnsupportedOperationException();
    }
}
