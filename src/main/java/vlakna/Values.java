package vlakna;

public final class Values {

    // GUI parametry
    public static final String BUTTON_CHOOSER_GUI_CHOOSER = "Výběr GUI";
    public static final String BUTTON_CHOOSER_SWING = "Swing";
    public static final String BUTTON_CHOOSER_FX = "FX";
    public static final String BUTTON_CHOOSER_BOTH = "Obojí";
    public static final String BUTTON_CHOOSER_TRUE_FX = "Správné FX";
    public static final String BUTTON_CHOOSER_TRUE_SWING = "Správný Swing";
    public static final int GUI_CHOOSER_WIDTH = 200;
    public static final int GUI_CHOOSER_HEIGHT = 300;
    public static final int WIDTH = 1400;
    public static final int HEIGHT = 1000;
    public static final String SWING_TITLE = "Swing Threads";
    public static final String TRUE_FX_TITLE = "Správné použití FX";
    public static final String TRUE_FX_EXAMPLE_TITLE = "Správné použití FX (ukázka)";
    public static final String FX_TITLE = "FX Threads";
    public static final int BUTTON_WIDTH = 300;
    public static final int BUTTON_HEIGHT = 30;
    public static final String BUTTONS_BACKGROUND = "#1CCFBA";
    public static final String ANIMATION_BACKGROUND = "#FFFFFF";
    public static final int BUTTON_PADDING = 15;
    public static final int SHAPE_RADIUS = 150;
    public static final int TEXT_AREA_WIDTH = BUTTON_WIDTH;
    public static final int TEXT_AREA_HEIGHT = 400;
    public static final String TEXT_AREA_BACKGROUND = "#81F3B7";
    public static final String TEXT_AREA_FONT_FAMILY = "Arial";
    public static final int TEXT_AREA_SIZE = 16;
    public static final int TEXT_AREA_TAB_SIZE = 3;
    public static final String CLEAR_OUTPUT_BUTTON = "Promaž txt areu";
    public static final String HIDE_OUTPUT_BUTTON = "Skrýt výpis";
    public static final int DEFAULT_DELAY = 4;
    public static final int VERTICAL_SCROLL_PANE_SPEED = 16;
    public static final String BIG_SPACE = "        ";
    public static final int EFFECT_SPEED = 10;
    public static final double RATIO = 500;
    public static final float STROKE_WIDTH = 10;
    public static final String OUTPUT_DIALOG_CONFIRM = "Chcete zobrazit výpis?";
    public static final String CSS_FILE = "/fe/fx/css/style.css";
    public static final String FIRST_EXAMPLE_TITLE = "První ukázka";
    public static final String FIRST_EXAMPLE_WITH_PROGRESS_BAR_TITLE = "První ukázka (s progress barem)";
    public static final String SECOND_EXAMPLE_TITLE = "Druhá ukázka";
    public static final String THIRD_EXAMPLE_TITLE = "Třetí ukázka";
    public static final String FOURTH_EXAMPLE_TITLE = "Čtvrtá ukázka";
    public static final String START_PROCESS = "Spustit proces";
    public static final String READY_IN = "proces skončí za %d sekund";
    public static final String COMPLETED = "hotovo!";

    // Typy vláken
    public static final String THREAD_TYPE_SIMPLE = "Obyčejné vlákno";
    public static final String THREAD_TYPE_SIMPLE_FALL = "Obyčejné vlákno (fall)";
    public static final String THREAD_TYPE_SIMPLE_FADE = "Obyčejné vlákno (fade)";
    public static final String THREAD_TYPE_SIMPLE_ROTATE = "Obyčejné vlákno (rotate)";
    public static final String THREAD_TYPE_SIMPLE_FILL = "Obyčejné vlákno (fill)";
    public static final String THREAD_TYPE_SIMPLE_STROKE = "Obyčejné vlákno (stroke)";
    public static final String THREAD_TYPE_SIMPLE_GRADIENT = "Obyčejné vlákno (gradient)";
    public static final String THREAD_TYPE_SIMPLE_TRANSFORM = "Obyčejné vlákno (transform)";
    public static final String THREAD_TYPE_SIMPLE_SCALE = "Obyčejné vlákno (scale)";
    public static final String THREAD_TYPE_SIMPLE_TIMELINE = "Obyčejné vlákno (FX timeline)";
    public static final String THREAD_TYPE_SIMPLE_PROGRESS_BAR = "Obyčejné vlákno (Progress bar)";

