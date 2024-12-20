package ee.ut.dendroloj;

import org.graphstream.graph.Graph;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class Dendrologist {

    static {
        // Initialize global Swing and GraphStream properties before anything else is called or initialized.
        // Doing this before everything else helps avoid issues when Swing objects are created in unexpected places.
        GraphGUI.initProperties();
    }

    private static boolean awake = false;

    // Startup settings
    private static double uiScale = 2.0;

    // Runtime settings
    static boolean showMethodNames = false;
    static boolean captureArgsDuringCall = true;
    static boolean captureArgsDuringReturn = true;

    private Dendrologist() {
    }

    /**
     * Sets multiplier for size of UI elements (text, nodes, etc.).
     * <p>
     * Default: {@code 2.0}
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
     * @param duringCall   capture argument values during call. Default: {@code true}
     * @param duringReturn capture argument values during return. Default: {@code true}
     */
    public static void setArgumentCapture(boolean duringCall, boolean duringReturn) {
        Dendrologist.captureArgsDuringCall = duringCall;
        Dendrologist.captureArgsDuringReturn = duringReturn;
    }

    /**
     * Wakes up dendrologist.
     */
    public static synchronized void wakeUp() {
        if (awake) return;

        if (!isHeadless()) {
            AgentTracer.init();
            GraphGUI.initCallTreeGUI(uiScale);
        }

        awake = true;
    }

    /**
     * Colors the node corresponding to the current method call in the given color.
     * <p>
     * Note: This should be called inside a {@link Grow @Grow} annotated method or a method called by a {@link Grow @Grow} annotated method.
     */
    public static void paint(Color color) {
        CallTreeLayout.setCurrentNodeColor(color);
    }

    /**
     * Draws a binary tree.
     * <p>
     * Note: Passing in graphs that are not trees is supported but not intended.
     * In such a case edges that violate the requirements of a tree will be highlighted in red.
     *
     * @param root  root node of the tree
     * @param label function that takes in a node and returns the label for that node
     * @param left  function that takes in a node and returns the left child of that node (or null if absent)
     * @param right function that takes in a node and returns the right child of that node (or null if absent)
     * @param <T>   type of nodes in the tree
     */
    public static <T> void drawBinaryTree(T root, Function<T, Object> label, Function<T, T> left, Function<T, T> right) {
        drawTree(root, label, n -> Arrays.asList(left.apply(n), right.apply(n)));
    }

    // TODO: Make `children` return T[] instead of List<T>, because we generally use T[]?
    /**
     * Draws a tree.
     * <p>
     * Note: Passing in graphs that are not trees is supported but not intended.
     * In such a case edges that violate the requirements of a tree will be highlighted in red.
     *
     * @param root     root node of the tree
     * @param label    function that takes in a node and returns the label for that node
     * @param children function that takes in a node and returns the children of that node
     *                 (children may include null values to indicate empty/missing branches)
     * @param <T>      type of nodes in the tree
     */
    public static <T> void drawTree(T root, Function<T, Object> label, Function<T, List<T>> children) {
        if (isHeadless()) {
            return;
        }

        if (root == null) {
            // TODO: Allow null root node and just show empty graph?
            throw new NullPointerException("Root node must not be null");
        }

        Graph graph = GenericTreeLayout.assembleGraph(root, label, children);
        GraphGUI.initGenericGUI(uiScale, graph, null);
    }

    /**
     * Draws a graph based on the provided graph canvas.
     *
     * @param graphCanvas canvas containing the graph to draw
     *                    (use {@link GraphCanvas} methods to draw the vertices and edges before passing the canvas to this method;
     *                    note that vertices and edges drawn to the canvas <i>after</i> calling this method will <i>not</i> appear in the rendered graph)
     */
    public static void drawGraph(GraphCanvas<?> graphCanvas) {
        if (isHeadless()) {
            return;
        }

        Graph graph = GenericGraphLayout.assembleGraph(graphCanvas);
        GraphGUI.initGenericGUI(uiScale, graph, GenericGraphLayout.autoLayout());
    }

    private static boolean isHeadless() {
        return GraphGUI.isHeadless();
    }

}

