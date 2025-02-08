package vlakna;

import vlakna.backend.H;

public class TretiZpusobPromenna {

    private Thread vlakno;

    public TretiZpusobPromenna() {
        vlakno = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                H.soutp(i);
                H.sleep(1000);
            }
        });
        vlakno.start();
    }
}
