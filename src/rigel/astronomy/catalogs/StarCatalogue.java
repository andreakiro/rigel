package rigel.astronomy.catalogs;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import rigel.astronomy.objects.Asterism;
import rigel.astronomy.objects.Star;

public final class StarCatalogue {
    private final List<Star> stars;
    private final Map<Asterism, List<Integer>> asterismIndices;

    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        var starMap = new HashMap<Star, Integer>();
        for (var star : stars)
            starMap.put(star, starMap.size());

        var asterismIndices = new HashMap<Asterism, List<Integer>>();
        for (var asterism : asterisms) {
            var asterismStars = asterism.stars();
            var indices = new ArrayList<Integer>(asterismStars.size());
            for (Star asterismStar : asterismStars) {
                var starIndex = starMap.get(asterismStar);
                if (starIndex == null)
                    throw new IllegalArgumentException();
                indices.add(starIndex);
            }
            asterismIndices.put(asterism, Collections.unmodifiableList(indices));
        }

        this.stars = List.copyOf(stars);
        this.asterismIndices = Collections.unmodifiableMap(asterismIndices);
    }

    public List<Star> stars() {
        return stars;
    }

    public Set<Asterism> asterisms() {
        return asterismIndices.keySet();
    }

    public List<Integer> asterismIndices(Asterism asterism) {
        var indices = asterismIndices.get(asterism);
        if (indices == null)
            throw new IllegalArgumentException();
        return indices;
    }

    public static final class Builder {
        private final List<Star> stars = new ArrayList<>();
        private final List<Asterism> asterisms = new ArrayList<>();

        public Builder addStar(Star star) {
            stars.add(star);
            return this;
        }

        public List<Star> stars() {
            return Collections.unmodifiableList(stars);
        }

        public Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }

        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterisms);
        }

        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }
    }

    public interface Loader {
        void load(InputStream inputStream, Builder builder) throws IOException;
    }
}
