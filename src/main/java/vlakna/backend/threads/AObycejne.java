package vlakna.backend.threads;

import vlakna.Values;
import vlakna.backend.H;

public class AObycejne extends AbstractThread {

    private String choice;

    public void start(String choice) {
        this.choice = choice;
        super.start();
    }

    @Override
    public void run() {
        switch (choice) {
            case Values.THREAD_TYPE_SIMPLE_FALL:
                fall();
                break;
            case Values.THREAD_TYPE_SIMPLE_FADE:
                fade();
                break;
            case Values.THREAD_TYPE_SIMPLE_ROTATE:
                rotate();
                break;
            case Values.THREAD_TYPE_SIMPLE_FILL:
                fill();
                break;
            case Values.THREAD_TYPE_SIMPLE_STROKE:
                stroke();
                break;
            case Values.THREAD_TYPE_SIMPLE_GRADIENT:
                gradient();
                break;
            case Values.THREAD_TYPE_SIMPLE_TRANSFORM:
                transform();
                break;
            default:
                around();
        }
        beManager.getFeManager().removeShape(this);
    }

    private void around() {
        for (int i = 0; i < 360; i++) {
            beManager.getFeManager().moveShape(this, getNewPosition(i));
            H.sleep();
        }
    }

    private void fall() {
        for (int i = 0; i < beManager.getFeManager().getHeight() - Values.SHAPE_RADIUS; i++) {
            beManager.getFeManager().moveShape(this, beManager.getFeManager().getCenterX() - Values.SHAPE_RADIUS / 2, i);
            H.sleep(1);
        }
    }

    private void fade() {
        for (float i = 0; i < 1; i += 0.01f) {
            beManager.getFeManager().fadeShape(this, i);
            H.sleep(Values.EFFECT_SPEED);
        }
        for (float i = 1; i >= 0; i -= 0.01f) {
            beManager.getFeManager().fadeShape(this, i);
            H.sleep(Values.EFFECT_SPEED);
        }
    }

    private void rotate() {
        for (int i = 0; i <= 360; i++) {
            beManager.getFeManager().rotateShape(this, Math.toRadians(i));
            H.sleep(Values.EFFECT_SPEED);
        }
    }

    private void fill() {
        applyEffect((color, color2) -> beManager.getFeManager().fillShape(this, color));
    }

    private void stroke() {
        applyEffect((color, color2) -> beManager.getFeManager().strokeShape(this, color));
    }

    private void gradient() {
        applyEffect((color, color2) -> beManager.getFeManager().gradientShape(this, color, color2));
    }

    private void transform() {
        for (double i = 0.0; i < 1.0; i += 0.02) {
            beManager.getFeManager().transformShape(this, i);
            H.sleep(30);
        }
        H.sleep(1000);
    }

    private void applyEffect(ModifiedConsumer<int[], int[]> action) {
        double[] color = generateNewColor();
        double[] secondColor = generateNewColor();
        double[] colorSteps = getSteps(color, secondColor);

        for (int i = 0; i < Values.RATIO; i++) {
            action.accept(modifiedColor(color, colorSteps, i), modifiedColor(secondColor, colorSteps, -i));
            H.sleep(Values.EFFECT_SPEED);
        }
        H.sleep(1000);
    }

    private double[] generateNewColor() {
        return new double[]{rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)};
    }

    private double[] getSteps(double[] color, double[] secondColor) {
        double[] colorSteps = new double[color.length];
        for (int i = 0; i < colorSteps.length; i++) {
            colorSteps[i] = (secondColor[i] - color[i]) / Values.RATIO;
        }
        return colorSteps;
    }

    private int[] modifiedColor(double[] color, double[] colorStepArray, int index) {
        int[] colorValues = new int[colorStepArray.length];
        for (int i = 0; i < colorValues.length; i++) {
            colorValues[i] = (int) (color[i] + colorStepArray[i] * index);
        }
        return colorValues;
    }

    private interface ModifiedConsumer<T, U> {
        void accept(T t, U u);
    }
}
