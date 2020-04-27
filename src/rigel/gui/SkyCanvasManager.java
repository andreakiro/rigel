package rigel.gui;

public final class SkyCanvasManager {
    private static final ClosedInterval FIELD_OF_VIEW_INTERVAL = ClosedInterval.of(30, 150);
    private static final RightOpenInterval VIEWING_AZ_INTERVAL = RightOpenInterval.of(0, 360);
    private static final ClosedInterval VIEWING_ALT_INTERVAL = ClosedInterval.of(5, 90);

    private static final int MAX_OBJECT_DISTANCE = 10;

    private static final Map<KeyCode, int[]> VIEW_DIRECTION_CHANGES = Map.of(
            KeyCode.LEFT, new int[]{-10, 0},
            KeyCode.RIGHT, new int[]{10, 0},
            KeyCode.UP, new int[]{0, 5},
            KeyCode.DOWN, new int[]{0, -5}
    );

    private final Canvas canvas;
    private final SkyCanvasPainter painter;

    private final DateTimeBean dateTimeBean;
    private final ObserverLocationBean observerLocationBean;
    private final ViewingParametersBean viewingParametersBean;

    private final ObservableValue<StereographicProjection> projection;
    private final ObservableValue<Transform> planeToCanvas;

    private final ObservableValue<ObservedSky> observedSky;

    private final ObjectProperty<Point2D> mousePosition;
    private final ObservableValue<HorizontalCoordinates> mouseHorizontalPosition;
    private final ObservableDoubleValue mouseAzDeg;
    private final ObservableDoubleValue mouseAltDeg;
    private final ObservableValue<CelestialObject> objectUnderMouse;

    public SkyCanvasManager(StarCatalogue starCatalogue,
                            DateTimeBean dateTimeBean,
                            ObserverLocationBean observerLocationBean,
                            ViewingParametersBean viewingParametersBean) {
        this.canvas = new Canvas();
        this.painter = new SkyCanvasPainter(canvas);
        this.dateTimeBean = dateTimeBean;
        this.observerLocationBean = observerLocationBean;
        this.viewingParametersBean = viewingParametersBean;

        this.mousePosition = new SimpleObjectProperty<>(Point2D.ZERO);

        this.projection = createObjectBinding(() ->
                        new StereographicProjection(viewingParametersBean.getCenter()),
                viewingParametersBean.centerProperty());

        this.planeToCanvas = createObjectBinding(() -> {
                    var fov = Angle.ofDeg(viewingParametersBean.getFieldOfViewDeg());
                    var projection = this.projection.getValue();
                    var width = canvas.widthProperty().get();
                    var height = canvas.heightProperty().get();
                    var scale = width / projection.applyToAngle(fov);
                    return Transform.translate(width / 2, height / 2)
                            .createConcatenation(Transform.scale(scale, -scale));
                },
                viewingParametersBean.fieldOfViewDegProperty(),
                projection,
                canvas.widthProperty(),
                canvas.heightProperty());

        this.observedSky = createObjectBinding(() ->
                        new ObservedSky(
                                dateTimeBean.getZonedDateTime(),
                                observerLocationBean.getCoordinates(),
                                projection.getValue(),
                                starCatalogue),
                dateTimeBean.dateProperty(),
                dateTimeBean.timeProperty(),
                dateTimeBean.zoneProperty(),
                observerLocationBean.coordinatesProperty(),
                projection);

        this.mouseHorizontalPosition = createObjectBinding(() -> {
                    try {
                        var planeToCanvas = this.planeToCanvas.getValue();
                        var planePt = planeToCanvas.inverseTransform(mousePosition.get());
                        var projection = this.projection.getValue();
                        return projection.inverseApply(CartesianCoordinates.of(planePt.getX(), planePt.getY()));
                    } catch (NonInvertibleTransformException e) {
                        return null;
                    }
                },
                planeToCanvas,
                mousePosition,
                projection);

        this.mouseAzDeg = createDoubleBinding(() -> {
                    var mouseHorPos = mouseHorizontalPosition.getValue();
                    return mouseHorPos != null ? mouseHorPos.azDeg() : Double.NaN;
                },
                mouseHorizontalPosition);

        this.mouseAltDeg = createDoubleBinding(() -> {
                    var mouseHorPos = mouseHorizontalPosition.getValue();
                    return mouseHorPos != null ? mouseHorPos.altDeg() : Double.NaN;
                },
                mouseHorizontalPosition);

        this.objectUnderMouse = createObjectBinding(() -> {
                    try {
                        var planeToCanvas = this.planeToCanvas.getValue();
                        var planePt = planeToCanvas.inverseTransform(mousePosition.get());
                        var planeMaxDist = planeToCanvas.inverseDeltaTransform(MAX_OBJECT_DISTANCE, 0).getX();
                        var maybeObj = observedSky.getValue().objectClosestTo(planePt.getX(), planePt.getY(), planeMaxDist);
                        return maybeObj.orElse(null);
                    } catch (NonInvertibleTransformException e) {
                        return null;
                    }
                },
                planeToCanvas,
                mousePosition,
                observedSky);

        setupListeners();
    }

