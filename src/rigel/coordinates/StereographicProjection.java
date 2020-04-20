package rigel.coordinates;

import static java.lang.Math.*;

import java.util.function.Function;

import rigel.math.Angle;

public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {
    private final HorizontalCoordinates center;
    private final double sinCenterAlt, cosCenterAlt;

    public StereographicProjection(HorizontalCoordinates center) {
        this.center = center;
        this.sinCenterAlt = sin(center.alt());
        this.cosCenterAlt = cos(center.alt());
    }

    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        var alt = hor.alt();
        return CartesianCoordinates.of(0, cosCenterAlt / (sin(alt) + sinCenterAlt));
    }

    public double circleRadiusForParallel(HorizontalCoordinates hor) {
        var alt = hor.alt();
        return cos(alt) / (sin(alt) + sinCenterAlt);
    }

    public double applyToAngle(double rad) {
        return 2d * tan(rad / 4d);
    }

    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        var sinAlt = sin(azAlt.alt());
        var cosAlt = cos(azAlt.alt());

        var azDiff = azAlt.az() - center.az();
        var sinAzDiff = sin(azDiff);
        var cosAzDiff = cos(azDiff);

        var d = 1d / (sinAlt * sinCenterAlt + cosAlt * cosCenterAlt * cosAzDiff + 1d);
        return CartesianCoordinates.of(
                d * (cosAlt * sinAzDiff),
                d * (sinAlt * cosCenterAlt - cosAlt * sinCenterAlt * cosAzDiff));
    }

    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        if (xy.x() == 0 && xy.y() == 0)
            return center;

        var rhoSq = xy.x() * xy.x() + xy.y() * xy.y();
        var rho = sqrt(rhoSq);
        var sinC = (2d * rho) / (rhoSq + 1d);
        var cosC = (1d - rhoSq) / (rhoSq + 1d);

        var az = center.lon() + atan2(xy.x() * sinC, (rho * cosCenterAlt * cosC - xy.y() * sinCenterAlt * sinC));
        var alt = asin(cosC * sinCenterAlt + xy.y() * sinC * cosCenterAlt / rho);

        return HorizontalCoordinates.of(Angle.normalizePositive(az), alt);
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object that) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return String.format("StereographicProjection(%s)", center);
    }
}
