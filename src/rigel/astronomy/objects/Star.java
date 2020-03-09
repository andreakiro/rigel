package rigel.astronomy.objects;

import static rigel.Preconditions.*;

import rigel.coordinates.EquatorialCoordinates;
import rigel.math.ClosedInterval;

public final class Star extends CelestialObject {
    private static final ClosedInterval COLOR_INDEX_INTERVAL = ClosedInterval.of(-0.5, 5.5);

    private final int hipparcosId;
    private final int colorTemperature;

    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude);

        checkArgument(hipparcosId >= 0);
        checkInInterval(COLOR_INDEX_INTERVAL, colorIndex);

        var scaledColorIndex = 0.92 * colorIndex;
        var colorTemperature = 4_600d * (1d / (scaledColorIndex + 1.7) + 1d / (scaledColorIndex + 0.62));

        this.hipparcosId = hipparcosId;
        this.colorTemperature = (int) colorTemperature;
    }

    public int hipparcosId() {
        return hipparcosId;
    }

    public int colorTemperature() {
        return colorTemperature;
    }
}
