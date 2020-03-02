package rigel.coordinates;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.time.ZonedDateTime;
import java.util.function.Function;

import rigel.astronomy.SiderealTime;
import rigel.math.Angle;

public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {
	
    private final double localSiderealTime;
    private final double sinLat;
    private final double cosLat;

    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        this.localSiderealTime = SiderealTime.local(when, where);
        this.sinLat = sin(where.lat());
        this.cosLat = cos(where.lat());
    }

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates eq) {
        var hourAngle = localSiderealTime - eq.ra();
        var sinDec = sin(eq.dec());
        var cosDec = cos(eq.dec());
        var sinAlt = sinDec * sinLat + cosDec * cosLat * cos(hourAngle);
        var az = Angle.normalizePositive(atan2(-cosDec * cosLat * sin(hourAngle), sinDec - sinLat * sinAlt));
        return HorizontalCoordinates.of(az, asin(sinAlt));
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
