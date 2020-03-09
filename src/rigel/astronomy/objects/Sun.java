package rigel.astronomy.objects;

import static java.util.Objects.requireNonNull;

import rigel.coordinates.EclipticCoordinates;
import rigel.coordinates.EquatorialCoordinates;

public final class Sun extends CelestialObject {
    private static final float MAGNITUDE = -26.7f;

    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;

    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super("Soleil", equatorialPos, angularSize, MAGNITUDE);
        this.eclipticPos = requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }

    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    public double meanAnomaly() {
        return meanAnomaly;
    }
}
