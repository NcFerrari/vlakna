package vlakna.backend.threads;

import vlakna.Values;
import vlakna.backend.BEManager;

import java.util.Random;

public abstract class AbstractThread implements Runnable {

    private static int count = 0;

    protected final BEManager beManager = BEManager.getInstance();

    protected final Random rnd = new Random();
    private final int id = count++;

    private Thread thread;
    protected boolean stopped;
    protected boolean pause;

    protected int getId() {
        return id;
    }

    public void start() {
        beManager.getFeManager().openShapeTransition(this);
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        stopped = true;
        pause = false;
        beManager.getFeManager().removeShape(this);
        beManager.getFeManager().clearOutput();
    }

    public void switchPause() {
        pause = !pause;
    }

    protected int[] getNewPosition(int angle) {
        int biggerRadius = Values.SHAPE_RADIUS * 2;
        int x = beManager.getFeManager().getCenterX() - biggerRadius / 4 +
                (int) (biggerRadius * Math.cos(Math.toRadians(angle)));
        int y = beManager.getFeManager().getCenterY() - biggerRadius / 4 +
                (int) (biggerRadius * Math.sin(Math.toRadians(angle)));
        return new int[]{x, y};
    }

    @Override
    public String toString() {
        return "" + id;
    }

    protected int askDialog() {
        int result = beManager.getFeManager().askDialog(Values.OUTPUT_DIALOG_CONFIRM);
        if (result == 0) {
            beManager.getFeManager().showOutput();
        }
        return result;
    }

    protected enum Style {
        MOVE_AROUND, FALL_DOWN, PRINT_TEXT, MOVE_BALL
    }
}
