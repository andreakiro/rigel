package rigel.astronomy.catalogs;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rigel.astronomy.objects.Star;
import rigel.coordinates.EquatorialCoordinates;

public enum HygDatabaseLoader implements StarCatalogue.Loader {
    INSTANCE;

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        try (var r = new BufferedReader(new InputStreamReader(inputStream, US_ASCII))) {
            var line = r.readLine(); // skip header line
            while ((line = r.readLine()) != null) {
                var parts = line.split(",");
                var bayer = HygFields.BAYER.extractFrom(parts, "?") + " " + HygFields.CON.extractFrom(parts);
                var name = HygFields.PROPER.extractFrom(parts, bayer);
                var hipparcosId = HygFields.HIP.extractIntFrom(parts, 0);
                var magnitude = (float) HygFields.MAG.extractDoubleFrom(parts);
                var colorIndex = (float) HygFields.CI.extractDoubleFrom(parts, 0d);
                var ra = HygFields.RARAD.extractDoubleFrom(parts);
                var dec = HygFields.DECRAD.extractDoubleFrom(parts);
                var star = new Star(hipparcosId, name, EquatorialCoordinates.of(ra, dec), magnitude, colorIndex);
                builder.addStar(star);
            }
        }
    }

    private enum HygFields {
        ID, HIP, HD, HR, GL, BF, PROPER,
        RA, DEC, DIST, PMRA, PMDEC, RV,
        MAG, ABSMAG, SPECT, CI,
        X, Y, Z, VX, VY, VZ,
        RARAD, DECRAD, PMRARAD, PMDECRAD,
        BAYER, FLAM, CON, COMP, COMP_PRIMARY,
        BASE, LUM, VAR, VAR_MIN, VAR_MAX;

        String extractFrom(String[] elements) {
            return elements[ordinal()];
        }

        String extractFrom(String[] elements, String defaultValue) {
            var s = extractFrom(elements);
            return s.trim().isEmpty() ? defaultValue : s; // s.trim().isEmpty() equiv. to s.isBlank()
        }

        int extractIntFrom(String[] elements, int defaultValue) {
            var s = extractFrom(elements);
            return s.trim().isEmpty() ? defaultValue : Integer.parseInt(s);
        }

        double extractDoubleFrom(String[] elements) {
            return Double.parseDouble(extractFrom(elements));
        }

        double extractDoubleFrom(String[] elements, double defaultValue) {
            return extractFrom(elements).trim().isEmpty() ? defaultValue : extractDoubleFrom(elements);
        }
    }
}
