package rigel.coordinates;

import static java.lang.Math.*;
import static rigel.astronomy.Epoch.J2000;

import java.time.ZonedDateTime;
import java.util.function.Function;

import rigel.math.Angle;
import rigel.math.Polynomial;

public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {
	
    private static final Polynomial OBLIQUITY_POLYNOMIAL = Polynomial.of(
            + Angle.ofArcsec(0.00181),
            - Angle.ofArcsec(0.0006),
            - Angle.ofArcsec(46.815),
            + Angle.ofDMS(23, 26, 21.45));

    private final double cosObl, sinObl;

    public EclipticToEquatorialConversion(ZonedDateTime when) {
        var obliquityOfEcliptic = obliquityOfEcliptic(when);
        this.cosObl = cos(obliquityOfEcliptic);
        this.sinObl = sin(obliquityOfEcliptic);
    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        // PACS4, §27
        var sinLon = sin(ecl.lon());
        var ra = atan2(sinLon * cosObl - tan(ecl.lat()) * sinObl, cos(ecl.lon()));
        var dec = asin(sin(ecl.lat()) * cosObl + cos(ecl.lat()) * sinObl * sinLon);
        return EquatorialCoordinates.of(Angle.normalizePositive(ra), dec);
    }

    static double obliquityOfEcliptic(ZonedDateTime when) {
        // PACS4, §27
        return OBLIQUITY_POLYNOMIAL.at(J2000.julianCenturiesUntil(when));
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object that) {
        throw new UnsupportedOperationException();
    }
}
