package vlakna.frontend.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import vlakna.Values;
import vlakna.backend.BEManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class App extends Application {

    private static final int BUTTON_WIDTH = 4 * Values.BUTTON_WIDTH / 3;
    private final List<List<Button>> buttonGroups = new ArrayList<>();
    private final BEManager beManager = BEManager.getInstance();
    private Pane buttonPane;
    private Pane animationPane;
    private ScrollPane textAreaScrollPane;
    private TextArea textArea;
    private Button textAreaClearButton;
    private Button hideButton;
    private Stage stage;

    public void addNode(Node node) {
        Platform.runLater(() -> animationPane.getChildren().add(node));
    }

    public void removeNode(Node node) {
        Platform.runLater(() -> animationPane.getChildren().remove(node));
    }

    public double getCenterX() {
        return animationPane.getPrefWidth() / 2;
    }

    public double getCenterY() {
        return animationPane.getPrefHeight() / 2;
    }

    public Pane getAnimationPane() {
        return animationPane;
    }

    @Override
    public void start(Stage stage) throws Exception {
        ScrollPane buttonScrollPane;
        FXManager.getInstance().setApp(this);
        this.stage = stage;
        stage.setWidth(Values.WIDTH);
        stage.setHeight(Values.HEIGHT);
        stage.setTitle(Values.FX_TITLE);
        stage.setOnCloseRequest(e -> System.exit(0));

        Pane mainPane = new Pane();
        Scene scene = new Scene(mainPane);
        String css = Objects.requireNonNull(getClass().getResource(Values.CSS_FILE)).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);

        buttonPane = new Pane();
        buttonScrollPane = new ScrollPane(buttonPane);
        buttonScrollPane.setPrefSize(stage.getWidth(), stage.getHeight() / 5);
        buttonScrollPane.getStyleClass().clear();
        mainPane.getChildren().add(buttonScrollPane);

        animationPane = new Pane();
        animationPane.setPrefSize(stage.getWidth(), stage.getHeight() - buttonScrollPane.getPrefHeight());
        animationPane.setLayoutY(buttonScrollPane.getPrefHeight());
        mainPane.getChildren().addFirst(animationPane);

        addButtons();
        addTextAreaComponents();
        switchOffOutput();

        stage.widthProperty().addListener((o, n, t) -> {
            buttonScrollPane.setPrefWidth((double) t);
            animationPane.setPrefWidth((double) t);
            textAreaScrollPane.setLayoutX((double) t - Values.TEXT_AREA_WIDTH);
            textAreaClearButton.setLayoutX(textAreaScrollPane.getLayoutX());
            hideButton.setLayoutX(textAreaScrollPane.getLayoutX());
        });
        stage.heightProperty().addListener((o, n, t) -> {
            buttonScrollPane.setPrefHeight((double) t / 5);
            animationPane.setPrefHeight((double) t - buttonScrollPane.getPrefHeight());
            animationPane.setLayoutY(buttonScrollPane.getPrefHeight());
            textAreaClearButton.setLayoutY(Values.TEXT_AREA_HEIGHT);
            hideButton.setLayoutY(Values.TEXT_AREA_HEIGHT + (double) Values.BUTTON_HEIGHT + Values.BUTTON_PADDING);
        });

        stage.show();
        stage.setY(stage.getY() + stage.getHeight() - scene.getHeight());
    }

    private void addTextAreaComponents() {
        textArea = new TextArea();
        textAreaScrollPane = new ScrollPane(textArea);
        textAreaScrollPane.setPrefSize(Values.TEXT_AREA_WIDTH, Values.TEXT_AREA_HEIGHT);
        textAreaScrollPane.setLayoutX(Values.WIDTH - (double) Values.TEXT_AREA_WIDTH);
        textAreaScrollPane.getStyleClass().clear();

        textArea.setStyle("-fx-control-inner-background: " + Values.TEXT_AREA_BACKGROUND);
        textArea.setFont(new Font(Values.TEXT_AREA_FONT_FAMILY, Values.TEXT_AREA_SIZE));
        textArea.setPrefSize(Values.TEXT_AREA_WIDTH, Values.TEXT_AREA_HEIGHT);
        textArea.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().equals("\t")) {
                change.setText(" ".repeat(Values.TEXT_AREA_TAB_SIZE));
            }
            return change;
        }));

        textAreaClearButton = new Button(Values.CLEAR_OUTPUT_BUTTON);
        textAreaClearButton.setOnAction(evt -> textArea.clear());
        textAreaClearButton.setPrefSize(Values.BUTTON_WIDTH, Values.BUTTON_HEIGHT);
        textAreaClearButton.setLayoutX(textAreaScrollPane.getLayoutX());
        textAreaClearButton.setLayoutY(Values.TEXT_AREA_HEIGHT);

        hideButton = new Button(Values.HIDE_OUTPUT_BUTTON);
        hideButton.setOnAction(evt -> switchOffOutput());
        hideButton.setPrefSize(textAreaClearButton.getPrefWidth(), textAreaClearButton.getPrefHeight());
        hideButton.setLayoutX(textAreaScrollPane.getLayoutX());
        hideButton.setLayoutY(Values.TEXT_AREA_HEIGHT + (double) Values.BUTTON_HEIGHT + Values.BUTTON_PADDING);

        animationPane.getChildren().addAll(textAreaScrollPane, textAreaClearButton, hideButton);
    }

    private void addButtons() {
        for (String[] buttonTitles : beManager.getThreadButtonTitles()) {
            List<Button> buttonList = new ArrayList<>();
            for (String title : buttonTitles) {
                buttonList.add(createButton(title));
            }
            buttonGroups.add(buttonList);
        }
        repaintButtons();
    }

    private Button createButton(String title) {
        Button button = new Button(title);
        button.setPrefSize(BUTTON_WIDTH, Values.BUTTON_HEIGHT);
        button.setOnAction(evt -> beManager.useThread(title, FXManager.getInstance()));
        return button;
    }

    private void repaintButtons() {
        int maxButtonsInColumn = 0;
        int groupCount = 0;
        for (List<Button> buttonGroup : buttonGroups) {
            int buttonCount = 0;
            for (Button button : buttonGroup) {
                button.setLayoutX(BUTTON_WIDTH * (double) groupCount);
                button.setLayoutY(Values.BUTTON_HEIGHT * (double) buttonCount + Values.BUTTON_PADDING * (double) buttonCount++);
                buttonPane.getChildren().add(button);
            }
            if (buttonCount > maxButtonsInColumn) {
                maxButtonsInColumn = buttonCount;
            }
            groupCount++;
        }
        buttonPane.setPrefSize(groupCount * (double) BUTTON_WIDTH, maxButtonsInColumn * (double) Values.BUTTON_HEIGHT + maxButtonsInColumn * (double) Values.BUTTON_PADDING);
    }

    public void switchOnOutput() {
        switchingOutput(true);
    }

    public void switchOffOutput() {
        switchingOutput(false);
    }

    private void switchingOutput(boolean onOff) {
        setTextAreaComponentsVisibility(onOff);
    }

    public void setTextAreaComponentsVisibility(boolean onOff) {
        textAreaScrollPane.setVisible(onOff);
        textAreaClearButton.setVisible(onOff);
        hideButton.setVisible(onOff);
    }

    public void appendToTextArea(String text) {
        Platform.runLater(() -> {
            textArea.appendText(text);
            if (textArea.getText().length() > 1000) {
                textArea.setText(textArea.getText().substring(textArea.getText().length() - 100));
            }
        });
    }

    public void clearTextArea() {
        Platform.runLater(() -> textArea.clear());
    }

    public void exit() {
        stage.close();
    }
}
