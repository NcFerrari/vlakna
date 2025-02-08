package vlakna.frontend.truefx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import vlakna.Values;
import vlakna.backend.BEManager;
import vlakna.frontend.truefx.threads.Asynch;
import vlakna.frontend.truefx.threads.IThread;
import vlakna.frontend.truefx.threads.Infinity;
import vlakna.frontend.truefx.threads.Joined;
import vlakna.frontend.truefx.threads.Simple;
import vlakna.frontend.truefx.threads.Synch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class App extends Application {

    private final Map<String, Runnable> threadMap = new HashMap<>();

    private Pane mainPane;

    @Override
    public void start(Stage stage) throws Exception {
        mainPane = new Pane();
        stage.setTitle(Values.TRUE_FX_TITLE);
        stage.setWidth(Values.WIDTH);
        stage.setHeight(Values.HEIGHT);
        Scene scene = new Scene(mainPane);
        String css = Objects.requireNonNull(getClass().getResource(Values.CSS_FILE)).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();

        Pane animationPane = new Pane();
        animationPane.setPrefSize(Values.WIDTH, 4 * Values.HEIGHT / 5.0);
        animationPane.setLayoutY(Values.HEIGHT / 5.0);
        mainPane.getChildren().add(animationPane);

        fillThreadMap(animationPane);

        createButtons();
    }

    private void fillThreadMap(Pane animationPane) {
        final IThread simple = new Simple();
        final IThread infinity = new Infinity();
        final IThread asynch = new Asynch();
        final IThread synch = new Synch();
        final IThread joined = new Joined();
        threadMap.put(Values.THREAD_TYPE_SIMPLE, () -> simple.action(Values.THREAD_TYPE_SIMPLE, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_FALL, () -> simple.action(Values.THREAD_TYPE_SIMPLE_FALL, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_FADE, () -> simple.action(Values.THREAD_TYPE_SIMPLE_FADE, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_ROTATE, () -> simple.action(Values.THREAD_TYPE_SIMPLE_ROTATE, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_FILL, () -> simple.action(Values.THREAD_TYPE_SIMPLE_FILL, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_STROKE, () -> simple.action(Values.THREAD_TYPE_SIMPLE_STROKE, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_GRADIENT, () -> simple.action(Values.THREAD_TYPE_SIMPLE_GRADIENT, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_TRANSFORM, () -> simple.action(Values.THREAD_TYPE_SIMPLE_TRANSFORM, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_SCALE, () -> simple.action(Values.THREAD_TYPE_SIMPLE_SCALE, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_TIMELINE, () -> simple.action(Values.THREAD_TYPE_SIMPLE_TIMELINE, animationPane));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_PROGRESS_BAR, () -> simple.action(Values.THREAD_TYPE_SIMPLE_PROGRESS_BAR, animationPane));

        threadMap.put(Values.THREAD_TYPE_INFINITY, () -> infinity.action(Values.THREAD_TYPE_INFINITY, animationPane));
        threadMap.put(Values.THREAD_TYPE_INFINITY_STOP, () -> infinity.action(Values.THREAD_TYPE_INFINITY_STOP, animationPane));

        threadMap.put(Values.THREAD_TYPE_INFINITY_WITH_PAUSE, () -> infinity.action(Values.THREAD_TYPE_INFINITY_WITH_PAUSE, animationPane));
        threadMap.put(Values.THREAD_TYPE_INFINITY_WITH_PAUSE_PAUSE, () -> infinity.action(Values.THREAD_TYPE_INFINITY_WITH_PAUSE_PAUSE, animationPane));
        threadMap.put(Values.THREAD_TYPE_INFINITY_WITH_PAUSE_STOP, () -> infinity.action(Values.THREAD_TYPE_INFINITY_WITH_PAUSE_STOP, animationPane));

        threadMap.put(Values.THREAD_TYPE_ASYNCH, () -> asynch.action(Values.THREAD_TYPE_ASYNCH, animationPane));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_FALL, () -> asynch.action(Values.THREAD_TYPE_ASYNCH_FALL, animationPane));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_MORE_FALLING_BALLS, () -> asynch.action(Values.THREAD_TYPE_ASYNCH_MORE_FALLING_BALLS, animationPane));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_PAUSE, () -> asynch.action(Values.THREAD_TYPE_ASYNCH_PAUSE, animationPane));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_STOP, () -> asynch.action(Values.THREAD_TYPE_ASYNCH_STOP, animationPane));

        threadMap.put(Values.THREAD_TYPE_SYNCH, () -> synch.action(Values.THREAD_TYPE_SYNCH, animationPane));
        threadMap.put(Values.THREAD_TYPE_SYNCH_FALL, () -> synch.action(Values.THREAD_TYPE_SYNCH_FALL, animationPane));
        threadMap.put(Values.THREAD_TYPE_SYNCH_MORE_FALLING_BALLS, () -> synch.action(Values.THREAD_TYPE_SYNCH_MORE_FALLING_BALLS, animationPane));
        threadMap.put(Values.THREAD_TYPE_SYNCH_PAUSE, () -> synch.action(Values.THREAD_TYPE_SYNCH_PAUSE, animationPane));
        threadMap.put(Values.THREAD_TYPE_SYNCH_STOP, () -> synch.action(Values.THREAD_TYPE_SYNCH_STOP, animationPane));

        threadMap.put(Values.THREAD_TYPE_JOINED_FALLING_BALLS, () -> joined.action(Values.THREAD_TYPE_JOINED_FALLING_BALLS, animationPane));
    }

    private void createButtons() {
        ScrollPane buttonScrollPane = new ScrollPane();
        buttonScrollPane.setPrefSize(Values.WIDTH, Values.HEIGHT / 5.0);
        mainPane.getChildren().add(buttonScrollPane);

        Pane buttonPane = new Pane();
        buttonScrollPane.setContent(buttonPane);

        double x = 0.0;
        double maxButtonHeight = 0.0;
        for (String[] titles : BEManager.getInstance().getThreadButtonTitles()) {
            double y = 0.0;
            for (String title : titles) {
                if (Values.THREAD_TYPE_ASYNCH_TEXTS.equals(title)
                        || Values.THREAD_TYPE_ASYNCH_FALL.equals(title)
                        || Values.THREAD_TYPE_ASYNCH_MOVE_TO_TARGET.equals(title)
                        || Values.THREAD_TYPE_ASYNCH_MORE_BALLS.equals(title)
                        || Values.THREAD_TYPE_SYNCH_FALL.equals(title)
                        || Values.THREAD_TYPE_SYNCH_TEXTS.equals(title)
                        || Values.THREAD_TYPE_SYNCH_MOVE_TO_TARGET.equals(title)
                        || Values.THREAD_TYPE_SYNCH_MORE_BALLS.equals(title)
                        || Values.THREAD_TYPE_COORDINATION_MORE_FALLING_BALLS.equals(title)
                        || Values.THREAD_TYPE_COORDINATION_PAUSE.equals(title)
                        || Values.THREAD_TYPE_COORDINATION_STOP.equals(title)
                        || Values.THREAD_TYPE_DAEMON.equals(title)
                        || Values.THREAD_TYPE_JOINED.equals(title)
                ) {
                    continue;
                }
                if (Values.THREAD_TYPE_COORDINATION.equals(title)
                        || Values.THREAD_TYPE_NO_DAEMON.equals(title)
                        || Values.THREAD_TYPE_SHOW_CURRENT_THREADS.equals(title)
                ) {
                    x -= Values.BUTTON_WIDTH;
                    continue;
                }
                buttonPane.getChildren().add(createButton(title, x, y));
                y += Values.BUTTON_HEIGHT + Values.BUTTON_PADDING;
                if (Values.THREAD_TYPE_SIMPLE_TRANSFORM.equals(title)) {
                    buttonPane.getChildren().add(createButton(Values.THREAD_TYPE_SIMPLE_SCALE, x, y));
                    y += Values.BUTTON_HEIGHT + Values.BUTTON_PADDING;
                    buttonPane.getChildren().add(createButton(Values.THREAD_TYPE_SIMPLE_TIMELINE, x, y));
                    y += Values.BUTTON_HEIGHT + Values.BUTTON_PADDING;
                    buttonPane.getChildren().add(createButton(Values.THREAD_TYPE_SIMPLE_PROGRESS_BAR, x, y));
                    y += Values.BUTTON_HEIGHT + Values.BUTTON_PADDING;
                }
                if (maxButtonHeight < y) {
                    maxButtonHeight = y;
                }
            }
            x += Values.BUTTON_WIDTH;
        }
        buttonPane.setPrefSize(x, maxButtonHeight);
    }

    private Button createButton(String title, double x, double y) {
        Button button = new Button(title);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefSize(Values.BUTTON_WIDTH, Values.BUTTON_HEIGHT);
        button.setOnAction(actionEvent -> threadMap.get(title).run());
        return button;
    }
}