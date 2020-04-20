package rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@FunctionalInterface
public interface TimeAccelerator {
    ZonedDateTime adjust(ZonedDateTime startingSimulatedTime, long elapsedRealNs);

    static TimeAccelerator continuous(int factor) {
        return (start, elapsedNs) ->
                start.plus(factor * elapsedNs, ChronoUnit.NANOS);
    }

    static TimeAccelerator discrete(Duration step, long stepsPerSecond) {
        return (start, elapsedNs) ->
                start.plus(step.multipliedBy(elapsedNs * stepsPerSecond / 1_000_000_000L));
    }
}
