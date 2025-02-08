package vlakna.frontend.fx;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import vlakna.Values;
import vlakna.backend.threads.AbstractThread;
import vlakna.frontend.FEManager;

import java.util.HashMap;
import java.util.Map;

public class FXManager implements FEManager {

    private static final Object SYNCH_LOCK = new Object();

    private static FXManager instance;

    private final Map<AbstractThread, FXShape> registredThreadsMap = new HashMap<>();

    private App app;
    private boolean canProceed = true;

    public static FXManager getInstance() {
        if (instance == null) {
            instance = new FXManager();
        }
        return instance;
    }

    private FXManager() {
    }

    public void setApp(App app) {
        if (this.app == null) {
            this.app = app;
        }
    }

    @Override

    public void showMessage(String string) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, string);
        alert.show();
    }

    @Override
    public void exitApplication() {
        app.exit();
    }

    @Override
    public void openShapeTransition(AbstractThread thread) {
        evidenceThread(thread);
    }

    @Override
    public void removeShape(AbstractThread thread) {
        if (registredThreadsMap.containsKey(thread)) {
            registredThreadsMap.get(thread).removeFromPane(app);
            registredThreadsMap.remove(thread);
        }
    }

    @Override
    public void clearOutput() {
        app.clearTextArea();
        app.switchOffOutput();
    }

    @Override
    public int askDialog(String outputDialogConfirm) {
        return 0;
    }

    @Override
    public void moveShape(AbstractThread thread, int[] newPosition) {
        moveShape(thread, newPosition[0], newPosition[1]);
    }

    @Override
    public void moveShape(AbstractThread thread, int x, int y) {
        Platform.runLater(() -> {
            if (registredThreadsMap.containsKey(thread)) {
                registredThreadsMap.get(thread).setLocation(x, y);
            }
        });
    }

    public void moveFXSynchronizationShape(AbstractThread thread, double x, double y) {
        synchronized (SYNCH_LOCK) {
            Platform.runLater(() -> {
                FXShape fxShape = registredThreadsMap.get(thread);
                if (registredThreadsMap.containsKey(thread)) {
                    registredThreadsMap.get(thread).setLocation(fxShape.getX() + x, fxShape.getY() + y);
                }
                synchronized (SYNCH_LOCK) {
                    SYNCH_LOCK.notify();
                    canProceed = true;
                }
            });
            while (!canProceed) {
                try {
                    SYNCH_LOCK.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            canProceed = false;
        }
    }

    /**
     * Tato metoda se používá pro asnchronizaci i synchronizaci. Problém ovšem je v tom, že pokud v JavaFX používáme
     * Platform.runLater() metodu, tak ta je vždy asynchronní (takže když před tím voláme synchronní metody, je to fuk,
     * protože když se to dostane do vlákna FX, tak to zase bude asynchronní).
     * Proto je potřeba synchronizovat i Platformní vlákno
     *
     * @param thread thread from list
     * @param x      x position
     * @param y      y position
     */
    @Override
    public void moveShapeBy(AbstractThread thread, int x, int y) {
        FXShape fxShape = registredThreadsMap.get(thread);
        moveShape(thread, fxShape.getX() + x, fxShape.getY() + y);
    }

    @Override
    public void fadeShape(AbstractThread thread, float fadeValue) {
        registredThreadsMap.get(thread).setAlpha(fadeValue);
    }

    @Override
    public void rotateShape(AbstractThread thread, double radians) {
        Platform.runLater(() -> registredThreadsMap.get(thread).setAngle(radians));
    }

    @Override
    public void fillShape(AbstractThread thread, int[] colorValues) {
        registredThreadsMap.get(thread).setColor(Color.rgb(colorValues[0], colorValues[1], colorValues[2]));
    }

    @Override
    public void strokeShape(AbstractThread thread, int[] colorValues) {
        registredThreadsMap.get(thread).setStrokeColor(Color.rgb(colorValues[0], colorValues[1], colorValues[2]));
    }

    @Override
    public void gradientShape(AbstractThread thread, int[] color, int[] color2) {
        registredThreadsMap.get(thread).setGradient(Color.rgb(color[0], color[1], color[2]), Color.rgb(color2[0], color2[1], color2[2]));
    }

    @Override
    public void transformShape(AbstractThread thread, double i) {
        registredThreadsMap.get(thread).transform(i);
    }

    @Override
    public void printText(String text) {
        app.appendToTextArea(text);
    }

    @Override
    public int getHeight() {
        return (int) app.getAnimationPane().getHeight();
    }

    @Override
    public int getCenterX() {
        return (int) app.getCenterX();
    }

    @Override
    public int getCenterY() {
        return (int) app.getCenterY();
    }

    @Override
    public void showOutput() {
        app.switchOnOutput();
    }

    private void evidenceThread(AbstractThread thread) {
        registredThreadsMap.computeIfAbsent(thread, abstractExample -> createShape());
        registredThreadsMap.get(thread).setLocation(app.getCenterX() - (double) Values.SHAPE_RADIUS / 2, app.getCenterY() - (double) Values.SHAPE_RADIUS / 2);
    }

    private FXShape createShape() {
        FXShape shape = new FXShape();
        shape.addToPane(app);
        return shape;
    }
}
