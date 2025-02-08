package vlakna;

import vlakna.frontend.swing.App;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Dimension;

public class Manager {

    private static final String[] BUTTONS = {Values.BUTTON_CHOOSER_SWING, Values.BUTTON_CHOOSER_FX, Values.BUTTON_CHOOSER_BOTH, Values.BUTTON_CHOOSER_TRUE_FX};
    private static JPanel panel;

    public static void main(String[] args) {
        JDialog dialog = new JDialog();
        dialog.setTitle(Values.BUTTON_CHOOSER_GUI_CHOOSER);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(Values.GUI_CHOOSER_WIDTH, Values.GUI_CHOOSER_HEIGHT);
        dialog.setLocationRelativeTo(null);

        panel = new JPanel();
        dialog.getContentPane().add(panel);
        panel.setLayout(null);

        for (String title : BUTTONS) {
            createButton(title, dialog, panel);
        }
        dialog.setVisible(true);
    }

    private static void createButton(String title, JDialog dialog, JPanel panel) {
        JButton button = new JButton(title);
        button.setSize(new Dimension(Values.GUI_CHOOSER_WIDTH, (Values.GUI_CHOOSER_HEIGHT - 36) / BUTTONS.length));
        button.setLocation(0, panel.getComponentCount() * button.getHeight());
        button.addActionListener(evt -> {
            dialog.dispose();
            switch (title) {
                case Values.BUTTON_CHOOSER_BOTH:
                    new Thread(App::new).start();
                    new Thread(() -> javafx.application.Application.launch(vlakna.frontend.fx.App.class)).start();
                    break;
                case Values.BUTTON_CHOOSER_FX:
                    javafx.application.Application.launch(vlakna.frontend.fx.App.class);
                    break;
                case Values.BUTTON_CHOOSER_TRUE_FX:
                    javafx.application.Application.launch(vlakna.frontend.truefx.App.class);
                    break;
                default:
                    new App();
            }
        });
        panel.add(button);
    }
}
