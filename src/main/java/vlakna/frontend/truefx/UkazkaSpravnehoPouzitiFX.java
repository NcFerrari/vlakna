package vlakna.frontend.truefx;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import vlakna.Values;
import vlakna.backend.H;

import java.util.Objects;

/**
 * Vlákna v JavaFX (třída Task, třída Platform a Transformace)<br><br>
 * Java FX je single-threaded aplikace. To znamená, že běží pouze v jednom vlákně. To by mohlo na jedné straně znamenat,
 * že nedovolí synchronizování UI komponent (není bezpečné a hlavně dochází k chybám, pokud by samostatná vlákna
 * jakkoliv zkoušela manipulovat s UI komponentami bez přihlášení k hlavnímu vláknu FX aplikace). Nicméně existuje
 * možnost, jak to FX umožní a tou je generická třída Task.<br>
 * Task je speciální třída v JavaFX, která umožní spouštět operace v samostatném vlákně nezávisle na hlavním vlákně FX
 * (hlavní vlákno FX se nazývá: JavaFX Application Thread).<br>
 * Ze třídy Task nás jako uživatele zajímá nejvíc následujícíh 5 metod:
 * <ul>
 *     <li>call()</li>
 *     <li>updateProgress</li>
 *     <li>updateMessage</li>
 *     <li>progressProperty</li>
 *     <li>messageProperty</li>
 * </ul>
 * s tím, že metody updateProgress a updateMessage se používají v metodě call() a poslední dvě metody (progressProperty
 * a messageProperty) se používají při bindování instance Task třídy ke konkrétnímu Nodu.<br><br>
 * Dále je tu třída Platform a její statická metoda runLater(), která se používá při manipulaci s GUI (ať už ve vlákně
 * třídy Task nebo mimo něj).
 * Je také důležité si uvědomit, že metody updateProgress a updateMessage pouze aktualizují hodnoty vlastností. Tyto
 * vlastnosti se potom musí ručně bindovat ke komponentám.<br><br>
 * Příklady použití:
 * 1) Pokud chceme v rámci nějaké komponenty použít vlákno a použijeme Task třídu s tím, že nabindujeme toto vlákno
 * ke konkrétní komponentě. Příkladem může být progressBar nebo posun Nodu.<br>
 * <code>
 * ProgressBar progressBar = new ProgressBar(0);<br>
 * Task&lt;Void&gt; task = new Task<>() {<br>
 * &#64;Override<br>
 * protected Void call() {<br>
 * &#9;for (int i = 0; i <= 100; i++) {<br>
 * &#9;&#9;updateProgress(i, 100);<br>
 * &#9;&#9;H.sleep(50);<br>
 * &#9;}<br>
 * &#9;return null;<br>
 * &#9;}<br>
 * };<br>
 * <br>
 * progressBar.progressProperty().bind(task.progressProperty());<br>
 * new Thread(task).start();
 * </code><br><br>
 * V případě, že bychom chtěli pracovat s hodnotou, kteoru metoda call() vrátí po jejím dokončení (kdybychom nezadali
 * Void, ale třeba Integer) tak bychom mohli použít metody pro posluchače v javě FX na task.<br>
 * Například:<br>
 * <code>
 * task.setOnSucceeded(e -> System.out.println(task.getValue());
 * </code><br><br>
 * Významy metod posluchače:
 * <ul>
 *     <li>task.setOnScheduled() -> Tento posluchač se provede ještě před samotným spuštěním tasku</li>
 *     <li>task.setOnRunning()   -> Tento posluchač se provede v moementě, kdy task běží</li>
 *     <li>task.setOnSucceeded() -> Tento posluchač se provede po úspěšném dokončení tasku</li>
 *     <li>task.setOnFailed()    -> Tento posluchač se provede, když se například vyhodí výjimka během provádění úlohy</li>
 *     <li>task.setOnCancelled() -> Tento posluchač se provede, když je task přerušen</li>
 * </ul>
 * Task prochází těmito šesti stavy a právě na každý stav má Task svůj posluchač (kromě prvotního stavu, který vlastně
 * představuje inicializaci tasku):
 * <ul>
 *     <li>1. READY         (nemá posluchač)    inicializace</li>
 *     <li>2. SCHEDULED     (setOnScheduled())  to, co se stane těsně po spuštěním vlákna s taskem</li>
 *     <li>3. RUNNING       (setOnRunning())    to, co se stane, když vlákno běží</li>
 *     <li>4. SUCCEEDED     (setOnSucceeded())  to, co se stane po úspěšném skončení vlákna tasku</li>
 *     <li>5. FAILED        (setOnFailed())     to, co se stane, když vlákno tasku spadne</li>
 *     <li>6. CANCELLED     (setOnCancelled())  to, co se stane, když se přeruší task. To se může stát třeba tak, že na
 *     task zavoláme metodu task.cancel(). Tím ukočníme okamžitě vlákno. Čistý zápis je i ten, kterým upodmňujeme přímo
 *     v metodě call() process pomocí podmínky isCancelled()</li>
 * </ul>
 * 2) Pokud nebudeme Task bindovat na konkrétní komponentu tak můžeme použít Platform třídu a její statickou metodu
 * runLater(). Je to vlastně takový "ruční" způsob použití předchozího použití.<br>
 * <code>
 * ShapeTemplate shape = setShape(Values.OVAL_NAME + 0);<br>
 * Task&lt;Void&gt; task = new Task<>() {<br>
 * &#9;&#64;Override<br>
 * &#9;protected Void call() {<br>
 * &#9;&#9;for (int i = Values.SHAPE_RADIUS / 2; i < pane.getPrefHeight() - 36 - Values.SHAPE_RADIUS / 2.0; i++) {<br>
 * &#9;&#9;&#9;double newPosition = i;<br>
 * &#9;&#9;&#9;Platform.runLater(() -> shape.getShape().setLayoutY(newPosition));<br>
 * &#9;&#9;&#9;H.sleep(2);<br>
 * &#9;&#9;}<br>
 * &#9;&#9;unlockThread(shape, buttonText);<br>
 * &#9;&#9;return null;<br>
 * &#9;}<br>
 * };<br>
 * new Thread(task).start();
 * </code><br><br>
 * 3) Můžeme se obejít úplně bez Task třídy. V takovém případě ovšem MUSÍME aspoň použít Platform.runLater(). Díky
 * metodě runLater() se její blok promítne do JavaFX Application Thread vlákna.<br>
 * Příklad použití:<br>
 * <code>
 * ShapeTemplate shape = setShape(Values.OVAL_NAME + 0);<br>
 * new Thread(() -> {<br>
 * &#9;for (int i = Values.SHAPE_RADIUS / 2; i < pane.getPrefHeight() - 36 - Values.SHAPE_RADIUS / 2.0; i++) {<br>
 * &#9;&#9;double newPosition = i;<br>
 * &#9;&#9;Platform.runLater(() -> shape.getShape().setLayoutY(newPosition));<br>
 * &#9;&#9;H.sleep(2);<br>
 * &#9;}<br>
 * &#9;unlockThread(shape, buttonText);<br>
 * }).start();
 * </code><br><br>
 * Tím se dá i říct, že vlastně jediný rozdíl mezi 2. a 3. případem je, že třetí případ se obejde bez Task třídy.
 * A je to tak. Ono i v prvním případě se používá Platform.runLater(), ovšem je to vnitřní implementace bindování na
 * node.<br>
 * TRANSFORMACE<br>
 * Nakonec je tu ještě jedna možnost, jak v rámci efektů pracovat s vlákny v JavěFX. Jedná se o tzv. animační
 * transformace. Zásadní rozdíly jsou:
 * <ul>
 *     <li>Není třeba definovat ani Thread, ani Task ani pracovat s Platform třídou</li>
 *     <li>Veškerá práce s vláknem se odehrává pomocí metod na instanci transformace</li>
 *     <li>V případě pozicování se nepracuje se souřadnicemi Layout, ale s translate souřadnicemi (což je i ten rozdíl
 *     proč existují metody na node jako jsou například: setLayoutY(double), setTranslateY(double)</li>
 * </ul>
 * V podstatě se jedná o třídu, která nějakým způsobem "transformuje" daný Node. Existují různé možnosti, jak node
 * transformovat:
 * <ul>
 *     <li>TranslateTransition -> postupně posunuje node</li>
 *     <li>FadeTransition      -> nastavuje postupné opaque (zprůhlednění)</li>
 *     <li>RotateTransition    -> postupně otáčí node</li>
 *     <li>FillTransition      -> postupně mění barvu z jedné do druhé (from - to)</li>
 *     <li>StrokeTransition    -> postupně mění barvu okraje z jedné do druhé (from - to) pozn.: je to úplně stejné,
 *     jako fillTransition, akorát pro okraj</li>
 *     <li>ScaleTransition     -> postupně upravuje zoom nebo poměr velikost chcete-li. uvádí se desetinných číslech,
 *     abychom mohli dobře nastavit poměr</li>
 * </ul>
 * <p>
 * NEJČÍSTŠNÍ ZPŮSOB PSANÍ KÓDU JE OVŠEM PRVNÍ A POSLEDNÍ PŘÍPAD!!
 */
