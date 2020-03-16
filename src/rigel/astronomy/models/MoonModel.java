package rigel.astronomy.models;

import static rigel.astronomy.models.SunModel.SUN;
import static rigel.math.Angle.ofDeg;
import static java.lang.Math.*;

import rigel.astronomy.objects.Moon;
import rigel.coordinates.EclipticCoordinates;
import rigel.coordinates.EclipticToEquatorialConversion;
import rigel.math.Angle;

public enum MoonModel implements CelestialObjectModel<Moon> {
    MOON;

    // PACS4, §65
    private static final double ORBIT_INCLINATION = ofDeg(5.145396);
    private static final double SIN_ORBIT_INCLINATION = sin(ORBIT_INCLINATION);
    private static final double COS_ORBIT_INCLINATION = cos(ORBIT_INCLINATION);

    private static final double MEAN_LON_J2010 = ofDeg(91.929336);
    private static final double MEAN_LON_PERIGEE_J2010 = ofDeg(130.143076);
    private static final double MEAN_NODE_LON_J2010 = ofDeg(291.682547);

    private static final double ECCENTRICITY = 0.0549;
    private static final double ONE_MINUS_ECCENTRICITY_SQ = 1d - ECCENTRICITY * ECCENTRICITY;
    private static final double ANGULAR_SIZE_0 = ofDeg(0.5181);

    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclToEqu) {
        // 1. Sun (we could also obtain the values below by calling the static methods of SunModel).
        var sun = SUN.at(daysSinceJ2010, eclToEqu);
        var sunMeanAnomaly = sun.meanAnomaly();
        var sunEclipticLon = sun.eclipticPos().lon();

        // 2. Moon's orbital longitude
        var meanLon = ofDeg(13.1763966) * daysSinceJ2010 + MEAN_LON_J2010;
        var meanAnomaly = meanLon - ofDeg(0.1114041) * daysSinceJ2010 - MEAN_LON_PERIGEE_J2010;

        var sinSunMeanAnomaly = sin(sunMeanAnomaly);
        var evection = ofDeg(1.2739) * sin(2d * (meanLon - sunEclipticLon) - meanAnomaly);
        var ae = ofDeg(0.1858) * sinSunMeanAnomaly;
        var a3 = ofDeg(0.37) * sinSunMeanAnomaly;
        var correctedAnomaly = meanAnomaly + evection - ae - a3;

        var ec = ofDeg(6.2886) * sin(correctedAnomaly);
        var a4 = ofDeg(0.214) * sin(2d * correctedAnomaly);
        var correctedLon = meanLon + evection + ec - ae + a4;

        var variation = ofDeg(0.6583) * sin(2d * (correctedLon - sunEclipticLon));
        var orbitalLon = correctedLon + variation;

        // 3. Moon's ecliptic position
        var meanNodeLon = MEAN_NODE_LON_J2010 - ofDeg(0.0529539) * daysSinceJ2010;
        var correctedNodeLon = meanNodeLon - ofDeg(0.16) * sinSunMeanAnomaly;

        var sinLonDiff = sin(orbitalLon - correctedNodeLon);
        var cosLonDiff = cos(orbitalLon - correctedNodeLon);
        var eclipticLon = atan2(sinLonDiff * COS_ORBIT_INCLINATION, cosLonDiff) + correctedNodeLon;
        var eclipticLat = asin(sinLonDiff * SIN_ORBIT_INCLINATION);
        var eclipticPos = EclipticCoordinates.of(Angle.normalizePositive(eclipticLon), eclipticLat);

        // 4. Moon's phase
        var phase = 0.5 * (1d - cos(orbitalLon - sunEclipticLon));

        // 5. Moon's angular size
        var distanceFromEarth = ONE_MINUS_ECCENTRICITY_SQ / (1d + ECCENTRICITY * cos(correctedAnomaly + ec));
        var angularSize = ANGULAR_SIZE_0 / distanceFromEarth;

        return new Moon(eclToEqu.apply(eclipticPos), (float) angularSize, 0, (float) phase);
    }
}
