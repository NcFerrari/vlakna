package vlakna.backend.threads;

import vlakna.backend.H;

/**
 * CONSOLE LOGGING ONLY
 */
public class HDaemon extends AbstractThread {

    private boolean daemon;
    private int count;

    public void start(boolean daemon, int count) {
        super.start();
        this.daemon = daemon;
        this.count = count;
    }

    @Override
    public void run() {
        beManager.getFeManager().removeShape(this);
        Thread thread1 = new Thread(() -> {
            for (int i = 1; i <= count; i++) {
                H.soutp((getId() % 2 == 0 ? "\t" : "") + i);
                H.sleep(1);
            }
        });
        thread1.setDaemon(daemon);
        thread1.start();
    }
}