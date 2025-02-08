package vlakna.frontend.truefx.threads;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import vlakna.Values;

import java.util.Random;

public class Synch implements IThread {

    private final ParallelTransition parallelTransition = new ParallelTransition();
    private final Duration duration = Duration.millis(Values.DEFAULT_DELAY + 1);
    private final Random rnd = new Random();
    private Pane pane;

    @Override
    public void action(String title, Pane pane) {
        this.pane = pane;
        switch (title) {
            case Values.THREAD_TYPE_SYNCH_MORE_FALLING_BALLS:
                if (Animation.Status.STOPPED.equals(parallelTransition.getStatus())) {
                    fall(5);
                }
                break;
            case Values.THREAD_TYPE_SYNCH_PAUSE:
                if (Animation.Status.RUNNING.equals(parallelTransition.getStatus())) {
                    parallelTransition.pause();
                } else if (Animation.Status.PAUSED.equals(parallelTransition.getStatus())) {
                    parallelTransition.play();
                }
                break;
            case Values.THREAD_TYPE_SYNCH_STOP:
                parallelTransition.stop();
                parallelTransition.getChildren().forEach(child -> pane.getChildren().remove(((TranslateTransition) child).getNode()));
                break;
            default:
                if (Animation.Status.STOPPED.equals(parallelTransition.getStatus())) {
                    fall(2);
                }
        }
    }

    private void fall(int count) {
        for (int i = 0; i < count; i++) {
            parallelTransition.getChildren().add(createTransition(i * Values.SHAPE_RADIUS + Values.SHAPE_RADIUS / 2));
        }
        parallelTransition.play();
    }

    private TranslateTransition createTransition(int x) {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(duration);
        translateTransition.setNode(createCircle(x));
        translateTransition.setToY(pane.getHeight() - Values.SHAPE_RADIUS - 40);
        translateTransition.setCycleCount(Animation.INDEFINITE);
        translateTransition.setAutoReverse(true);
        return translateTransition;
    }

    private Circle createCircle(int x) {
        Circle localCircle = new Circle();
        localCircle.setFill(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        localCircle.setRadius(Values.SHAPE_RADIUS / 2.0);
        localCircle.setLayoutX(x);
        localCircle.setLayoutY(Values.SHAPE_RADIUS / 2.0);
        pane.getChildren().add(localCircle);
        return localCircle;
    }
}
