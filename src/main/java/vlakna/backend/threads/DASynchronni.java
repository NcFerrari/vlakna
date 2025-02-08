package vlakna.backend.threads;

import vlakna.Values;
import vlakna.backend.H;

import java.util.ArrayList;
import java.util.List;

public class DASynchronni extends AbstractThread {

    private final List<AbstractThread> threads = new ArrayList<>();
    private final AbstractThread movingBallThread = new AbstractThread() {
        @Override
        public void run() {
            throw new UnsupportedOperationException();
        }
    };

    private String choice;
    private String selected;
    private int sum;

    public void start(String choice) {
        this.choice = choice;
        super.start();
    }

    @Override
    public void run() {
        beManager.getFeManager().removeShape(movingBallThread);
        beManager.getFeManager().removeShape(this);
        if (choice.equals(selected)) {
            return;
        }
        stop();
        H.sleep(300);
        selected = choice;
        switch (choice) {
            case Values.THREAD_TYPE_ASYNCH_MORE_FALLING_BALLS:
                fillThreads(new int[]{0, Values.SHAPE_RADIUS, 2 * Values.SHAPE_RADIUS, 3 * Values.SHAPE_RADIUS, 4 * Values.SHAPE_RADIUS}, Style.FALL_DOWN, askDialog());
                break;
            case Values.THREAD_TYPE_ASYNCH_MORE_BALLS:
                fillThreads(new int[]{0, 72, 144, 216, 278}, Style.MOVE_AROUND);
                break;
            case Values.THREAD_TYPE_ASYNCH_MOVE_TO_TARGET:
                int width = Values.WIDTH - Values.BUTTON_WIDTH;
                int height = 4 * Values.HEIGHT / 5;
                int[] locations = new int[]{rnd.nextInt((width) - Values.SHAPE_RADIUS), height / 2 + rnd.nextInt((height / 2) - Values.SHAPE_RADIUS)};
                beManager.getFeManager().openShapeTransition(this);
                beManager.getFeManager().moveShape(this, locations);
                beManager.getFeManager().openShapeTransition(movingBallThread);
                beManager.getFeManager().moveShape(movingBallThread, 0, 0);
                H.sleep(10);
                fillThreads(locations, Style.MOVE_BALL);
                break;
            case Values.THREAD_TYPE_ASYNCH_TEXTS:
                sum = 0;
                beManager.getFeManager().showOutput();
                fillThreads(new int[]{1, 1, 1, 1, 1}, Style.PRINT_TEXT);
                break;
            case Values.THREAD_TYPE_ASYNCH_FALL:
                fillThreads(new int[]{0, Values.SHAPE_RADIUS}, Style.FALL_DOWN, askDialog());
                break;
            default:
                fillThreads(new int[]{0, 180}, Style.MOVE_AROUND);
        }
    }

    @Override
    public void stop() {
        super.stop();
        beManager.getFeManager().removeShape(this);
        beManager.getFeManager().removeShape(movingBallThread);
        threads.forEach(AbstractThread::stop);
        threads.clear();
    }

    private void fillThreads(int[] startingPositions, Style style) {
        fillThreads(startingPositions, style, 1);
    }

    private void fillThreads(int[] startingPositions, Style style, int outputDialog) {
        if (outputDialog == 2) {
            selected = null;
            return;
        }
        int i = -1;
        StringBuilder tab = new StringBuilder();
        for (int startingPosition : startingPositions) {
            int movingPositionArrayIndex = ++i;
            String tabulator = tab.toString();
            threads.add(new AbstractThread() {
                @Override
                public void run() {
                    switch (style) {
                        case MOVE_AROUND:
                            moveAround(this, startingPosition);
                            break;
                        case PRINT_TEXT:
                            beManager.getFeManager().moveShape(this, -Values.SHAPE_RADIUS, -Values.SHAPE_RADIUS);
                            addToSum(startingPosition);
                            break;
                        case FALL_DOWN:
                            fallDown(this, startingPosition, outputDialog, tabulator);
                            break;
                        default:
                            beManager.getFeManager().moveShape(this, -Values.SHAPE_RADIUS, -Values.SHAPE_RADIUS);
                            int[] movingPosition = new int[2];
                            movingPosition[movingPositionArrayIndex] = 1;
                            moveBall(movingPosition, startingPosition);
                    }
                    selected = null;
                }
            });
            tab.append("\t");
        }
        stopped = false;
        threads.forEach(AbstractThread::start);
    }

    private void moveBall(int[] movingPosition, int maxMove) {
        for (int i = 0; i < maxMove; i++) {
            beManager.getFeManager().moveShapeBy(movingBallThread, movingPosition[0], movingPosition[1]);
            H.sleep();
        }
    }

    private void addToSum(int value) {
        for (int i = 0; i < 1_000; i++) {
            aSynchronizationMethod(value);
            H.sleep(1);
        }
    }

    private void moveAround(AbstractThread thread, int startingPosition) {
        int i = startingPosition;
        while (!stopped) {
            if (!pause) {
                beManager.getFeManager().moveShape(thread, getNewPosition(i++));
            }
            H.sleep(1);
        }
    }

    private void fallDown(AbstractThread thread, int startingPosition, int output, String tab) {
        int i = 1;
        int move = 2;
        while (!stopped) {
            if (!pause) {
                beManager.getFeManager().moveShape(thread, startingPosition, i);
                if (output == 0) {
                    beManager.getFeManager().printText(tab + thread + "\n");
                }
                i += move;
                if (i >= beManager.getFeManager().getHeight() - Values.SHAPE_RADIUS || i <= 0) {
                    move = -move;
                }
            }
            H.sleep(1);
        }
    }

    private void aSynchronizationMethod(Integer count) {
        if (count != null && count > 0) {
            sum += count;
        }
        beManager.getFeManager().printText(sum + "\n");
    }
}
