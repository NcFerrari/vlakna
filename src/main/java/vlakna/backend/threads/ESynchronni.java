package vlakna.backend.threads;

import vlakna.Values;
import vlakna.backend.H;
import vlakna.frontend.fx.FXManager;

import java.util.ArrayList;
import java.util.List;

public class ESynchronni extends AbstractThread {

    private static final Object AROUND_LOCK = new Object();
    private static final Object FALL_LOCK = new Object();
    private static final Object SUM_LOCK = new Object();
    private static final Object MOVE_BALL_LOCK = new Object();

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
    private boolean canProceed = true;

    public void start(String choice) {
        this.choice = choice;
        super.start();
    }

    @Override
    public void run() {
        beManager.getFeManager().removeShape(this);
        if (choice.equals(selected)) {
            return;
        }
        stop();
        selected = choice;
        switch (choice) {
            case Values.THREAD_TYPE_SYNCH_MORE_FALLING_BALLS:
                fillThreads(new int[]{0, Values.SHAPE_RADIUS, 2 * Values.SHAPE_RADIUS, 3 * Values.SHAPE_RADIUS, 4 * Values.SHAPE_RADIUS}, Style.FALL_DOWN, askDialog());
                break;
            case Values.THREAD_TYPE_SYNCH_MORE_BALLS:
                fillThreads(new int[]{0, 72, 144, 216, 278}, Style.MOVE_AROUND);
                break;
            case Values.THREAD_TYPE_SYNCH_MOVE_TO_TARGET:
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
            case Values.THREAD_TYPE_SYNCH_TEXTS:
                sum = 0;
                beManager.getFeManager().showOutput();
                fillThreads(new int[]{1, 1, 1, 1, 1}, Style.PRINT_TEXT);
                break;
            case Values.THREAD_TYPE_SYNCH_FALL:
                fillThreads(new int[]{0, Values.SHAPE_RADIUS}, Style.FALL_DOWN, askDialog());
                break;
            default:
                fillThreads(new int[]{0, 180}, Style.MOVE_AROUND);
        }
    }

    @Override
    public void stop() {
        super.stop();
        synchronized (AROUND_LOCK) {
            AROUND_LOCK.notifyAll();
        }
        H.sleep(100);
        selected = null;
        beManager.getFeManager().removeShape(this);
        beManager.getFeManager().removeShape(movingBallThread);
        threads.forEach(AbstractThread::stop);
        threads.clear();
    }

    private void fillThreads(int[] positions, Style style) {
        fillThreads(positions, style, 1);
    }

    private void fillThreads(int[] positions, Style style, int outputDialog) {
        if (outputDialog == 2) {
            selected = null;
            return;
        }
        int i = -1;
        StringBuilder tab = new StringBuilder();
        for (int position : positions) {
            int movingPositionArrayIndex = ++i;
            String tabulator = tab.toString();
            threads.add(new AbstractThread() {
                @Override
                public void run() {
                    switch (style) {
                        case PRINT_TEXT:
                            beManager.getFeManager().moveShape(this, -Values.SHAPE_RADIUS, -Values.SHAPE_RADIUS);
                            addToSum(position);
                            break;
                        case FALL_DOWN:
                            fallDown(this, position, outputDialog, tabulator);
                            break;
                        case MOVE_AROUND:
                            moveAround(this, position);
                            break;
                        default:
                            beManager.getFeManager().moveShape(this, -Values.SHAPE_RADIUS, -Values.SHAPE_RADIUS);
                            int[] movingPosition = new int[2];
                            movingPosition[movingPositionArrayIndex] = 1;
                            moveBall(movingPosition, position);
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
        int i = 0;
        while (i < maxMove) {
            synchronized (MOVE_BALL_LOCK) {
                if (beManager.getFeManager() instanceof FXManager fxManager) {
                    fxManager.moveFXSynchronizationShape(movingBallThread, movingPosition[0], movingPosition[1]);
                } else {
                    beManager.getFeManager().moveShapeBy(movingBallThread, movingPosition[0], movingPosition[1]);
                }
                i++;
                while (!canProceed) {
                    try {
                        MOVE_BALL_LOCK.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                canProceed = false;
                MOVE_BALL_LOCK.notify();
                canProceed = true;
            }
            H.sleep();
        }
        H.sleep(2000);
    }

    /**
     * Proč potřebujeme podmínku canProceed -> Spurious Wakeups (Neopodstatněné probuzení):
     * <p>
     * Vlákna se mohou probudit z wait() bez volání notify() nebo notifyAll(). To se nazývá "spurious wakeup".
     * Bez podmínky by vlákno mohlo pokračovat ve své činnosti, i když by nemělo, což vede k nekonzistentním výsledkům.
     * <p>
     * Pokud není použita podmínka, může se stát, že vlákno po probuzení ihned opět zavolá wait(), a jiné vlákno se
     * nikdy nedostane ke slovu. To může vést k tomu, že některé iterace nejsou provedeny správně.
     */
    private void addToSum(int value) {
        for (int i = 0; i < 1_000; i++) {
            synchronized (SUM_LOCK) {
                while (!canProceed) {
                    try {
                        SUM_LOCK.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                canProceed = false;
                synchronizationMethod(value);
            }
            H.sleep(1);
        }
    }

    private void moveAround(AbstractThread thread, int position) {
        int i = position;
        while (!stopped) {
            if (!pause) {
                synchronized (AROUND_LOCK) {
                    beManager.getFeManager().moveShape(thread, getNewPosition(i));
                    i += 2;
                    AROUND_LOCK.notify();
                    try {
                        AROUND_LOCK.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            H.sleep(1);
        }
    }

    private void fallDown(AbstractThread thread, int startingPosition, int output, String tab) {
        int i = 1;
        int move = 4;
        while (!stopped) {
            if (!pause) {
                synchronized (FALL_LOCK) {
                    beManager.getFeManager().moveShape(thread, startingPosition, i);
                    output(output, tab, thread);
                    i += move;
                    if (i >= beManager.getFeManager().getHeight() - Values.SHAPE_RADIUS || i <= 0) {
                        move = -move;
                    }
                    FALL_LOCK.notify();
                    try {
                        FALL_LOCK.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            H.sleep(1);
        }
    }

    private void output(int output, String tab, AbstractThread thread) {
        if (output == 0) {
            beManager.getFeManager().printText(tab + thread + "\n");
        }
    }

    private void synchronizationMethod(Integer count) {
        synchronized (SUM_LOCK) {
            if (count != null && count > 0) {
                sum += count;
            }
            beManager.getFeManager().printText(sum + "\n");
            canProceed = true;
            SUM_LOCK.notify();
        }
    }
}