    private void setupListeners() {
        canvas.setOnMousePressed(e -> {
            if (e.isPrimaryButtonDown())
                canvas.requestFocus();
        });

        // Key listeners (to move view direction)
        canvas.setOnKeyPressed(e -> {
            if (! VIEW_DIRECTION_CHANGES.containsKey(e.getCode()))
                return;
            var dirChange = VIEW_DIRECTION_CHANGES.get(e.getCode());
            var center = viewingParametersBean.getCenter();
            var newCenterAzDeg = VIEWING_AZ_INTERVAL.reduce(center.azDeg() + dirChange[0]);
            var newCenterAltDeg = VIEWING_ALT_INTERVAL.clip(center.altDeg() + dirChange[1]);
            viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(newCenterAzDeg, newCenterAltDeg));
            e.consume();
        });

        // Mouse wheel listeners (to zoom)
        canvas.setOnScroll(e -> {
            var dx = e.getDeltaX();
            var dy = e.getDeltaY();
            var d = abs(dx) >= abs(dy) ? dx : dy;
            var newFieldOfView = FIELD_OF_VIEW_INTERVAL.clip(viewingParametersBean.getFieldOfViewDeg() + d);
            viewingParametersBean.setFieldOfViewDeg(newFieldOfView);
        });

        // Mouse movement listeners (to display information)
        canvas.setOnMouseMoved(e -> mousePosition.set(new Point2D(e.getX(), e.getY())));

        // "Data" listeners (to redraw the sky)
        for (var p: List.of(observedSky, planeToCanvas)) {
            p.addListener(o -> {
                var observedSky = this.observedSky.getValue();
                var projection = this.projection.getValue();
                var planeToCanvas = this.planeToCanvas.getValue();

                painter.clear();
                painter.drawStars(observedSky, projection, planeToCanvas);
                painter.drawPlanets(observedSky, projection, planeToCanvas);
                painter.drawSun(observedSky, projection, planeToCanvas);
                painter.drawMoon(observedSky, projection, planeToCanvas);
                painter.drawHorizon(projection, planeToCanvas);
            });
        }
    }

    public Canvas canvas() {
        return canvas;
    }

    public DateTimeBean dateTimeBean() {
        return dateTimeBean;
    }

    public ObserverLocationBean observerLocationBean() {
        return observerLocationBean;
    }

    public ViewingParametersBean viewingParametersBean() {
        return viewingParametersBean;
    }

    public ObservableDoubleValue mouseAzDegProperty() {
        return mouseAzDeg;
    }

    public ObservableDoubleValue mouseAltDegProperty() {
        return mouseAltDeg;
    }

    public ObservableValue<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }
}
