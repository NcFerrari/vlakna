package vlakna.frontend;

import vlakna.backend.threads.AbstractThread;

public interface FEManager {

    void showMessage(String string);

    void exitApplication();

    void openShapeTransition(AbstractThread abstractThread);

    void removeShape(AbstractThread abstractThread);

    void clearOutput();

    int askDialog(String outputDialogConfirm);

    void moveShape(AbstractThread abstractThread, int[] newPosition);

    void moveShape(AbstractThread abstractThread, int x, int y);

    void moveShapeBy(AbstractThread abstractThread, int x, int y);

    void fadeShape(AbstractThread abstractThread, float i);

    void rotateShape(AbstractThread abstractThread, double radians);

    void fillShape(AbstractThread abstractThread, int[] color);

    void strokeShape(AbstractThread abstractThread, int[] color);

    void gradientShape(AbstractThread abstractThread, int[] color, int[] color2);

    void transformShape(AbstractThread abstractThread, double i);

    void printText(String text);

    int getHeight();

    int getCenterX();

    int getCenterY();

    void showOutput();
}
