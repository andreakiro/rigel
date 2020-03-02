package rigel.coordinates;

import java.util.Locale;

public final class CartesianCoordinates {
	
    private final double x, y;

    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object that) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(%.4f, %.4f)", x, y);
    }
}