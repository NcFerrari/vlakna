package vlakna.frontend.truefx.threads;

import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import vlakna.Values;

import java.util.Random;

public class Joined implements IThread {

    private final SequentialTransition sequentialTransition = new SequentialTransition();
    private final Duration duration = Duration.millis(Values.DEFAULT_DELAY * 125);
    private final Random rnd = new Random();
    private Pane pane;

    @Override
    public void action(String title, Pane pane) {
        if (Animation.Status.RUNNING.equals(sequentialTransition.getStatus())) {
            return;
        }
        this.pane = pane;
        for (int i = 0; i < 5; i++) {
            sequentialTransition.getChildren().add(createTransition(i * Values.SHAPE_RADIUS + Values.SHAPE_RADIUS / 2));
        }
        sequentialTransition.play();
        sequentialTransition.setOnFinished(e -> {
            sequentialTransition.getChildren().forEach(animation -> pane.getChildren().remove(((TranslateTransition) animation).getNode()));
            sequentialTransition.getChildren().clear();
        });
    }

    private TranslateTransition createTransition(int x) {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(duration);
        translateTransition.setNode(createCircle(x));
        translateTransition.setToY(pane.getHeight() - Values.SHAPE_RADIUS - 40);
        translateTransition.setCycleCount(1);
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
