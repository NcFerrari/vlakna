package vlakna.frontend.fx;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import vlakna.Values;

import java.util.Random;

public class FXShape {

    private final Random rnd = new Random();
    private final Circle circle;
    private final Polygon polygon;
    private final Rectangle rectangle;

    public FXShape() {
        circle = new Circle();
        circle.setFill(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        circle.setRadius(Values.SHAPE_RADIUS / 2.0);

        rectangle = new Rectangle();
        rectangle.setFill(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));

        polygon = new Polygon();
        polygon.setFill(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
    }

    public void setColor(Color color) {
        circle.setFill(color);
    }

    public void setStrokeColor(Color color) {
        circle.setStrokeWidth(Values.STROKE_WIDTH);
        circle.setStroke(color);
    }

    public void setAngle(double angle) {
        circle.setRadius(0);
        rectangle.setWidth(Values.SHAPE_RADIUS);
        rectangle.setHeight(Values.SHAPE_RADIUS);
        rectangle.setRotate(Math.toDegrees(angle));
    }

    public void setAlpha(float fadeValue) {
        circle.setOpacity(fadeValue);
    }

    public void addToPane(App app) {
        app.addNode(circle);
        app.addNode(polygon);
        app.addNode(rectangle);
    }

    public void setLocation(double x, double y) {
        circle.setLayoutX(x + circle.getRadius());
        circle.setLayoutY(y + circle.getRadius());
        rectangle.setLayoutX(x);
        rectangle.setLayoutY(y);
        polygon.setLayoutX(x);
        polygon.setLayoutY(y);
    }

    public int getX() {
        return (int) (circle.getLayoutX() - circle.getRadius());
    }

    public int getY() {
        return (int) (circle.getLayoutY() - circle.getRadius());
    }

    public void removeFromPane(App app) {
        app.removeNode(circle);
        app.removeNode(rectangle);
        app.removeNode(polygon);
    }

    public void setGradient(Color color, Color color2) {
        LinearGradient linearGradient = new LinearGradient(0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, color),
                new Stop(1, color2));
        circle.setFill(linearGradient);
    }

    public void transform(double progress) {
        circle.setRadius(0);
        rectangle.setWidth(0);
        rectangle.setHeight(0);
        polygon.getPoints().clear();
        polygon.getPoints().addAll(
                0.0, progress * (double) Values.SHAPE_RADIUS,
                (double) Values.SHAPE_RADIUS, progress * (double) Values.SHAPE_RADIUS,
                (double) Values.SHAPE_RADIUS - progress * (double) Values.SHAPE_RADIUS / 2, (double) Values.SHAPE_RADIUS - progress * (double) Values.SHAPE_RADIUS,
                progress * (double) Values.SHAPE_RADIUS / 2, (double) Values.SHAPE_RADIUS - progress * (double) Values.SHAPE_RADIUS
        );
    }
}
