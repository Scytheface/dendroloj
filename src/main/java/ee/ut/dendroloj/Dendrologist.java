package ee.ut.dendroloj;

public class Dendrologist {

    private static boolean awake = false;

    protected static boolean showMethodNames = true;

    private Dendrologist() {
    }

    /**
     * Wakes up dendrologist with default configuration.
     */
    public static void wakeUp() {
        init(1.0, true);
    }

    /**
     * Wakes up dendrologist with custom configuration.
     *
     * @param uiScale         sets scaling multiplier for UI elements (text and nodes). default: 1.0
     * @param showMethodNames if enabled shows method name for each method call. default: true
     */
    public static void wakeUp(double uiScale, boolean showMethodNames) {
        init(uiScale, showMethodNames);
    }

    private static void init(double uiScale, boolean showMethodNames) {
        if (awake) return;

        Dendrologist.showMethodNames = showMethodNames;

        initTracing();
        initGraphics(uiScale);

        awake = true;
    }

    private static void initTracing() {
        // DebuggerTracer.init();
        AgentTracer.init();
    }

    private static void initGraphics(double uiScale) {
        GraphGUI.init(uiScale);
    }
}

