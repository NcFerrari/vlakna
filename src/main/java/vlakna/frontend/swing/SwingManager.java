package vlakna.frontend.swing;

import vlakna.Values;
import vlakna.backend.threads.AbstractThread;
import vlakna.frontend.FEManager;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.util.HashMap;
import java.util.Map;

public class SwingManager implements FEManager {

    private static SwingManager instance;

    private final Map<AbstractThread, Shape> registredThreadsMap = new HashMap<>();

    private App app;
    private JTextArea textArea;
    private JScrollPane textAreaScrollPane;
    private JButton textAreaClearButton;
    private JButton hideButton;

    public static SwingManager getInstance() {
        if (instance == null) {
            instance = new SwingManager();
        }
        return instance;
    }

    private SwingManager() {
    }

    @Override
    public void removeShape(AbstractThread thread) {
        if (registredThreadsMap.containsKey(thread)) {
            registredThreadsMap.get(thread).removeFromFrame(app);
            registredThreadsMap.remove(thread);
        }
    }

    public void setAppAndInitComponents(App app) {
        if (this.app == null) {
            this.app = app;
            initComponents();
        }
    }

    @Override
    public void openShapeTransition(AbstractThread thread) {
        evidenceThread(thread);
    }

    @Override
    public void moveShape(AbstractThread thread, int[] locations) {
        moveShape(thread, locations[0], locations[1]);
    }

    @Override
    public void moveShape(AbstractThread thread, int x, int y) {
        registredThreadsMap.get(thread).setLocation(x, y);
        registredThreadsMap.get(thread).repaint();
        app.getAnimationPanel().revalidate();
        app.getAnimationPanel().repaint();
    }

    @Override
    public void moveShapeBy(AbstractThread thread, int x, int y) {
        Shape shape = registredThreadsMap.get(thread);
        moveShape(thread, shape.getX() + x, shape.getY() + y);
    }

    @Override
    public void fadeShape(AbstractThread thread, float fadeValue) {
        registredThreadsMap.get(thread).setAlpha(fadeValue);
        registredThreadsMap.get(thread).repaint();
    }

    @Override
    public void rotateShape(AbstractThread thread, double radians) {
        registredThreadsMap.get(thread).setAngle(radians);
        registredThreadsMap.get(thread).repaint();
    }

    @Override
    public void fillShape(AbstractThread thread, int[] colorValues) {
        registredThreadsMap.get(thread).setColor(new Color(colorValues[0], colorValues[1], colorValues[2]));
        registredThreadsMap.get(thread).repaint();
    }

    @Override
    public void strokeShape(AbstractThread thread, int[] colorValues) {
        registredThreadsMap.get(thread).setStrokeColor(new Color(colorValues[0], colorValues[1], colorValues[2]));
        registredThreadsMap.get(thread).repaint();
    }

    @Override
    public void gradientShape(AbstractThread thread, int[] colorValues, int[] secondColorValues) {
        registredThreadsMap.get(thread).setGradient(
                new GradientPaint(0, Values.SHAPE_RADIUS / 2f, new Color(colorValues[0], colorValues[1], colorValues[2]),
                        Values.SHAPE_RADIUS, Values.SHAPE_RADIUS / 2f, new Color(secondColorValues[0], secondColorValues[1], secondColorValues[2])));
        registredThreadsMap.get(thread).repaint();
    }

    @Override
    public void transformShape(AbstractThread thread, double i) {
        registredThreadsMap.get(thread).transform(i);
        registredThreadsMap.get(thread).repaint();
    }

    @Override
    public void printText(String text) {
        textArea.append(text);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    @Override
    public int getHeight() {
        return app.getAnimationPanel().getHeight();
    }

    @Override
    public int getCenterX() {
        return app.getCenterX();
    }

    @Override
    public int getCenterY() {
        return app.getCenterY();
    }

    @Override
    public void clearOutput() {
        textArea.setText("");
        switchOffOutput();
    }

    @Override
    public void showOutput() {
        switchOnOutput();
    }

    private Shape createShape() {
        Shape shape = new Shape();
        shape.addToFrame(app);
        return shape;
    }

    private void initComponents() {
        createJTextArea();
        createRemoveAndHideButtons();
        switchOffOutput();
    }

    private void createJTextArea() {
        textArea = new JTextArea();
        textArea.setBackground(Color.decode(Values.TEXT_AREA_BACKGROUND));
        textArea.setFont(new Font(Values.TEXT_AREA_FONT_FAMILY, Font.PLAIN, Values.TEXT_AREA_SIZE));
        textArea.setTabSize(Values.TEXT_AREA_TAB_SIZE);
        textAreaScrollPane = new JScrollPane(textArea);
        textAreaScrollPane.setSize(Values.TEXT_AREA_WIDTH, Values.TEXT_AREA_HEIGHT);
        textAreaScrollPane.setLocation(app.getAnimationPanel().getWidth() - Values.BUTTON_WIDTH, 0);
        textAreaScrollPane.getVerticalScrollBar().setUnitIncrement(Values.VERTICAL_SCROLL_PANE_SPEED);
        app.addComponent(textAreaScrollPane);
    }

    private void createRemoveAndHideButtons() {
        textAreaClearButton = new JButton(Values.CLEAR_OUTPUT_BUTTON);
        textAreaClearButton.addActionListener(evt -> textArea.setText(""));
        textAreaClearButton.setSize(Values.BUTTON_WIDTH, Values.BUTTON_HEIGHT);
        textAreaClearButton.setLocation(textAreaScrollPane.getX(), textAreaScrollPane.getY() + textAreaScrollPane.getHeight());
        app.addComponent(textAreaClearButton);

        hideButton = new JButton(Values.HIDE_OUTPUT_BUTTON);
        hideButton.addActionListener(evt -> switchOffOutput());
        hideButton.setSize(Values.BUTTON_WIDTH, Values.BUTTON_HEIGHT);
        hideButton.setLocation(textAreaClearButton.getX(), textAreaClearButton.getY() + textAreaClearButton.getHeight());
        app.addComponent(hideButton);
    }

    private void switchingOutput(boolean onOff) {
        textAreaScrollPane.setVisible(onOff);
        textAreaClearButton.setVisible(onOff);
        hideButton.setVisible(onOff);
    }

    private void switchOnOutput() {
        switchingOutput(true);
    }

    private void switchOffOutput() {
        switchingOutput(false);
    }

    private void evidenceThread(AbstractThread thread) {
        registredThreadsMap.computeIfAbsent(thread, abstractExample -> createShape());
        registredThreadsMap.get(thread).setLocation(app.getCenterX() - Values.SHAPE_RADIUS / 2, app.getCenterY() - Values.SHAPE_RADIUS / 2);
    }

    @Override
    public int askDialog(String question) {
        return JOptionPane.showConfirmDialog(app.getAnimationPanel(), question);
    }

    @Override
    public void showMessage(String text) {
        JOptionPane.showMessageDialog(null, text);
    }

    @Override
    public void exitApplication() {
        app.exit();
    }
}
