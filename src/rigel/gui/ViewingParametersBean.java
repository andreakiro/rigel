package rigel.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import rigel.coordinates.HorizontalCoordinates;

public final class ViewingParametersBean {
    private final ObjectProperty<HorizontalCoordinates> center;
    private final DoubleProperty fieldOfViewDeg;

    public ViewingParametersBean() {
        this.center = new SimpleObjectProperty<>();
        this.fieldOfViewDeg = new SimpleDoubleProperty();
    }

    public HorizontalCoordinates getCenter() {
        return center.getValue();
    }

    public ObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }

    public void setCenter(HorizontalCoordinates newCenter) {
        center.setValue(newCenter);
    }

    public double getFieldOfViewDeg() {
        return fieldOfViewDeg.get();
    }

    public DoubleProperty fieldOfViewDegProperty() {
        return fieldOfViewDeg;
    }

    public void setFieldOfViewDeg(double newFieldOfViewDeg) {
        fieldOfViewDeg.set(newFieldOfViewDeg);
    }
}