    public static final String THREAD_TYPE_INFINITY = "Nekonečné vlákno";
    public static final String THREAD_TYPE_INFINITY_STOP = "Zastav nekonečné vlákno";

    public static final String THREAD_TYPE_INFINITY_WITH_PAUSE = "Nekonečné s pauzou";
    public static final String THREAD_TYPE_INFINITY_WITH_PAUSE_PAUSE = "Pauza";
    public static final String THREAD_TYPE_INFINITY_WITH_PAUSE_STOP = "Zastav vlákno s pauzou";

    public static final String THREAD_TYPE_ASYNCH = "Asynchronní vlákna (dvou kuliček)";
    public static final String THREAD_TYPE_ASYNCH_FALL = "Asynchronní vlákna (dvou padajících kuliček)";
    public static final String THREAD_TYPE_ASYNCH_TEXTS = "Asynchronní vlákna (textů)";
    public static final String THREAD_TYPE_ASYNCH_MOVE_TO_TARGET = "Asynchronní vlákna (posun kuličky)";
    public static final String THREAD_TYPE_ASYNCH_MORE_BALLS = "Asynchronní vlákna (více kuliček)";
    public static final String THREAD_TYPE_ASYNCH_MORE_FALLING_BALLS = "Asynchronní vlákna (více padajících kuliček)";
    public static final String THREAD_TYPE_ASYNCH_PAUSE = "Pauza asynchronnich vláken";
    public static final String THREAD_TYPE_ASYNCH_STOP = "Zastav asynchronní vlákna";

    public static final String THREAD_TYPE_SYNCH = "Synchronní vlákna(dvou kuliček)";
    public static final String THREAD_TYPE_SYNCH_FALL = "Synchronní vlákna (dvou padajících kuliček)";
    public static final String THREAD_TYPE_SYNCH_TEXTS = "Synchronní vlákna (textů)";
    public static final String THREAD_TYPE_SYNCH_MOVE_TO_TARGET = "Synchronní vlákna (posun kuličky)";
    public static final String THREAD_TYPE_SYNCH_MORE_BALLS = "Synchronní vlákna (více kuliček)";
    public static final String THREAD_TYPE_SYNCH_MORE_FALLING_BALLS = "Synchronní vlákna (více padajících kuliček)";
    public static final String THREAD_TYPE_SYNCH_PAUSE = "Pauza synchronnich vláken";
    public static final String THREAD_TYPE_SYNCH_STOP = "Zastav synchronní vlákna";

    public static final String THREAD_TYPE_COORDINATION = "Koordinovaná vlákna (více kuliček)";
    public static final String THREAD_TYPE_COORDINATION_MORE_FALLING_BALLS = "Koordinovaná vlákna (více padajících kuliček)";
    public static final String THREAD_TYPE_COORDINATION_PAUSE = "Pauza koordinovaných vláken";
    public static final String THREAD_TYPE_COORDINATION_STOP = "Zastav koordinovaná vlákna";

    public static final String THREAD_TYPE_JOINED = "Vlákna po sobě";
    public static final String THREAD_TYPE_JOINED_FALLING_BALLS = "Vlákna po sobě (padající kuličky)";

    public static final String THREAD_TYPE_NO_DAEMON = "Není daemon vlákno";
    public static final String THREAD_TYPE_DAEMON = "Daemon vlákno";

    public static final String THREAD_TYPE_SHOW_CURRENT_THREADS = "Zobraz aktuální vlákna";

    private Values() {
    }
}