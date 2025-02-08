package vlakna;

import vlakna.backend.H;

public class DruhyZpusobImplementace implements Runnable {

    private Thread vlakno;

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            H.soutp(i);
            H.sleep(1000);
        }
    }

    public void start() {
        if (vlakno == null || !vlakno.isAlive()) {
            vlakno = new Thread(this);
            vlakno.start();
        }
    }
}
