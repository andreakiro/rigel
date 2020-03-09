package rigel.astronomy.models;

import rigel.coordinates.EclipticToEquatorialConversion;

public interface CelestialObjectModel<O> {
    O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);
}
