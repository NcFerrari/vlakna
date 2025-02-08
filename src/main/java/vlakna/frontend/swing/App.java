package vlakna.frontend.swing;

import vlakna.Values;
import vlakna.backend.BEManager;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class App {

    private final List<List<JButton>> buttonGroups = new ArrayList<>();
    private final BEManager beManager = BEManager.getInstance();
    private JFrame frame;
    private JPanel buttonPanel;
    private JPanel animationPanel;

    public App() {
        init();
        SwingManager.getInstance().setAppAndInitComponents(this);
    }

    public void addComponent(JComponent component) {
        animationPanel.add(component);
        animationPanel.revalidate();
        animationPanel.repaint();
    }

    public void removeComponent(JComponent component) {
        animationPanel.remove(component);
        animationPanel.revalidate();
        animationPanel.repaint();
    }

    public int getCenterX() {
        return animationPanel.getWidth() / 2;
    }

    public int getCenterY() {
        return animationPanel.getHeight() / 2;
    }

    public JPanel getAnimationPanel() {
        return animationPanel;
    }

    private void init() {
        frame = new JFrame();
        frame.setSize(Values.WIDTH, Values.HEIGHT);
        frame.setTitle(Values.SWING_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setBackground(Color.decode(Values.BUTTONS_BACKGROUND));
        buttonPanel.setPreferredSize(new Dimension(Values.BUTTON_WIDTH + 15, getHeightForButtons()));
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(Values.VERTICAL_SCROLL_PANE_SPEED);
        mainPanel.add(scrollPane, BorderLayout.WEST);

        animationPanel = new JPanel();
        animationPanel.setLayout(null);
        animationPanel.setBackground(Color.decode(Values.ANIMATION_BACKGROUND));
        mainPanel.add(animationPanel, BorderLayout.CENTER);

        addButtons();

        frame.setVisible(true);
    }

    private void addButtons() {
        for (String[] buttonTitles : beManager.getThreadButtonTitles()) {
            List<JButton> buttonList = new ArrayList<>();
            for (String title : buttonTitles) {
                buttonList.add(createButton(title));
            }
            buttonGroups.add(buttonList);
        }
        repaintButtons();
    }

    private JButton createButton(String title) {
        JButton button = new JButton(title);
        button.setSize(Values.BUTTON_WIDTH, Values.BUTTON_HEIGHT);
        button.addActionListener(evt -> beManager.useThread(title, SwingManager.getInstance()));
        return button;
    }

    private void repaintButtons() {
        int buttonCount = 0;
        for (int i = 0; i < buttonGroups.size(); i++) {
            for (JButton button : buttonGroups.get(i)) {
                button.setLocation(0, Values.BUTTON_HEIGHT * buttonCount++ + Values.BUTTON_PADDING * i);
                buttonPanel.add(button);
            }
        }
    }

    private int getHeightForButtons() {
        int buttonCounts = 0;
        int spaceCounts = beManager.getThreadButtonTitles().length - 1;
        for (String[] buttonTitles : beManager.getThreadButtonTitles()) {
            buttonCounts += buttonTitles.length;
        }
        return buttonCounts * Values.BUTTON_HEIGHT + spaceCounts * Values.BUTTON_PADDING;
    }

    public void exit() {
        frame.dispose();
    }
}
