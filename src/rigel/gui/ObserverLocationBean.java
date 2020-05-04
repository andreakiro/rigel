package rigel.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import rigel.coordinates.GeographicCoordinates;

import static javafx.beans.binding.Bindings.createObjectBinding;

public final class ObserverLocationBean {
    private final DoubleProperty lonDeg;
    private final DoubleProperty latDeg;
    private final ObservableValue<GeographicCoordinates> coordinates;

    public ObserverLocationBean() {
        this.lonDeg = new SimpleDoubleProperty();
        this.latDeg = new SimpleDoubleProperty();
        this.coordinates = createObjectBinding(() ->
                        GeographicCoordinates.ofDeg(lonDeg.get(), latDeg.get()),
                lonDeg,
                latDeg);
    }

    public DoubleProperty lonDegProperty() {
        return lonDeg;
    }

    public DoubleProperty latDegProperty() {
        return latDeg;
    }

    public ObservableValue<GeographicCoordinates> coordinatesProperty() {
        return coordinates;
    }

    public GeographicCoordinates getCoordinates() {
        return coordinates.getValue();
    }

    public void setCoordinates(GeographicCoordinates newCoordinates) {
        lonDeg.set(newCoordinates.lonDeg());
        latDeg.set(newCoordinates.latDeg());
    }
}
