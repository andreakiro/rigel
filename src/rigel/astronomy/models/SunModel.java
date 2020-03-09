package rigel.astronomy.models;

import static rigel.math.Angle.ofDeg;
import static rigel.math.Angle.TAU;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import rigel.astronomy.models.CelestialObjectModel;
import rigel.astronomy.objects.Sun;
import rigel.coordinates.EclipticCoordinates;
import rigel.coordinates.EclipticToEquatorialConversion;
import rigel.math.Angle;

public enum SunModel implements CelestialObjectModel<Sun> {
    SUN;

    public static final double MEAN_EARTH_ANGULAR_SPEED = TAU / 365.242_191;

    // All values below are for the J2010 epoch.
    private static final double MEAN_LON = ofDeg(279.557_208);
    private static final double LON_PERIGEE = ofDeg(283.112_438);
    private static final double ECCENTRICITY = 0.016_705;
    private static final double ONE_MINUS_ECCENTRICITY_SQUARED = 1d - ECCENTRICITY * ECCENTRICITY;

    private static final double ANGULAR_SIZE_0 = ofDeg(0.533_128);

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclToHor) {
        var meanAnomaly = meanAnomaly(daysSinceJ2010);
        var trueAnomaly = trueAnomaly(meanAnomaly);
        var eclipticPos = eclipticPosition(trueAnomaly);
        var equatorialPos = eclToHor.apply(eclipticPos);
        var angularSize = ANGULAR_SIZE_0 * (1d + ECCENTRICITY * cos(trueAnomaly)) / ONE_MINUS_ECCENTRICITY_SQUARED;
        return new Sun(eclipticPos, equatorialPos, (float) angularSize, (float) meanAnomaly);
    }

    // The methods below could also be package-private to make them accessible to tests
    // (and to MoonModel).
    private static EclipticCoordinates eclipticPosition(double trueAnomaly) {
        return EclipticCoordinates.of(Angle.normalizePositive(LON_PERIGEE + trueAnomaly), 0);
    }

    private static double trueAnomaly(double meanAnomaly) {
        return meanAnomaly + 2d * ECCENTRICITY * sin(meanAnomaly);
    }

    private static double meanAnomaly(double daysSinceJ2010) {
        return Angle.normalizePositive(MEAN_EARTH_ANGULAR_SPEED * daysSinceJ2010 + MEAN_LON - LON_PERIGEE);
    }
}
