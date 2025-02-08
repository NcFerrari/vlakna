package vlakna.frontend.truefx.threads;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import vlakna.Values;

import java.util.Random;

public class Infinity implements IThread {

    private final Duration duration = Duration.millis(Values.DEFAULT_DELAY * 500.0);
    private final Random rnd = new Random();
    private Pane pane;
    private Circle circle;
    private Circle circle2;
    private PathTransition pathTransition;
    private PathTransition pausePathTransition;

    @Override
    public void action(String title, Pane pane) {
        this.pane = pane;
        switch (title) {
            case Values.THREAD_TYPE_INFINITY_STOP:
                stop();
                break;
            case Values.THREAD_TYPE_INFINITY_WITH_PAUSE:
                infinityWithPause();
                break;
            case Values.THREAD_TYPE_INFINITY_WITH_PAUSE_PAUSE:
                pause();
                break;
            case Values.THREAD_TYPE_INFINITY_WITH_PAUSE_STOP:
                stopPause();
                break;
            default:
                infinity();
        }
    }

    private void pause() {
        if (pausePathTransition != null) {
            if (Animation.Status.RUNNING.equals(pausePathTransition.getStatus())) {
                pausePathTransition.pause();
            } else {
                pausePathTransition.play();
            }
        }
    }

    private void stopPause() {
        if (pausePathTransition != null) {
            pausePathTransition.stop();
            pane.getChildren().remove(circle2);
        }
    }

    private void infinityWithPause() {
        if (pausePathTransition != null && (Animation.Status.RUNNING.equals(pausePathTransition.getStatus()) ||
                Animation.Status.PAUSED.equals(pausePathTransition.getStatus()))) {
            return;
        }
        circle2 = createCircle();
        circle2.setLayoutX(0);
        circle2.setLayoutY(0);
        Path path = new Path();
        MoveTo moveTo = new MoveTo(pane.getWidth() / 2.0 + Values.SHAPE_RADIUS, pane.getHeight() / 2.0);
        ArcTo arcTo = new ArcTo(Values.SHAPE_RADIUS, Values.SHAPE_RADIUS, 0.0,
                pane.getWidth() / 2.0 - Values.SHAPE_RADIUS, pane.getHeight() / 2.0,
                false, true);
        ArcTo arcTo2 = new ArcTo(Values.SHAPE_RADIUS, Values.SHAPE_RADIUS, 0.0,
                pane.getWidth() / 2.0 + Values.SHAPE_RADIUS, pane.getHeight() / 2.0,
                false, true);
        path.getElements().addAll(moveTo, arcTo, arcTo2);

        pausePathTransition = new PathTransition();
        pausePathTransition.setNode(circle2);
        pausePathTransition.setPath(path);
        pausePathTransition.setDuration(duration);
        pausePathTransition.setCycleCount(Animation.INDEFINITE);
        pausePathTransition.setAutoReverse(false);
        pausePathTransition.play();
    }

    private void stop() {
        if (pathTransition != null) {
            pathTransition.stop();
            pane.getChildren().remove(circle);
        }
    }

    private void infinity() {
        if (pathTransition != null && Animation.Status.RUNNING.equals(pathTransition.getStatus())) {
            return;
        }
        circle = createCircle();
        circle.setLayoutX(0);
        circle.setLayoutY(0);
        Path path = new Path();
        MoveTo moveTo = new MoveTo(pane.getWidth() / 2.0 + Values.SHAPE_RADIUS, pane.getHeight() / 2.0);
        ArcTo arcTo = new ArcTo(Values.SHAPE_RADIUS, Values.SHAPE_RADIUS, 0.0,
                pane.getWidth() / 2.0 - Values.SHAPE_RADIUS, pane.getHeight() / 2.0,
                false, true);
        ArcTo arcTo2 = new ArcTo(Values.SHAPE_RADIUS, Values.SHAPE_RADIUS, 0.0,
                pane.getWidth() / 2.0 + Values.SHAPE_RADIUS, pane.getHeight() / 2.0,
                false, true);
        path.getElements().addAll(moveTo, arcTo, arcTo2);

        pathTransition = new PathTransition();
        pathTransition.setNode(circle);
        pathTransition.setPath(path);
        pathTransition.setDuration(duration);
        pathTransition.setCycleCount(Animation.INDEFINITE);
        pathTransition.setAutoReverse(false);
        pathTransition.play();
    }

    private Circle createCircle() {
        Circle localCircle = new Circle();
        localCircle.setRadius(Values.SHAPE_RADIUS / 2.0);
        localCircle.setLayoutX(pane.getWidth() / 2.0);
        localCircle.setLayoutY(pane.getHeight() / 2.0);
        localCircle.setFill(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        pane.getChildren().add(localCircle);
        return localCircle;
    }
}
