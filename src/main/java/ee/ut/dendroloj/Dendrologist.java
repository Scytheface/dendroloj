package ee.ut.dendroloj;

import java.awt.*;

public class Dendrologist {

    // TODO: Add methods to show an arbitrary graphs and graphs constructed from user-supplied tree classes (A&A 'Tipp' and similar).

    private static boolean awake = false;

    // Startup settings
    private static double uiScale = 1.0;

    // Runtime settings
    protected static boolean showMethodNames = false;
    protected static boolean captureArgsDuringCall = true;
    protected static boolean captureArgsDuringReturn = true;

    private Dendrologist() {
    }

    /**
     * Sets multiplier for size of UI elements (text, nodes, etc.).
     * <p>
     * Default: {@code 1.0}
     */
    public static void setUIScale(double uiScale) {
        if (awake) {
            throw new IllegalStateException("Cannot set UI scale after dendrologist has woken up");
        }
        Dendrologist.uiScale = uiScale;
    }

    /**
     * Sets whether to show method names in call graph or not.
     * <p>
     * Default: {@code false}
     */
    public static void setShowMethodNames(boolean showMethodNames) {
        Dendrologist.showMethodNames = showMethodNames;
    }

    /**
     * Sets when to capture argument values.
     *
     * @param duringCall   Capture argument values during call. Default: {@code true}
     * @param duringReturn Capture argument values during return. Default: {@code true}
     */
    public static void setArgumentCapture(boolean duringCall, boolean duringReturn) {
        Dendrologist.captureArgsDuringCall = duringCall;
        Dendrologist.captureArgsDuringReturn = duringReturn;
    }

    /**
     * Wakes up dendrologist.
     */
    public static void wakeUp() {
        init();
    }

    /**
     * Colors the node corresponding to the current method call in the given color.
     * <p>
     * Note: This should be called inside a {@link Grow @Grow} annotated method or a method called by a {@link Grow @Grow} annotated method.
     */
    public static void paint(Color color) {
        CallTreeLayout.setCurrentNodeColor(color);
    }

    private static synchronized void init() {
        if (awake) return;

        if (!isHeadless()) {
            initTracing();
            initGraphics();
        } else {
            System.err.println("Dendrologist: Running in headless environment. Ignoring call to wakeUp().");
        }

        awake = true;
    }

    private static boolean isHeadless() {
        return GraphGUI.isHeadless();
    }

    private static void initTracing() {
        // DebuggerTracer.init();
        AgentTracer.init();
    }

    private static void initGraphics() {
        GraphGUI.init(uiScale);
    }

}

