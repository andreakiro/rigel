package rigel.math;

import static rigel.Preconditions.checkArgument;;

public final class Polynomial {
    private final double[] coefficients;

    public static Polynomial of(double coefficientN, double... coefficients) {
        checkArgument(coefficientN != 0);
        var allCoefficients = new double[1 + coefficients.length];
        allCoefficients[0] = coefficientN;
        System.arraycopy(coefficients, 0, allCoefficients, 1, coefficients.length);
        return new Polynomial(allCoefficients);
    }

    private Polynomial(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public double at(double x) {
        var result = coefficients[0];
        for (var i = 1; i < coefficients.length; i += 1)
            result = result * x + coefficients[i];
        return result;
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
        var b = new StringBuilder();
        for (var i = 0; i < coefficients.length; i += 1) {
            var coefficient = coefficients[i];
            if (coefficient == 0)
                continue;
            if (coefficient >= 0 && b.length() > 0)
                b.append("+");
            else if (coefficient == -1)
                b.append("-");
            if (coefficient != 1d && coefficient != -1d)
                b.append(coefficient);
            var degree = coefficients.length - 1 - i;
            if (degree > 0)
                b.append("x");
            if (degree > 1)
                b.append("^").append(degree);
        }
        return b.toString();
    }
}
