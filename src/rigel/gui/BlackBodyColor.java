package rigel.gui;

import static java.lang.Math.round;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Collections.unmodifiableList;
import static rigel.Preconditions.checkInInterval;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

import rigel.math.ClosedInterval;
import rigel.math.Interval;

public final class BlackBodyColor {
    private static final Interval TEMPERATURE_INTERVAL = ClosedInterval.of(1_000, 40_000);
    private static final double TEMPERATURE_STEP = 100;

    // Columns in data file
    private static final int DEG_BEGIN = 10;
    private static final int DEG_END = 15;
    private static final int WEB_COLOR_BEGIN = 80;

    private BlackBodyColor() { }

    // Color table based on http://www.vendian.org/mncharity/dir3/blackbody/UnstableURLs/bbr_color.txt
    private static final List<Color> COLOR_TABLE = loadTable(BlackBodyColor.class.getResourceAsStream("/bbr_color.txt"));

    private static List<Color> loadTable(InputStream tableStream) {
        try (var reader = new BufferedReader(new InputStreamReader(tableStream, US_ASCII))) {
            var colors = new ArrayList<Color>();
            var line = (String) null;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && line.substring(DEG_BEGIN, DEG_END).equals("10deg"))
                    colors.add(Color.web(line.substring(WEB_COLOR_BEGIN)));
            }
            return unmodifiableList(colors);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Color colorForTemperature(double temperature) {
        checkInInterval(TEMPERATURE_INTERVAL, temperature);
        return COLOR_TABLE.get((int) round((temperature - TEMPERATURE_INTERVAL.low()) / TEMPERATURE_STEP));
    }
}
