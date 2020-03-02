package rigel.astronomy;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public enum Epoch {
    // January 1 2000, 12h UTC
    J2000(ZonedDateTime.of(
            LocalDate.of(2000, Month.JANUARY, 1),
            LocalTime.NOON,
            ZoneOffset.UTC)),

    // January 0 (!) 2010, 0h UTC
    J2010(ZonedDateTime.of(
            LocalDate.of(2010, Month.JANUARY, 1).minusDays(1),
            LocalTime.MIDNIGHT,
            ZoneOffset.UTC));

    private static final double DAYS_PER_MILLIS = 1e-3 / Duration.ofDays(1).getSeconds();
    private static final double JULIAN_CENTURIES_PER_MILLIS = 1e-3 / Duration.ofDays(36525).getSeconds();

    private final ZonedDateTime dateTime;

    Epoch(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public double daysUntil(ZonedDateTime when) {
        return dateTime.until(when, MILLIS) * DAYS_PER_MILLIS;
    }

    public double julianCenturiesUntil(ZonedDateTime when) {
        return dateTime.until(when, MILLIS) * JULIAN_CENTURIES_PER_MILLIS;
    }
}
