package ee.ut.dendroloj;

import org.graphstream.graph.Graph;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class Dendrologist {

    private static boolean awake = false;

    // Startup settings
    private static double uiScale = 1.0;

    // Runtime settings
    static boolean showMethodNames = false;
    static boolean captureArgsDuringCall = true;
    static boolean captureArgsDuringReturn = true;

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

        if (isHeadless()) {
            System.err.println("Dendrologist: Running in headless environment. Ignoring call to wakeUp.");
        } else {
            // DebuggerTracer.init();
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
    public static <T> void drawBinaryTree(T root, Function<T, String> label, Function<T, T> left, Function<T, T> right) {
        drawTree(root, label, n -> Arrays.asList(left.apply(n), right.apply(n)));
    }

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
    public static <T> void drawTree(T root, Function<T, String> label, Function<T, List<T>> children) {
        if (isHeadless()) {
            System.err.println("Dendrologist: Running in headless environment. Ignoring call to drawTree.");
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
     * <b>EXPERIMENTAL API</b>
     * <p>
     * Draws the graph from the provided graph canvas.
     * Note that vertices and edges drawn to the canvas <i>after</i> calling this method will <i>not</i> appear in the rendered graph.
     */
    public static void drawGraph(GraphCanvas<?> graphCanvas) {
        if (isHeadless()) {
            System.err.println("Dendrologist: Running in headless environment. Ignoring call to drawGraph.");
            return;
        }

        Graph graph = GenericGraphLayout.assembleGraph(graphCanvas);
        GraphGUI.initGenericGUI(uiScale, graph, GenericGraphLayout.autoLayout());
    }

    /**
     * <b>EXPERIMENTAL API</b>
     * <p>
     * Draws a graph based on the provided adjacency matrix.
     * Negative values in the adjacency matrix are treated as missing edges.
     *
     * @param adjacencyMatrix graph adjacency matrix; value at [i][j] is treated as the weight of the edge from vertex i to vertex j
     * @param labels          string labels for vertices; pass null to use vertex indices as labels
     */
    public static void drawGraph(int[][] adjacencyMatrix, String[] labels) {
        if (isHeadless()) {
            System.err.println("Dendrologist: Running in headless environment. Ignoring call to drawGraph.");
            return;
        }
        for (var row : adjacencyMatrix) {
            if (row.length != adjacencyMatrix.length) {
                throw new IllegalArgumentException("Adjacency matrix is not square");
            }
        }

        drawAdjacencyMatrixGraph(adjacencyMatrix.length, (from, to) -> {
            int value = adjacencyMatrix[from][to];
            return value >= 0 ? value : null;
        }, labels);
    }

    /**
     * Draws a graph based on the provided adjacency matrix.
     * Infinite values and NaN in the adjacency matrix are treated as missing edges.
     * <p>
     * <i>EXPERIMENTAL API</i>
     */
    public static void drawGraph(double[][] adjacencyMatrix, String[] labels) {
        if (isHeadless()) {
            System.err.println("Dendrologist: Running in headless environment. Ignoring call to drawGraph.");
            return;
        }
        for (var row : adjacencyMatrix) {
            if (row.length != adjacencyMatrix.length) {
                throw new IllegalArgumentException("Adjacency matrix is not square");
            }
        }

        drawAdjacencyMatrixGraph(adjacencyMatrix.length, (from, to) -> {
            double value = adjacencyMatrix[from][to];
            return Double.isFinite(value) ? value : null;
        }, labels);
    }

    /**
     * Draws a graph based on the provided adjacency matrix.
     * Infinite values and NaN in the adjacency matrix are treated as missing edges.
     * <p>
     * <i>EXPERIMENTAL API</i>
     */
    public static void drawGraph(float[][] adjacencyMatrix, String[] labels) {
        if (isHeadless()) {
            System.err.println("Dendrologist: Running in headless environment. Ignoring call to drawGraph.");
            return;
        }
        for (var row : adjacencyMatrix) {
            if (row.length != adjacencyMatrix.length) {
                throw new IllegalArgumentException("Adjacency matrix is not square");
            }
        }

        drawAdjacencyMatrixGraph(adjacencyMatrix.length, (from, to) -> {
            float value = adjacencyMatrix[from][to];
            return Float.isFinite(value) ? value : null;
        }, labels);
    }

    private static void drawAdjacencyMatrixGraph(int vertexCount, WeightProvider weights, String[] labels) {
        GraphCanvas<Number> graphCanvas = new GraphCanvas<>();
        for (int i = 0; i < vertexCount; i++) {
            graphCanvas.drawVertex(i, labels == null ? String.valueOf(i) : labels[i]);
            for (int j = 0; j < vertexCount; j++) {
                Number weight = weights.getWeight(i, j);
                if (weight != null && (i != j || weight.doubleValue() != 0.0)) {
                    graphCanvas.drawEdge(i, j, weight.toString());
                }
            }
        }
        Graph graph = GenericGraphLayout.assembleGraph(graphCanvas);
        GraphGUI.initGenericGUI(uiScale, graph, GenericGraphLayout.autoLayout());
    }

    @FunctionalInterface
    interface WeightProvider {
        Number getWeight(int from, int to);
    }

    private static boolean isHeadless() {
        return GraphGUI.isHeadless();
    }

}

