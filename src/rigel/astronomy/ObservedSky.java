package rigel.astronomy;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

import rigel.astronomy.catalogs.StarCatalogue;
import rigel.astronomy.models.*;
import rigel.astronomy.objects.*;
import rigel.coordinates.*;

public final class ObservedSky {
    private enum Kind {SUN, PLANET, MOON, STAR}

    private final Sun sun;
    private final Moon moon;
    private final List<Planet> planets;
    private final StarCatalogue starCatalogue;

    private final Map<Kind, double[]> positions;

    public ObservedSky(ZonedDateTime when, GeographicCoordinates where, StereographicProjection projection, StarCatalogue starCatalogue) {
        var daysSinceEpoch = Epoch.J2010.daysUntil(when);
        var eclToEqu = new EclipticToEquatorialConversion(when);

        var sun = SunModel.SUN.at(daysSinceEpoch, eclToEqu);
        var moon = MoonModel.MOON.at(daysSinceEpoch, eclToEqu);
        var planets = new ArrayList<Planet>();
        for (PlanetModel planetModel : PlanetModel.ALL) {
            if (planetModel != PlanetModel.EARTH)
                planets.add(planetModel.at(daysSinceEpoch, eclToEqu));
        }

        var combinedProjection = new EquatorialToHorizontalConversion(when, where).andThen(projection);
        var positions = new EnumMap<>(Map.of(
                Kind.SUN, computePositions(combinedProjection, List.of(sun)),
                Kind.MOON, computePositions(combinedProjection, List.of(moon)),
                Kind.PLANET, computePositions(combinedProjection, planets),
                Kind.STAR, computePositions(combinedProjection, starCatalogue.stars())));

        this.sun = sun;
        this.moon = moon;
        this.planets = unmodifiableList(planets);
        this.starCatalogue = starCatalogue;
        this.positions = unmodifiableMap(positions);
    }

    private static double[] computePositions(Function<EquatorialCoordinates, CartesianCoordinates> projection,
                                             List<? extends CelestialObject> objects) {
        var positions = new double[2 * objects.size()];
        var i = 0;
        for (var object : objects) {
            var position = projection.apply(object.equatorialPos());
            positions[i++] = position.x();
            positions[i++] = position.y();
        }
        return positions;
    }

    public Sun sun() {
        return sun;
    }

    public Moon moon() {
        return moon;
    }

    public List<Planet> planets() {
        return planets;
    }

    public List<Star> stars() {
        return starCatalogue.stars();
    }

    public Set<Asterism> asterisms() {
        return starCatalogue.asterisms();
    }

    public List<Integer> asterismIndices(Asterism asterism) {
        return starCatalogue.asterismIndices(asterism);
    }

    public CartesianCoordinates sunPosition() {
        var pos = positions.get(Kind.SUN);
        return CartesianCoordinates.of(pos[0], pos[1]);
    }

    public CartesianCoordinates moonPosition() {
        var pos = positions.get(Kind.MOON);
        return CartesianCoordinates.of(pos[0], pos[1]);
    }

    public double[] planetPositions() {
        var pos = positions.get(Kind.PLANET);
        return Arrays.copyOf(pos, pos.length);
    }

    public double[] starPositions() {
        var pos = positions.get(Kind.STAR);
        return Arrays.copyOf(pos, pos.length);
    }

    public Optional<CelestialObject> objectClosestTo(double pX, double pY, double maxDistance) {
        var closestDistance = maxDistance;
        var closestKind = (Kind) null;
        var closestIndex = -1;

        for (var entry : positions.entrySet()) {
            var kind = entry.getKey();
            var positions = entry.getValue();
            for (int i = 0; i < positions.length; i += 2) {
                var dX = abs(pX - positions[i]);
                var dY = abs(pY - positions[i + 1]);
                if (dX < closestDistance && dY < closestDistance) {
                    var d = hypot(dX, dY);
                    if (d < closestDistance) {
                        closestKind = kind;
                        closestIndex = i >> 1;
                        closestDistance = d;
                    }
                }
            }
        }
        return closestKind != null ? Optional.of(object(closestKind, closestIndex)) : Optional.empty();
    }

    private CelestialObject object(Kind kind, int index) {
        switch (kind) {
            case SUN:
                return sun;
            case MOON:
                return moon;
            case PLANET:
                return planets().get(index);
            case STAR:
                return stars().get(index);
            default:
                throw new Error();
        }
    }
}
