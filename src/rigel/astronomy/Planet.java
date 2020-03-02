package rigel.astronomy;

import rigel.coordinates.EquatorialCoordinates;

public final class Planet extends CelestialObject {
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        super(name, equatorialPos, angularSize, magnitude);
    }
}