public class UkazkaSpravnehoPouzitiFX extends Application {

    private static final int BUTTON_WIDTH = 4 * Values.BUTTON_WIDTH / 3;

    private Pane mainPane;

    @Override
    public void start(Stage stage) throws Exception {
        mainPane = new Pane();
        stage.setTitle(Values.TRUE_FX_EXAMPLE_TITLE);
        Scene scene = new Scene(mainPane, Values.WIDTH, Values.HEIGHT);
        String css = Objects.requireNonNull(getClass().getResource(Values.CSS_FILE)).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();

        createButtons();
    }

    private void createButtons() {
        String[] titles = {Values.FIRST_EXAMPLE_TITLE, Values.FIRST_EXAMPLE_WITH_PROGRESS_BAR_TITLE, Values.SECOND_EXAMPLE_TITLE, Values.THIRD_EXAMPLE_TITLE, Values.FOURTH_EXAMPLE_TITLE};
        for (double i = 0; i < titles.length; i++) {
            Button button = new Button(titles[(int) i]);
            button.setPrefSize(BUTTON_WIDTH, Values.BUTTON_HEIGHT);
            button.setLayoutY((Values.BUTTON_HEIGHT + 20) * i);
            button.getStyleClass().clear();
            button.setOnAction(actionEvent -> action(button.getText()));
            mainPane.getChildren().add(button);
        }
    }

