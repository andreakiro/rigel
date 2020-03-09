package rigel.astronomy.models;

import static rigel.astronomy.models.SunModel.MEAN_EARTH_ANGULAR_SPEED;
import static rigel.math.Angle.ofDeg;
import static rigel.math.Angle.ofArcsec;
import static java.lang.Math.*;

import java.util.List;

import rigel.astronomy.objects.Planet;
import rigel.coordinates.EclipticCoordinates;
import rigel.coordinates.EclipticToEquatorialConversion;
import rigel.math.Angle;

public enum PlanetModel implements CelestialObjectModel<Planet> {
    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627, 0.387098, 7.0051, 48.449, 6.74, -0.42),
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812, 0.723329, 3.3947, 76.769, 16.92, -4.40),
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671, 0.999985, 0, 0, 0, 0),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348, 1.523689, 1.8497, 49.632, 9.36, -1.52),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907, 5.20278, 1.3035, 100.595, 196.74, -9.40),
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853, 9.51134, 2.4873, 113.752, 165.60, -8.88),
    URANUS("Uranus", 84.039492, 271.063148, 172.884833, 0.046321, 19.21814, 0.773059, 73.926961, 65.80, -7.19),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483, 30.1985, 1.7673, 131.879, 62.20, -6.87);

    private final String name;
    private final double angularSpeed;
    private final double epochLon;
    private final double perihelionLon;
    private final double eccentricity;
    private final double semiMajorAxis;
    private final double sinOrbitalInclination;
    private final double cosOrbitalInclination;
    private final double ascNodeLon;
    private final double angularSize1AU;
    private final double magnitude1AU;

    public static final List<PlanetModel> ALL = List.of(values());

    PlanetModel(String name,
                double orbitalPeriod,
                double lonJ2010Deg,
                double perihelionLonDeg,
                double eccentricity,
                double semiMajorAxis,
                double orbitalInclinationDeg,
                double ascNodeLonDeg,
                double angularSize1AUArcsec,
                double magnitude1AU) {
        var orbitalInclination = ofDeg(orbitalInclinationDeg);

        this.name = name;
        this.angularSpeed = MEAN_EARTH_ANGULAR_SPEED / orbitalPeriod;
        this.epochLon = ofDeg(lonJ2010Deg);
        this.perihelionLon = ofDeg(perihelionLonDeg);
        this.eccentricity = eccentricity;
        this.semiMajorAxis = semiMajorAxis;
        this.sinOrbitalInclination = sin(orbitalInclination);
        this.cosOrbitalInclination = cos(orbitalInclination);
        this.ascNodeLon = ofDeg(ascNodeLonDeg);
        this.angularSize1AU = ofArcsec(angularSize1AUArcsec);
        this.magnitude1AU = magnitude1AU;
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclToEqu) {
        // 1. Ecliptic position
        var trueAnomaly = trueAnomaly(daysSinceJ2010);
        var radius = radius(trueAnomaly);
        var hLon = heliocentricLongitude(trueAnomaly);
        var sinLonDiff = sin(hLon - ascNodeLon);
        var hLat = asin(sinLonDiff * sinOrbitalInclination);

        var l1 = atan2(sinLonDiff * cosOrbitalInclination, cos(hLon - ascNodeLon)) + ascNodeLon;
        var r1 = radius * cos(hLat);

        var trueAnomalyEarth = EARTH.trueAnomaly(daysSinceJ2010);
        var radiusEarth = EARTH.radius(trueAnomalyEarth);
        var hLonEarth = EARTH.heliocentricLongitude(trueAnomalyEarth);

        var rSinDeltaL = radiusEarth * sin(l1 - hLonEarth);
        var lon = semiMajorAxis < 1d
                ? PI + hLonEarth + atan2(r1 * sin(hLonEarth - l1), radiusEarth - r1 * cos(hLonEarth - l1))
                : l1 + atan2(rSinDeltaL, r1 - radiusEarth * cos(l1 - hLonEarth));
        // Don't use atan2 for latitudes, which should be between ±90°
        var lat = atan((r1 * tan(hLat) * sin(lon - l1)) / rSinDeltaL);
        var eclPos = EclipticCoordinates.of(Angle.normalizePositive(lon), lat);

        // 2. Angular size (and distance from Earth)
        var distFromEarth = sqrt(radius * radius + radiusEarth * radiusEarth - 2d * radius * radiusEarth * cos(hLon - hLonEarth) * cos(hLat));
        var angularSize = angularSize1AU / distFromEarth;

        // 3. Magnitude (and phase)
        var phase = (1d + cos(lon - hLon)) / 2d;
        var magnitude = magnitude1AU + 5d * log10(radius * distFromEarth / sqrt(phase));

        return new Planet(name, eclToEqu.apply(eclPos), (float) angularSize, (float) magnitude);
    }

    // The methods below could also be package-private to make them accessible to tests.
    private double trueAnomaly(double daysSinceJ2010) {
        var meanAnomaly = angularSpeed * daysSinceJ2010 + epochLon - perihelionLon;
        return meanAnomaly + 2d * eccentricity * sin(meanAnomaly);
    }

    private double radius(double trueAnomaly) {
        return semiMajorAxis * (1d - eccentricity * eccentricity) / (1d + eccentricity * cos(trueAnomaly));
    }

    private double heliocentricLongitude(double trueAnomaly) {
        return trueAnomaly + perihelionLon;
    }
}
