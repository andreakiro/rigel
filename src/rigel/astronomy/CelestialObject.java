package rigel.astronomy;

import static rigel.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import rigel.coordinates.EquatorialCoordinates;

public abstract class CelestialObject {
    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;

    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        checkArgument(angularSize >= 0);

        this.name = requireNonNull(name);
        this.equatorialPos = requireNonNull(equatorialPos);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    }

    public String name() {
        return name;
    }

    public double angularSize() {
        return angularSize;
    }

    public double magnitude() {
        return magnitude;
    }

    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    public String info() {
        return name();
    }

    @Override
    public String toString() {
        return info();
    }
}