    private void action(String buttonTitle) {
        switch (buttonTitle) {
            case Values.FIRST_EXAMPLE_TITLE:
                firstExample();
                break;
            case Values.FIRST_EXAMPLE_WITH_PROGRESS_BAR_TITLE:
                firstExampleWithProgressBar();
                break;
            case Values.SECOND_EXAMPLE_TITLE:
                secondExample();
                break;
            case Values.THIRD_EXAMPLE_TITLE:
                thirdExample();
                break;
            default:
                fourthExample();
        }
    }

    private Circle createCircle() {
        Circle circle = new Circle();
        circle.setRadius(Values.SHAPE_RADIUS / 2.0);
        circle.setLayoutX(Values.WIDTH / 2.0);
        circle.setLayoutY(Values.SHAPE_RADIUS / 2.0);
        mainPane.getChildren().add(circle);
        return circle;
    }

    private void firstExample() {
        Circle circle = createCircle();
        Task<Double> task = new Task<>() {
            @Override
            protected Double call() {
                for (double i = Values.SHAPE_RADIUS / 2.0; i < Values.HEIGHT - Values.SHAPE_RADIUS / 2.0; i++) {
                    updateValue(i);
                    H.sleep(2);
                }
                return null;
            }
        };

        task.valueProperty().addListener((observableValue, aDouble, value) -> {
            if (value != null) {
                circle.setLayoutY(value);
            }
        });
        new Thread(task).start();
    }

    private void firstExampleWithProgressBar() {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefSize(Values.BUTTON_WIDTH * 4.0, Values.BUTTON_HEIGHT);
        progressBar.setLayoutX(mainPane.getWidth() / 2.0 - progressBar.getPrefWidth() / 2);
        progressBar.setLayoutY(mainPane.getHeight() / 2 - progressBar.getPrefHeight() / 2);
        mainPane.getChildren().add(progressBar);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int i = 0; i < 1_000; i++) {
                    updateProgress(i, 1_000);
                    if (i == 500) {
                        H.sleep(2000);
                    }
                    H.sleep(4);
                }
                return null;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> mainPane.getChildren().remove(progressBar));
        new Thread(task).start();
    }

    private void secondExample() {
        Circle circle = createCircle();
        Task<Double> task = new Task<>() {
            @Override
            protected Double call() {
                for (double i = Values.SHAPE_RADIUS / 2.0; i < Values.HEIGHT - Values.SHAPE_RADIUS / 2.0; i++) {
                    double position = i;
                    Platform.runLater(() -> circle.setLayoutY(position));
                    H.sleep(2);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void thirdExample() {
        Circle circle = createCircle();
        new Thread(() -> {
            for (double i = Values.SHAPE_RADIUS / 2.0; i < Values.HEIGHT - Values.SHAPE_RADIUS / 2.0; i++) {
                double position = i;
                Platform.runLater(() -> circle.setLayoutY(position));
                H.sleep(2);
            }
        }).start();
    }

    private void fourthExample() {
        Circle circle = createCircle();

        TranslateTransition transition = new TranslateTransition();
        transition.setDelay(Duration.millis(500)); // zpoždění, za jak dlouho animace začne
        transition.setDuration(Duration.millis(2000));
        transition.setNode(circle);
        transition.setToX(0);
        transition.setToY(Values.HEIGHT - (double) Values.SHAPE_RADIUS);
        transition.setCycleCount(1);
        transition.setAutoReverse(false);
        transition.play();
        transition.currentTimeProperty().addListener((observableValue, duration, t1) -> System.out.println(circle.getLayoutY() + ", " + circle.getTranslateY()));
        transition.setOnFinished(e -> {
            System.out.println("finished");
            System.out.println(circle.getLayoutY() + ", " + circle.getTranslateY());
        });
    }
}
