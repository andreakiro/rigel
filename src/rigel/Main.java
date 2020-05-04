package rigel;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import rigel.astronomy.catalogs.AsterismLoader;
import rigel.astronomy.catalogs.HygDatabaseLoader;
import rigel.astronomy.catalogs.StarCatalogue;
import rigel.coordinates.GeographicCoordinates;
import rigel.coordinates.HorizontalCoordinates;
import rigel.gui.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static javafx.beans.binding.Bindings.*;
import static javafx.collections.FXCollections.observableList;

public final class Main extends Application {
    private static final GeographicCoordinates INITIAL_OBSERVER_LOCATION =
            GeographicCoordinates.ofDeg(6.57, 46.52); // EPFL
    private static final HorizontalCoordinates INITIAL_OBSERVATION_CENTER =
            HorizontalCoordinates.ofDeg(180.000000000001, 15);
    private static final double INITIAL_FIELD_OF_VIEW = 100;

    // Font Awesome glyphs (see https://fontawesome.com/)
    private static final String FA_PLAY = "\uf04b";
    private static final String FA_PAUSE = "\uf04c";
    private static final String FA_UNDO = "\uf0e2";

    private final StarCatalogue starCatalogue;

    public static void main(String[] args) { launch(args); }

    public Main() throws IOException {
        try (var hygStream = getClass().getResourceAsStream("/hygdata_v3.csv");
             var asterismStream = getClass().getResourceAsStream("/asterisms.txt")) {
            this.starCatalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                    .build();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        var dateTimeBean = new DateTimeBean();
        dateTimeBean.setZonedDateTime(ZonedDateTime.now());

        var observerLocationBean = new ObserverLocationBean();
        observerLocationBean.setCoordinates(INITIAL_OBSERVER_LOCATION);

        var viewingParamtersBean = new ViewingParametersBean();
        viewingParamtersBean.setCenter(INITIAL_OBSERVATION_CENTER);
        viewingParamtersBean.setFieldOfViewDeg(INITIAL_FIELD_OF_VIEW);

        var skyCanvasManager = new SkyCanvasManager(
                starCatalogue,
                dateTimeBean,
                observerLocationBean,
                viewingParamtersBean);

        var skyCanvas = skyCanvasManager.canvas();
        var skyPane = new Pane(skyCanvas);
        skyCanvas.widthProperty().bind(skyPane.widthProperty());
        skyCanvas.heightProperty().bind(skyPane.heightProperty());

        var controlPane = createControlPane(skyCanvasManager);
        var infoPane = createInfoPane(skyCanvasManager);

        var mainPane = new BorderPane(skyPane, controlPane, null, infoPane, null);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle("Rigel");
        primaryStage.show();

        skyCanvas.requestFocus();
    }

    private Pane createControlPane(SkyCanvasManager skyCanvasManager) {
        var timeAnimator = new TimeAnimator(skyCanvasManager.dateTimeBean());
        var controlPane = new HBox(
                createObserverLocationPane(skyCanvasManager.observerLocationBean()),
                new Separator(Orientation.VERTICAL),
                createDateTimePane(skyCanvasManager.dateTimeBean(), timeAnimator),
                new Separator(Orientation.VERTICAL),
                createTimeControlPane(skyCanvasManager.dateTimeBean(), timeAnimator));
        controlPane.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlPane;
    }

    private Pane createObserverLocationPane(ObserverLocationBean observerLocationBean) {
        var pane = new HBox();
        pane.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");
        pane.getChildren().addAll(
                createLonLatNodes(
                        "Longitude (°) :",
                        GeographicCoordinates::isValidLonDeg,
                        observerLocationBean.lonDegProperty()));
        pane.getChildren().addAll(
                createLonLatNodes(
                        "Latitude (°) :",
                        GeographicCoordinates::isValidLatDeg,
                        observerLocationBean.latDegProperty()));
        return pane;
    }

    private List<Node> createLonLatNodes(String labelText, Predicate<Double> predicate, DoubleProperty property) {
        var label = new Label(labelText);
        var textField = new TextField();
        textField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        var stringConverter = new NumberStringConverter("#0.00");
        var filter = (UnaryOperator<TextFormatter.Change>) change -> {
            try {
                var newText = change.getControlNewText();
                var newValue = stringConverter.fromString(newText).doubleValue();
                return predicate.test(newValue) ? change : null;
            } catch (Exception e) {
                return null;
            }
        };
        var textFormatter = new TextFormatter<>(stringConverter, 0, filter);
        textField.setTextFormatter(textFormatter);
        textFormatter.valueProperty().bindBidirectional(property);
        return List.of(label, textField);
    }

    private Pane createDateTimePane(DateTimeBean dateTimeBean, TimeAnimator timeAnimator) {
        var dateLabel = new Label("Date :");
        var datePicker = new DatePicker();
        datePicker.setStyle("-fx-pref-width: 120;");
        datePicker.valueProperty().bindBidirectional(dateTimeBean.dateProperty());

        var timeLabel = new Label("Heure :");
        var timeField = new TextField();
        timeField.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");
        var hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        var stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        var timeFormatter = new TextFormatter<>(stringConverter);
        timeField.setTextFormatter(timeFormatter);
        timeFormatter.valueProperty().bindBidirectional(dateTimeBean.timeProperty());

        var zoneIds = ZoneId.getAvailableZoneIds().stream()
                .sorted()
                .map(ZoneId::of)
                .collect(Collectors.toUnmodifiableList());
        var zoneBox = new ComboBox<>(observableList(zoneIds));
        zoneBox.setStyle("-fx-pref-width: 180;");
        zoneBox.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());

        var pane = new HBox(dateLabel, datePicker, timeLabel, timeField, zoneBox);
        pane.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        for (var child : pane.getChildren())
            child.disableProperty().bind(timeAnimator.runningProperty());

        return pane;
    }

