package vlakna.frontend.truefx.threads;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import vlakna.Values;
import vlakna.backend.H;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Simple implements IThread {

    private final Duration duration = Duration.millis(Values.DEFAULT_DELAY * 500.0);
    private final Random rnd = new Random();
    private Animation animation;
    private Pane pane;
    private Circle circle;
    private Circle fromCircle;
    private Circle toCircle;
    private Task<Integer> task;

    @Override
    public void action(String title, Pane pane) {
        this.pane = pane;
        if (animation != null && Animation.Status.RUNNING.equals(animation.getStatus()) || task != null && task.isRunning()) {
            return;
        }
        circle = createCircle();
        switch (title) {
            case Values.THREAD_TYPE_SIMPLE_FALL:
                fall();
                break;
            case Values.THREAD_TYPE_SIMPLE_FADE:
                fade();
                break;
            case Values.THREAD_TYPE_SIMPLE_ROTATE:
                rotate();
                break;
            case Values.THREAD_TYPE_SIMPLE_FILL:
                fill();
                break;
            case Values.THREAD_TYPE_SIMPLE_STROKE:
                stroke();
                break;
            case Values.THREAD_TYPE_SIMPLE_GRADIENT:
                gradient();
                break;
            case Values.THREAD_TYPE_SIMPLE_TRANSFORM:
                transform();
                break;
            case Values.THREAD_TYPE_SIMPLE_SCALE:
                scale();
                break;
            case Values.THREAD_TYPE_SIMPLE_TIMELINE:
                timeline();
                break;
            case Values.THREAD_TYPE_SIMPLE_PROGRESS_BAR:
                progressBar();
                break;
            default:
                simple();
        }

        if (animation != null) {
            EventHandler<ActionEvent> existingHandler = animation.getOnFinished();
            animation.setOnFinished(actionEvent -> {
                if (existingHandler != null) {
                    existingHandler.handle(actionEvent);
                }
                pane.getChildren().remove(circle);
                pane.getChildren().remove(fromCircle);
                pane.getChildren().remove(toCircle);
            });
        }
    }

    /**
     * Přes animaci se kružnicová rotace dělá nejlépe přes dva oblouky (ArcTo)
     * Vzhledem k odfláklému pojmenování a mizerné dokumentaci, tak uvádím popis konstruktoru:<br>
     * ArcTo(double radiusX, double radiusY, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag)
     * přičemž:
     * <ul>
     * <li>radiusX, radiusY – poloměry elipsy určující tvar oblouku.</li>
     * <li>xAxisRotation – úhel rotace elipsy.</li>
     * <li>x, y – koncový bod oblouku.</li>
     * <li>largeArcFlag – určuje, zda se vykreslí menší nebo větší oblouk.</li>
     * <li>sweepFlag – určuje směr vykreslení oblouku (po směru hodinových ručiček nebo proti).</li>
     * </ul>
     * <p>
     * A protože se jedná jen o půl oblouk, potřebujeme tedy dva.
     */
    private void simple() {
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

        PathTransition pathTransition = new PathTransition();
        animation = pathTransition;
        pathTransition.setNode(circle);
        pathTransition.setPath(path);
        pathTransition.setDuration(duration);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(true);
        pathTransition.play();
    }

    private void legacysimple() {
        circle.setLayoutX(0);
        Path path = new Path();
        H.setX(pane.getWidth() / 2.0);
        H.setY(pane.getHeight() / 2.0);
        H.setDistance(Values.SHAPE_RADIUS * 2);
        double[] xy = H.getRadiusByAngle(0);
        path.getElements().add(new MoveTo(xy[0], xy[1]));
        for (int i = 0; i < 360; i++) {
            xy = H.getRadiusByAngle(i);
            LineTo lineTo = new LineTo(xy[0], xy[1]);
            path.getElements().add(lineTo);
        }

        PathTransition pathTransition = new PathTransition();
        animation = pathTransition;
        pathTransition.setNode(circle);
        pathTransition.setPath(path);
        pathTransition.setDuration(duration);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(true);
        pathTransition.play();
    }

    private void fall() {
        TranslateTransition translateTransition = new TranslateTransition();
        animation = translateTransition;
        translateTransition.setDuration(duration);
        translateTransition.setNode(circle);
        translateTransition.setToY(pane.getHeight() - Values.SHAPE_RADIUS - 40);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);
        translateTransition.play();
    }

    private void fade() {
        circle.setRadius(Values.SHAPE_RADIUS);
        circle.setLayoutY(pane.getHeight() / 2.0 - Values.SHAPE_RADIUS / 2.0);

        FadeTransition fadeTransition = new FadeTransition();
        animation = fadeTransition;
        fadeTransition.setDuration(duration);
        fadeTransition.setNode(circle);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setByValue(0.01);
        fadeTransition.setCycleCount(2);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }

    private void rotate() {
        pane.getChildren().remove(circle);
        Rectangle rectangle = cretateRectangle();

        RotateTransition rotateTransition = new RotateTransition();
        animation = rotateTransition;
        rotateTransition.setDuration(duration.multiply(2.0));
        rotateTransition.setNode(rectangle);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(1);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
        rotateTransition.setOnFinished(actionEvent -> pane.getChildren().remove(rectangle));
    }

    private void fill() {
        useThreeBalls();
        circle.setFill(fromCircle.getFill());

        FillTransition fillTransition = new FillTransition();
        animation = fillTransition;
        fillTransition.setDelay(Duration.millis(1_000));
        fillTransition.setDuration(duration);
        fillTransition.setShape(circle);
        fillTransition.setFromValue((Color) fromCircle.getFill());
        fillTransition.setToValue((Color) toCircle.getFill());
        fillTransition.setCycleCount(1);
        fillTransition.setAutoReverse(false);
        fillTransition.play();
        fillTransition.setOnFinished(actionEvent -> H.sleep(1_000));
    }

    private void stroke() {
        useThreeBalls();
        circle.setStrokeWidth(10);

        StrokeTransition strokeTransition = new StrokeTransition();
        animation = strokeTransition;
        strokeTransition.setDuration(duration);
        strokeTransition.setShape(circle);
        strokeTransition.setFromValue((Color) fromCircle.getFill());
        strokeTransition.setToValue((Color) toCircle.getFill());
        strokeTransition.setCycleCount(1);
        strokeTransition.setAutoReverse(false);
        strokeTransition.play();
        strokeTransition.setOnFinished(actionEvent -> H.sleep(1_000));
    }

    private void gradient() {
        circle.setRadius(Values.SHAPE_RADIUS);
        circle.setLayoutY(pane.getHeight() / 2.0 - Values.SHAPE_RADIUS / 2.0);

        Color firstColor = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        Color secondColor = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        Timeline timeline = new Timeline();
        animation = timeline;

        for (double i = 0; i < 1; i += 0.002) {
            Color interpolatedFirstColor = firstColor.interpolate(secondColor, i);
            Color interpolatedSecondColor = secondColor.interpolate(firstColor, i);
            LinearGradient linearGradient = new LinearGradient(0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE, new Stop(0, interpolatedFirstColor), new Stop(1, interpolatedSecondColor));

            KeyValue keyValue = new KeyValue(circle.fillProperty(), linearGradient);

            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 2_000), keyValue);

            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    /**
     * Funkční metoda a je to i čistý kód, ovšem díky tomu, že pracujeme s tranzicemi, tak se v
     * tomto projektu nedá použít. Leda někde separátně
     */
    private void legacy_gradient() {
        circle.setRadius(Values.SHAPE_RADIUS);
        circle.setLayoutY(pane.getHeight() / 2.0 - Values.SHAPE_RADIUS / 2.0);

        double[] color = new double[]{rnd.nextInt(128), rnd.nextInt(128), rnd.nextInt(128)};
        double[] secondColor = new double[]{256 - rnd.nextInt(128), 256 - rnd.nextInt(128), 256 - rnd.nextInt(128)};
        double[] colorSteps = new double[color.length];
        for (int i = 0; i < colorSteps.length; i++) {
            colorSteps[i] = (secondColor[i] - color[i]) / Values.RATIO;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    for (int i = 0; i < Values.RATIO; i++) {
                        circle.setFill(new LinearGradient(0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb((int) (color[0] + colorSteps[0] * i), (int) (color[1] + colorSteps[1] * i), (int) (color[2] + colorSteps[2] * i))), new Stop(1, Color.rgb((int) (secondColor[0] + colorSteps[0] * -i), (int) (secondColor[1] + colorSteps[1] * -i), (int) (secondColor[2] + colorSteps[2] * -i)))));
                        H.sleep(10);
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    private void timeline() {
        pane.getChildren().remove(circle);
        Rectangle rectangle = cretateRectangle();
        rectangle.setWidth(100);
        rectangle.setHeight(100);
        rectangle.setLayoutX(0);
        rectangle.setLayoutY(0);

        KeyValue keyValue = new KeyValue(rectangle.xProperty(), pane.getWidth() - rectangle.getWidth());
        KeyValue keyValue2 = new KeyValue(rectangle.yProperty(), pane.getHeight() - 2 * rectangle.getHeight());
        KeyValue keyValue3 = new KeyValue(rectangle.fillProperty(), Color.RED);
        KeyValue keyValue4 = new KeyValue(rectangle.scaleXProperty(), 0.5);
        KeyValue keyValue5 = new KeyValue(rectangle.scaleYProperty(), 0.5);
        KeyValue keyValue6 = new KeyValue(rectangle.arcWidthProperty(), 100);
        KeyValue keyValue7 = new KeyValue(rectangle.arcHeightProperty(), 100);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), keyValue, keyValue2, keyValue3, keyValue4, keyValue5);
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(5), keyValue6, keyValue7);

        Timeline timeline = new Timeline();
        animation = timeline;
        timeline.getKeyFrames().addAll(keyFrame, keyFrame2);

        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        timeline.play();

        timeline.setOnFinished(actionEvent -> pane.getChildren().remove(rectangle));
    }

    /**
     * Info k barvě. -fx-accent je nastavení výchozí barvy (ale jen jednobarevné!!)
     * Proto pro přechod je potřeba nastavit gradient přes css file
     */
    private void progressBar() {
        pane.getChildren().remove(circle);
        ProgressBar progressBar = new ProgressBar();
        pane.getChildren().add(progressBar);
//        progressBar.setStyle("-fx-accent: green;");
        String css = Objects.requireNonNull(getClass().getResource(Values.CSS_FILE)).toExternalForm();
        progressBar.getStylesheets().add(css);
        progressBar.setPrefSize(Values.SHAPE_RADIUS * 4.0, Values.SHAPE_RADIUS / 2.0);
        progressBar.setLayoutX(pane.getWidth() / 2 - progressBar.getPrefWidth() / 2);
        progressBar.setLayoutY(pane.getHeight() / 2 - progressBar.getPrefHeight() / 2);
        task = new Task<>() {
            @Override
            protected Integer call() {
                for (int i = 0; i < 1_000; i++) {
                    updateProgress(i, 1_000);
                    H.sleep();
                }
                return null;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> pane.getChildren().remove(progressBar));
        new Thread(task).start();
    }

    private void transform() {
        pane.getChildren().remove(circle);
        double sx = pane.getWidth() / 2.0;
        double sy = pane.getHeight() / 2.0;

        Polygon polygon = new Polygon();
        polygon.setFill(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        pane.getChildren().add(polygon);

        final List<DoubleProperty> points = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            DoubleProperty doubleProperty = new SimpleDoubleProperty();
            points.add(doubleProperty);
            doubleProperty.addListener((obs, oldVal, newVal) -> polygon.getPoints().setAll(points.stream().map(DoubleProperty::get).toArray(Double[]::new)));
        }

        points.get(0).set(sx - Values.SHAPE_RADIUS);
        points.get(1).set(sy - Values.SHAPE_RADIUS);
        points.get(2).set(sx + Values.SHAPE_RADIUS);
        points.get(3).set(sy - Values.SHAPE_RADIUS);
        points.get(4).set(sx + Values.SHAPE_RADIUS);
        points.get(5).set(sy + Values.SHAPE_RADIUS);
        points.get(6).set(sx - Values.SHAPE_RADIUS);
        points.get(7).set(sy + Values.SHAPE_RADIUS);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), new KeyValue(points.get(1), points.get(1).get() + Values.SHAPE_RADIUS * 2), new KeyValue(points.get(3), points.get(3).get() + Values.SHAPE_RADIUS * 2), new KeyValue(points.get(4), points.get(4).get() - Values.SHAPE_RADIUS), new KeyValue(points.get(5), points.get(5).get() - Values.SHAPE_RADIUS * 2), new KeyValue(points.get(6), points.get(6).get() + Values.SHAPE_RADIUS), new KeyValue(points.get(7), points.get(7).get() - Values.SHAPE_RADIUS * 2));

        Timeline timeline = new Timeline();
        animation = timeline;
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        timeline.play();

        timeline.setOnFinished(actionEvent -> pane.getChildren().remove(polygon));
    }

    private void legacyTransform() {
        pane.getChildren().remove(circle);
        Polygon polygon = new Polygon();
        pane.getChildren().add(polygon);
        ObservableList<Double> points = polygon.getPoints();
        double sx = pane.getWidth() / 2.0;
        double sy = pane.getHeight() / 2.0;
        points.addAll(sx - Values.SHAPE_RADIUS, sy - Values.SHAPE_RADIUS, sx + Values.SHAPE_RADIUS, sy - Values.SHAPE_RADIUS, sx + Values.SHAPE_RADIUS, sy + Values.SHAPE_RADIUS, sx - Values.SHAPE_RADIUS, sy + Values.SHAPE_RADIUS);

        Task<Double> task = new Task<>() {
            @Override
            protected Double call() {
                for (int i = 0; i < Values.SHAPE_RADIUS * 2; i++) {
                    Platform.runLater(() -> {
                        points.set(1, points.get(1) + 1);
                        points.set(3, points.get(3) + 1);
                        points.set(4, points.get(4) - 0.5);
                        points.set(5, points.get(5) - 1);
                        points.set(6, points.get(6) + 0.5);
                        points.set(7, points.get(7) - 1);
                    });
                    H.sleep();
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> pane.getChildren().remove(polygon));
        new Thread(task).start();
    }

    /**
     * Scale se myslí procentuální velikost. Takže 1 znamená, že se objekt nezmění, 2 dvojnásobek
     * atd. Proto je to double, protože se dá takto přímo napsat poměr zvětšení
     * (když to bude v rozmezí 0 až 1, tak to bude zmenšení)
     */
    private void scale() {
        circle.setLayoutY(pane.getHeight() / 2);
        double newSize = rnd.nextDouble() * rnd.nextInt(2);

        ScaleTransition scaleTransition = new ScaleTransition();
        animation = scaleTransition;
        scaleTransition.setDuration(duration);
        scaleTransition.setNode(circle);
        scaleTransition.setToX(newSize);
        scaleTransition.setToY(newSize);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(false);
        scaleTransition.play();
    }

    private Circle createCircle() {
        Circle localCircle = new Circle();
        localCircle.setFill(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        localCircle.setRadius(Values.SHAPE_RADIUS / 2.0);
        localCircle.setLayoutX(pane.getWidth() / 2.0);
        localCircle.setLayoutY(Values.SHAPE_RADIUS / 2.0);
        pane.getChildren().add(localCircle);
        return localCircle;
    }

    private void useThreeBalls() {
        fromCircle = createCircle();
        toCircle = createCircle();

        circle.setRadius(Values.SHAPE_RADIUS);
        circle.setLayoutY(pane.getHeight() / 2.0 - Values.SHAPE_RADIUS / 2.0);

        fromCircle.setRadius(Values.SHAPE_RADIUS / 2.0);
        fromCircle.setLayoutX(circle.getLayoutX() - 3 * Values.SHAPE_RADIUS / 2.0);
        fromCircle.setLayoutY(pane.getHeight() / 2.0 - Values.SHAPE_RADIUS / 2.0);

        toCircle.setRadius(Values.SHAPE_RADIUS / 2.0);
        toCircle.setLayoutX(circle.getLayoutX() + 3 * Values.SHAPE_RADIUS / 2.0);
        toCircle.setLayoutY(pane.getHeight() / 2.0 - Values.SHAPE_RADIUS / 2.0);
    }

    private Rectangle cretateRectangle() {
        Rectangle localRectangle = new Rectangle();
        localRectangle.setFill(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        localRectangle.setWidth(Values.SHAPE_RADIUS * 2.0);
        localRectangle.setHeight(Values.SHAPE_RADIUS * 2.0);
        localRectangle.setLayoutX(pane.getWidth() / 2.0 - localRectangle.getWidth() / 2);
        localRectangle.setLayoutY(pane.getHeight() / 2.0 - localRectangle.getHeight() / 2);
        pane.getChildren().add(localRectangle);
        return localRectangle;
    }
}
