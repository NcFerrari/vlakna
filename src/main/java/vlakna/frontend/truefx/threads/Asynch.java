package vlakna.frontend.truefx.threads;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import vlakna.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Asynch implements IThread {

    private final List<TranslateTransition> transitions = new ArrayList<>();
    private final Duration duration = Duration.millis(Values.DEFAULT_DELAY + 1);
    private final Random rnd = new Random();
    private Pane pane;

    @Override
    public void action(String title, Pane pane) {
        this.pane = pane;
        switch (title) {
            case Values.THREAD_TYPE_ASYNCH_MORE_FALLING_BALLS:
                if (transitions.isEmpty()) {
                    fall(5);
                }
                break;
            case Values.THREAD_TYPE_ASYNCH_PAUSE:
                transitions.forEach(t -> {
                    if (Animation.Status.RUNNING.equals(t.getStatus())) {
                        t.pause();
                    } else {
                        t.play();
                    }
                });
                break;
            case Values.THREAD_TYPE_ASYNCH_STOP:
                transitions.forEach(t -> {
                    if (t != null) {
                        t.stop();
                        pane.getChildren().remove(t.getNode());
                    }
                });
                transitions.clear();
                break;
            default:
                if (transitions.isEmpty()) {
                    fall(2);
                }
        }
    }

    private void fall(int count) {
        for (int i = 0; i < count; i++) {
            transitions.add(createTransition(i * Values.SHAPE_RADIUS + Values.SHAPE_RADIUS / 2));
        }
    }

    private TranslateTransition createTransition(int x) {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(duration);
        translateTransition.setNode(createCircle(x));
        translateTransition.setToY(pane.getHeight() - Values.SHAPE_RADIUS - 40);
        translateTransition.setCycleCount(Animation.INDEFINITE);
        translateTransition.setAutoReverse(true);
        translateTransition.play();
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
