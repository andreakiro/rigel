package rigel;

public class Preconditions {

	private Preconditions() {}

    public static void checkArgument(boolean isTrue) {
        if (! isTrue)
            throw new IllegalArgumentException();
    }

    public static double checkInInterval(Interval interval, double value) {
        if (! interval.contains(value))
            throw new IllegalArgumentException(String.format("%f not in interval %s", value, interval));
        return value;
    }
}