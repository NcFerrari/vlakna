package vlakna.backend.threads;


import vlakna.backend.H;

public class CNekonecneSPauzou extends AbstractThread {

    @Override
    public void run() {
        stopped = false;
        int i = 0;
        while (!stopped) {
            if (!pause) {
                beManager.getFeManager().moveShape(this, getNewPosition(i++));
            }
            H.sleep();
        }
        beManager.getFeManager().removeShape(this);
    }
}
