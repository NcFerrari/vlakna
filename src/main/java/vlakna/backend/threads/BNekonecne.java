package vlakna.backend.threads;


import vlakna.backend.H;

public class BNekonecne extends AbstractThread {

    @Override
    public void run() {
        stopped = false;
        for (int i = 0; !stopped; i++) {
            beManager.getFeManager().moveShape(this, getNewPosition(i));
            H.sleep();
        }
        beManager.getFeManager().removeShape(this);
    }
}
