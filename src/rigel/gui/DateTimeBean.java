package rigel.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class DateTimeBean {
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
    private final ObjectProperty<ZoneId> zone = new SimpleObjectProperty<>();

    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    public void setZonedDateTime(ZonedDateTime newZonedDateTime) {
        setDate(newZonedDateTime.toLocalDate());
        setTime(newZonedDateTime.toLocalTime());
        setZone(newZonedDateTime.getZone());
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public LocalTime getTime() {
        return time.get();
    }

    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time.set(time);
    }

    public ZoneId getZone() {
        return zone.get();
    }

    public ObjectProperty<ZoneId> zoneProperty() {
        return zone;
    }

    public void setZone(ZoneId zone) {
        this.zone.set(zone);
    }
}
