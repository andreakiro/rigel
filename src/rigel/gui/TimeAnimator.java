package rigel.gui;

import java.time.ZonedDateTime;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.ZonedDateTime;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class TimeAnimator extends AnimationTimer {
    private final DateTimeBean dateTimeBean;

    private final ObjectProperty<TimeAccelerator> accelerator;
    private final BooleanProperty running;

    private long realStart;
    private ZonedDateTime simulatedStart;

    public TimeAnimator(DateTimeBean dateTimeBean) {
        this.dateTimeBean = dateTimeBean;
        this.accelerator = new SimpleObjectProperty<>();
        this.running = new SimpleBooleanProperty(false);
    }

    public TimeAccelerator getAccelerator() {
        return accelerator.get();
    }

    public ObjectProperty<TimeAccelerator> acceleratorProperty() {
        return accelerator;
    }

    public void setAccelerator(TimeAccelerator accelerator) {
        this.accelerator.set(accelerator);
    }

    public boolean isRunning() {
        return running.get();
    }

    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }

    @Override
    public void start() {
        realStart = 0;
        simulatedStart = null;
        running.set(true);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        running.set(false);
    }

    public void toggle() {
        if (isRunning()) stop();
        else start();
    }

    @Override
    public void handle(long realNow) {
        if (simulatedStart == null) {
            realStart = realNow;
            simulatedStart = dateTimeBean.getZonedDateTime();
        } else {
            var elapsedRealNs = realNow - realStart;
            dateTimeBean.setZonedDateTime(accelerator.get().adjust(simulatedStart, elapsedRealNs));
        }
    }
}
