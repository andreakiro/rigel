package rigel.astronomy.objects;

import java.util.Locale;

import rigel.Preconditions;
import rigel.coordinates.EquatorialCoordinates;
import rigel.math.ClosedInterval;
import rigel.math.Interval;

public final class Moon extends CelestialObject {
    private static final Interval PHASE_INTERVAL = ClosedInterval.of(0, 1);

    private final double phase;

    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude);
        this.phase = Preconditions.checkInInterval(PHASE_INTERVAL, phase);
    }

    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f%%)", super.info(), 100f * phase);
    }
}
