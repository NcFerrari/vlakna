package vlakna.backend;

import vlakna.Values;

public class H {

    private final static double[] XY_COORDINATION = new double[2];
    private static double x;
    private static double y;
    private static double distance;

    public static void setX(double x) {
        H.x = x;
    }

    public static void setY(double y) {
        H.y = y;
    }

    public static void setDistance(double distance) {
        H.distance = distance;
    }

    public static void sleep() {
        sleep(Values.DEFAULT_DELAY);
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exp) {
            Thread.currentThread().interrupt();
        }
    }

    public static void soutp(int number) {
        soutp("" + number);
    }

    public static void soutp(String text) {
        System.out.println(text);
    }

    public static double[] getRadiusBy(double x, double y, double distance, int angle) {
        double[] result = new double[2];
        result[0] = x - distance / 4 + (int) (distance * Math.cos(Math.toRadians(angle)));
        result[1] = y - distance / 4 + (int) (distance * Math.sin(Math.toRadians(angle)));
        return result;
    }

    public static double[] getRadiusByAngle(int angle) {
        XY_COORDINATION[0] = x - distance / 4 + (int) (distance * Math.cos(Math.toRadians(angle)));
        XY_COORDINATION[1] = y - distance / 4 + (int) (distance * Math.sin(Math.toRadians(angle)));
        return XY_COORDINATION;
    }

    private H() {

    }
}
