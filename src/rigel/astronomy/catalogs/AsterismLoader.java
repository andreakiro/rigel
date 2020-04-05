package rigel.astronomy.catalogs;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import rigel.astronomy.objects.Asterism;
import rigel.astronomy.objects.Star;

public enum AsterismLoader implements StarCatalogue.Loader {
    INSTANCE;

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        var hipparcosToStar = new HashMap<Integer, Star>();
        for (var star : builder.stars()) {
            if (star.hipparcosId() > 0)
                hipparcosToStar.put(star.hipparcosId(), star);
        }

        try (var r = new BufferedReader(new InputStreamReader(inputStream, US_ASCII))) {
            var line = (String)null;
            while ((line = r.readLine()) != null) {
                var stars = new ArrayList<Star>();
                for (var id : line.split(","))
                    stars.add(hipparcosToStar.get(Integer.parseInt(id)));
                builder.addAsterism(new Asterism(stars));
            }
        }
    }
}
