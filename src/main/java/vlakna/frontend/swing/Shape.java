package vlakna.frontend.swing;

import vlakna.Values;

import javax.swing.JComponent;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class Shape extends JComponent {

    private final Random rnd = new Random();
    private Color color;
    private float alpha = 1;
    private Double angle;
    private Color strokeColor;
    private transient GradientPaint gradient;
    private final int[] xPoints = new int[]{
            Values.SHAPE_RADIUS / 4,
            3 * Values.SHAPE_RADIUS / 4,
            3 * Values.SHAPE_RADIUS / 4,
            Values.SHAPE_RADIUS / 4};
    private final int[] yPoints = new int[]{
            Values.SHAPE_RADIUS / 4,
            Values.SHAPE_RADIUS / 4,
            3 * Values.SHAPE_RADIUS / 4,
            3 * Values.SHAPE_RADIUS / 4};
    private int[] currentX = xPoints;
    private int[] currentY = yPoints;
    private boolean transformed;

    public Shape() {
        this(Values.SHAPE_RADIUS);
    }

    public Shape(int radius) {
        super.setSize(radius, radius);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void addToFrame(App app) {
        color = new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        app.addComponent(this);
    }

    public void removeFromFrame(App app) {
        app.removeComponent(this);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public GradientPaint getGradient() {
        return gradient;
    }

    public void setGradient(GradientPaint gradient) {
        this.gradient = gradient;
    }

    public void transform(double progress) {
        transformed = true;
        currentX = new int[4];
        currentY = new int[4];

        int baseX1 = 0;                   // xPoints[3]
        int baseY1 = Values.SHAPE_RADIUS; // yPoints[3]
        int baseX2 = Values.SHAPE_RADIUS; // xPoints[2]
        int baseY2 = Values.SHAPE_RADIUS; // yPoints[2]

        int sideLength = Values.SHAPE_RADIUS;    // xPoints[1] - xPoints[0]
        int topX = (baseX1 + baseX2) / 2;
        int topY = baseY1 - (int) (Math.sqrt(3) * sideLength / 2);
        int maxTop = (int) ((1 - progress) * Values.SHAPE_RADIUS + progress * topY);

        currentX[0] = (int) ((1 - progress) * 0 + progress * baseX1);                   // (int) ((1 - progress) * xPoints[0] + progress * baseX1)
        currentY[0] = (int) ((1 - progress) * 0 + progress * baseY1);                   // (int) ((1 - progress) * yPoints[0] + progress * baseY1)

        currentX[1] = (int) ((1 - progress) * Values.SHAPE_RADIUS + progress * baseX2); // (int) ((1 - progress) * xPoints[1] + progress * baseX2)
        currentY[1] = (int) ((1 - progress) * 0 + progress * baseY2);                   // (int) ((1 - progress) * yPoints[1] + progress * baseY2)

        currentX[2] = (int) ((1 - progress) * Values.SHAPE_RADIUS + progress * topX);   // (int) ((1 - progress) * xPoints[2] + progress * topX)
        currentY[2] = maxTop;                                                           // (int) ((1 - progress) * yPoints[2] + progress * topY)

        currentX[3] = (int) ((1 - progress) * 0 + progress * topX);                     // (int) ((1 - progress) * xPoints[3] + progress * topX)
        currentY[3] = maxTop;                                                           // (int) ((1 - progress) * yPoints[3] + progress * topY)
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //SMOOTHING

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha())); //FADE EFFECT

        g2d.setColor(getColor());
        g2d.setPaint(getGradient());

        if (getAngle() != null) {
            AffineTransform transform = new AffineTransform(); //ROTATE EFFECT
            transform.rotate(getAngle(), getWidth() / 2.0, getHeight() / 2.0);
            g2d.setTransform(transform);
            g2d.fillPolygon(currentX, currentY, 4);
        } else if (transformed) {
            g2d.fillPolygon(currentX, currentY, 4);
        } else {
            g2d.fillOval(0, 0, getWidth(), getHeight());
            if (getStrokeColor() != null) {
                g2d.setColor(getStrokeColor());
                g2d.setStroke(new BasicStroke(Values.STROKE_WIDTH));
                g2d.drawOval(
                        (int) Values.STROKE_WIDTH / 2,
                        (int) Values.STROKE_WIDTH / 2,
                        getWidth() - (int) Values.STROKE_WIDTH,
                        getHeight() - (int) Values.STROKE_WIDTH);
            }
        }
    }
}