    private Pane createTimeControlPane(DateTimeBean dateTimeBean, TimeAnimator timeAnimator) {
        try (var fontStream = getClass().getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf")) {
            var fontAwesome = Font.loadFont(fontStream, 15);

            var timeAccelerator = new ChoiceBox<NamedTimeAccelerator>();
            timeAccelerator.setItems(observableList(NamedTimeAccelerator.ALL));
            timeAccelerator.setValue(NamedTimeAccelerator.TIMES_300);
            timeAnimator.acceleratorProperty().bind(select(timeAccelerator.valueProperty(), "accelerator"));
            timeAccelerator.disableProperty().bind(timeAnimator.runningProperty());

            var resetButton = new Button(FA_UNDO);
            resetButton.setFont(fontAwesome);
            resetButton.setOnAction(o -> dateTimeBean.setZonedDateTime(ZonedDateTime.now()));
            resetButton.disableProperty().bind(timeAnimator.runningProperty());

            var playPauseButton = new Button();
            playPauseButton.setFont(fontAwesome);
            playPauseButton.textProperty().bind(
                    when(timeAnimator.runningProperty().not())
                            .then(FA_PLAY)
                            .otherwise(FA_PAUSE));
            playPauseButton.setOnAction(o -> timeAnimator.toggle());

            HBox pane = new HBox(timeAccelerator, resetButton, playPauseButton);
            pane.setStyle("-fx-spacing: inherit;");
            return pane;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Pane createInfoPane(SkyCanvasManager skyCanvasManager) {
        var fovText = new Text();
        fovText.textProperty().bind(
                format(Locale.ROOT, "Champ de vue : %.1f°",
                        skyCanvasManager.viewingParametersBean().fieldOfViewDegProperty()));

        var infoText = new Text();
        infoText.textProperty().bind(createStringBinding(() -> {
                    var obj = skyCanvasManager.objectUnderMouseProperty().getValue();
                    return obj == null ? "" : obj.info();
                },
                skyCanvasManager.objectUnderMouseProperty()));

        var horPosText = new Text();
        horPosText.textProperty().bind(
                format(Locale.ROOT, "Azimut : %3.2f°, hauteur : %2.2f°",
                        skyCanvasManager.mouseAzDegProperty(),
                        skyCanvasManager.mouseAltDegProperty()));

        var infoPane = new BorderPane(infoText, null, horPosText, null, fovText);
        infoPane.setStyle("-fx-padding: 4; -fx-background-color: white;");
        return infoPane;
    }
}
