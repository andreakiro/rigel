package rigel.gui;

import java.time.Duration;
import java.util.List;

public enum NamedTimeAccelerator {
    TIMES_1("1×", TimeAccelerator.continuous(1)),
    TIMES_30("30×", TimeAccelerator.continuous(30)),
    TIMES_300("300×", TimeAccelerator.continuous(300)),
    TIMES_3000("3000×", TimeAccelerator.continuous(3000)),
    DAY("jour", TimeAccelerator.discrete(Duration.ofDays(1), 60)),
    SIDEREAL_DAY("jour sidéral", TimeAccelerator.discrete(Duration.ofHours(23).plusMinutes(56).plusSeconds(4), 60));

    public static final List<NamedTimeAccelerator> ALL = List.of(values());

    private final String name;
    private final TimeAccelerator accelerator;

    NamedTimeAccelerator(String name, TimeAccelerator accelerator) {
        this.name = name;
        this.accelerator = accelerator;
    }

    public String getName() {
        return name;
    }

    public TimeAccelerator getAccelerator() {
        return accelerator;
    }

    @Override
    public String toString() {
        return name;
    }
}
