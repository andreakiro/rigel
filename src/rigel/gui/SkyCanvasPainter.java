package rigel.gui;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;
import rigel.astronomy.ObservedSky;
import rigel.coordinates.HorizontalCoordinates;
import rigel.coordinates.StereographicProjection;
import rigel.math.Angle;
import rigel.math.ClosedInterval;

public final class SkyCanvasPainter {
    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);
    private static final double MAX_ANGLE_FOR_MAGNITUDE = Angle.ofDeg(0.5d);
    private static final Color SUN_HALO_COLOR = Color.YELLOW.deriveColor(0, 1, 1, 0.25);

    private final Canvas canvas;
    private final GraphicsContext ctx;

    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.ctx = canvas.getGraphicsContext2D();
    }

    public void clear() {
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawStars(ObservedSky observedSky, StereographicProjection projection, Transform planeToCanvas) {
        var stars = observedSky.stars();
        var starPos = observedSky.starPositions();
        planeToCanvas.transform2DPoints(starPos, 0, starPos, 0, stars.size());

        // Asterisms
        ctx.setStroke(Color.BLUE);
        ctx.setLineWidth(1);
        var visibleBounds = canvas.getBoundsInLocal();
        for (var asterism : observedSky.asterisms()) {
            var starIndices = observedSky.asterismIndices(asterism);

            var starIndex0 = starIndices.get(0);
            var x0 = starPos[2 * starIndex0];
            var y0 = starPos[2 * starIndex0 + 1];
            var previousVisible = visibleBounds.contains(x0, y0);

            ctx.beginPath();
            ctx.moveTo(x0, y0);
            for (var starIndex : starIndices.subList(1, starIndices.size())) {
                var x = starPos[2 * starIndex];
                var y = starPos[2 * starIndex + 1];
                var currentVisible = visibleBounds.contains(x, y);

                if (previousVisible || currentVisible)
                    ctx.lineTo(x, y);
                else
                    ctx.moveTo(x, y);
                previousVisible = currentVisible;
            }
            ctx.stroke();
        }

        // Stars
        var maxDiameter = apparentDiameter(MAX_ANGLE_FOR_MAGNITUDE, projection, planeToCanvas);
        var i = 0;
        for (var star : stars) {
            var x = starPos[i++];
            var y = starPos[i++];
            var d = diameterScaleFactor(star.magnitude()) * maxDiameter;
            var c = BlackBodyColor.colorForTemperature(star.colorTemperature());
            drawDisk(x, y, d, c);
        }
    }

    public void drawPlanets(ObservedSky observedSky, StereographicProjection projection, Transform planeToCanvas) {
        var planets = observedSky.planets();
        var planetsPos = observedSky.planetPositions();
        planeToCanvas.transform2DPoints(planetsPos, 0, planetsPos, 0, planets.size());

        var maxDiameter = apparentDiameter(MAX_ANGLE_FOR_MAGNITUDE, projection, planeToCanvas);
        var i = 0;
        for (var planet : planets) {
            var x = planetsPos[i++];
            var y = planetsPos[i++];
            var d = diameterScaleFactor(planet.magnitude()) * maxDiameter;
            drawDisk(x, y, d, Color.LIGHTGRAY);
        }
    }

    public void drawSun(ObservedSky observedSky, StereographicProjection projection, Transform planeToCanvas) {
        var sunDiameter = apparentDiameter(observedSky.sun().angularSize(), projection, planeToCanvas);
        var sunPlanePos = observedSky.sunPosition();
        var sunCenter = planeToCanvas.transform(sunPlanePos.x(), sunPlanePos.y());
        drawDisk(sunCenter, sunDiameter * 2.2, SUN_HALO_COLOR);
        drawDisk(sunCenter, sunDiameter + 2, Color.YELLOW);
        drawDisk(sunCenter, sunDiameter, Color.WHITE);
    }

    public void drawMoon(ObservedSky observedSky, StereographicProjection projection, Transform planeToCanvas) {
        var moonDiameter = apparentDiameter(observedSky.moon().angularSize(), projection, planeToCanvas);
        var moonPlanePos = observedSky.moonPosition();
        var moonCenter = planeToCanvas.transform(moonPlanePos.x(), moonPlanePos.y());
        drawDisk(moonCenter, moonDiameter, Color.WHITE);
    }

    public void drawHorizon(StereographicProjection projection, Transform planeToCanvas) {
        var horizonCoordinates = HorizontalCoordinates.of(0, 0);
        var planeCenter = projection.circleCenterForParallel(horizonCoordinates);
        var planeRadius = projection.circleRadiusForParallel(horizonCoordinates);

        var center = planeToCanvas.transform(planeCenter.x(), planeCenter.y());
        var radius = planeToCanvas.deltaTransform(planeRadius, 0).getX();
        var diameter = 2d * radius;

        ctx.setStroke(Color.RED);
        ctx.setLineWidth(2);
        ctx.strokeOval(center.getX() - radius, center.getY() - radius, diameter, diameter);

        ctx.setFill(Color.RED);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.TOP);
        for (var azDeg = 0; azDeg < 360; azDeg += 45) {
            var horPos = HorizontalCoordinates.ofDeg(azDeg, -0.5);
            var planePos = projection.apply(horPos);
            var imagePos = planeToCanvas.transform(planePos.x(), planePos.y());
            ctx.fillText(horPos.azOctantName("N", "E", "S", "O"), imagePos.getX(), imagePos.getY());
        }
    }

    private static double apparentDiameter(double angularSize, StereographicProjection projection, Transform planeToCanvas) {
        var planeSize = projection.applyToAngle(angularSize);
        return planeToCanvas.deltaTransform(planeSize, 0).getX();
    }

    private static double diameterScaleFactor(double magnitude) {
        var m1 = MAGNITUDE_INTERVAL.clip(magnitude);
        return (99d - 17d * m1) / 140d;
    }

    private void drawDisk(Point2D center, double diameter, Color color) {
        drawDisk(center.getX(), center.getY(), diameter, color);
    }

    private void drawDisk(double centerX, double centerY, double diameter, Color color) {
        var radius = diameter / 2d;
        ctx.setFill(color);
        ctx.fillOval(centerX - radius, centerY - radius, diameter, diameter);
    }
}
