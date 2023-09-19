package ee.ut.dendroloj;

public class Dendrologist {

    // TODO: Add methods to show an arbitrary graphs and graphs constructed from user-supplied tree classes (A&A 'Tipp' and similar).

    private static boolean awake = false;

    // Startup settings
    private static double uiScale = 1.0;

    // Runtime settings
    protected static boolean showMethodNames = true;

    private Dendrologist() {
    }

    /**
     * Sets multiplier for size of UI elements (text, nodes, etc.).
     * <p>
     * Default: {@code 1.0}
     */
    public static void setUIScale(double uiScale) {
        Dendrologist.uiScale = uiScale;
    }

    /**
     * Sets whether to show method names in call tree or not.
     * <p>
     * Default: {@code true}
     */
    public static void setShowMethodNames(boolean showMethodNames) {
        Dendrologist.showMethodNames = showMethodNames;
    }

    /**
     * Wakes up dendrologist.
     */
    public static void wakeUp() {
        init();
    }

    private static void init() {
        if (awake) return;

        initTracing();
        initGraphics();

        awake = true;
    }

    private static void initTracing() {
        // DebuggerTracer.init();
        AgentTracer.init();
    }

    private static void initGraphics() {
        GraphGUI.init(uiScale);
    }
}

