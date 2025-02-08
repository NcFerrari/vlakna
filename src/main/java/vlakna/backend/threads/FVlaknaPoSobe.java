package vlakna.backend.threads;

import vlakna.Values;
import vlakna.backend.H;

import java.util.ArrayList;
import java.util.List;

public class FVlaknaPoSobe extends AbstractThread {

    private String choice;
    private boolean running;

    public void start(String choice) {
        this.choice = choice;
        super.start();
    }

    @Override
    public void run() {
        if (running) {
            return;
        }
        running = true;
        if (Values.THREAD_TYPE_JOINED.equals(choice)) {
            ovals();
        } else {
            falling();
        }
    }

    private void ovals() {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 360; i++) {
                beManager.getFeManager().moveShape(this, getNewPosition(i));
                H.sleep(2);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 360; i++) {
                beManager.getFeManager().moveShape(this, getNewPosition(-i));
                H.sleep(2);
            }
        });

        Thread thread3 = new Thread(() -> {
            for (int i = 0; i < 360; i++) {
                beManager.getFeManager().moveShape(this, getNewPosition(i));
                H.sleep(2);
            }
        });

        thread1.start();
        try {
            thread1.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        thread3.start();
        try {
            thread3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        beManager.getFeManager().removeShape(this);
        running = false;
    }

    private void falling() {
        beManager.getFeManager().removeShape(this);
        final List<Thread> threads = new ArrayList<>();
        final List<AbstractThread> abstractThreads = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            int xPosition = i * Values.SHAPE_RADIUS;

            AbstractThread aThread = createAbstractThread();
            beManager.getFeManager().moveShape(aThread, xPosition, 0);
            abstractThreads.add(aThread);

            threads.add(new Thread(() -> {
                for (int j = 0; j < beManager.getFeManager().getHeight() - Values.SHAPE_RADIUS; j += 3) {
                    beManager.getFeManager().moveShape(aThread, xPosition, j);
                    H.sleep(1);
                }
                beManager.getFeManager().moveShape(aThread, xPosition, beManager.getFeManager().getHeight() - Values.SHAPE_RADIUS);
            }));
        }

        threads.forEach(thread -> {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        abstractThreads.forEach(beManager.getFeManager()::removeShape);
        running = false;
    }

    private AbstractThread createAbstractThread() {
        AbstractThread abstractThread = new AbstractThread() {
            @Override
            public void run() {
                throw new UnsupportedOperationException();
            }
        };
        beManager.getFeManager().openShapeTransition(abstractThread);
        return abstractThread;
    }
}
