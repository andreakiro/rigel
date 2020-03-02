package rigel.astronomy;

import static rigel.astronomy.Epoch.J2000;
import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import rigel.coordinates.GeographicCoordinates;
import rigel.math.Angle;
import rigel.math.Polynomial;

public final class SiderealTime {
    // PACS4, §12
    private static final Polynomial GST_POLYNOMIAL = Polynomial.of(
            Angle.ofHr(0.000_025_862),
            Angle.ofHr(2_400.051_336),
            Angle.ofHr(6.697_374_558));
    private static final double HOURS_PER_MILLIS = 1e-3 / (60d * 60d);
    private static final double U1 = Angle.ofHr(1.002_737_909);

    private SiderealTime() {}

    public static double greenwich(ZonedDateTime when) {
        var whenUtc = when.withZoneSameInstant(ZoneOffset.UTC);

        var whenUtc0 = whenUtc.truncatedTo(ChronoUnit.DAYS);
        var s0 = GST_POLYNOMIAL.at(J2000.julianCenturiesUntil(whenUtc0));
        var s1 = U1 * (whenUtc0.until(whenUtc, MILLIS) * HOURS_PER_MILLIS);

        return Angle.normalizePositive(s0 + s1);
    }

    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return Angle.normalizePositive(greenwich(when) + where.lon());
    }
}
