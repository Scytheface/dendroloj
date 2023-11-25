package ee.ut.dendroloj;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A helper class for drawing a graph.
 * Can be passed to {@link Dendrologist#drawGraph(GraphCanvas)} to render the drawn graph on screen.
 *
 * @param <V> type of graph vertices
 */
public final class GraphCanvas<V> {

    private final Set<String> drawnVertices = new HashSet<>();
    final List<Vertex<V>> vertices = new ArrayList<>();
    final List<Edge<V>> edges = new ArrayList<>();

    // TODO: Add overload that allows drawing a vertex with the vertex itself as a default label.
    // Note that this will make the order of parameters for drawFixedVertex a little awkward.
    // Also note that the default label being the vertex itself converted to a string
    // might be non-obvious for reference types (especially ones that don't override toString).

    // public void drawVertex(V vertex) {
    //    if (vertex == null) throw new NullPointerException("Vertex must not be null");
    //    drawVertex(vertex, vertex.toString(), null);
    // }

    /**
     * Draws a vertex with the given label.
     */
    public void drawVertex(V vertex, String label) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        addVertex(new Vertex<>(vertex, label, null));
    }

    /**
     * Draws a vertex with the given label.
     */
    public void drawVertex(V vertex, String label, Color color) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        addVertex(new Vertex<>(vertex, label, color));
    }

    // public void drawFixedVertex(V vertex, double x, double y) {
    //    if (vertex == null) throw new NullPointerException("Vertex must not be null");
    //    drawVertex(vertex, vertex.toString(), null);
    // }

    /**
     * Draws a vertex with the given label at the given coordinates.
     */
    public void drawFixedVertex(V vertex, String label, double x, double y) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        addVertex(new Vertex<>(vertex, label, null, x, y));
    }

    /**
     * Draws a vertex with the given label at the given coordinates.
     */
    public void drawFixedVertex(V vertex, String label, double x, double y, Color color) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        addVertex(new Vertex<>(vertex, label, color, x, y));
    }

    private void addVertex(Vertex<V> vertex) {
        String id = IdHelper.getNodeId(vertex.vertex);
        if (drawnVertices.contains(id)) {
            throw new IllegalArgumentException("Vertex " + vertex.vertex + " (" + vertex.label + ") has already been drawn");
        }
        drawnVertices.add(id);
        vertices.add(vertex);
    }

    /**
     * Draws an undirected edge from vertex v1 to vertex v2.
     */
    public void drawEdge(V v1, V v2) {
        drawEdge(v1, v2, null, null);
    }

    /**
     * Draws an undirected edge from vertex v1 to vertex v2.
     */
    public void drawEdge(V v1, V v2, String label) {
        drawEdge(v1, v2, label, null);
    }

    /**
     * Draws an undirected edge from vertex v1 to vertex v2.
     */
    public void drawEdge(V v1, V v2, String label, Color color) {
        if (v1 == null || v2 == null) throw new NullPointerException("Target vertices must not be null");
        edges.add(new Edge<>(false, v1, v2, label, color));
    }

    /**
     * Draws a directed edge from vertex v1 to vertex v2.
     */
    public void drawDirectedEdge(V v1, V v2) {
        drawDirectedEdge(v1, v2, null, null);
    }

    /**
     * Draws a directed edge from vertex v1 to vertex v2.
     */
    public void drawDirectedEdge(V v1, V v2, String label) {
        drawDirectedEdge(v1, v2, label, null);
    }

    /**
     * Draws a directed edge from vertex v1 to vertex v2.
     */
    public void drawDirectedEdge(V v1, V v2, String label, Color color) {
        if (v1 == null || v2 == null) throw new NullPointerException("Target vertices must not be null");
        edges.add(new Edge<>(true, v1, v2, label, color));
    }

    static final class Vertex<T> {
        public final T vertex;
        public final String label;
        public final Color color;
        public final boolean fixed;
        public final double x;
        public final double y;

        private Vertex(T vertex, String label, Color color) {
            this.vertex = vertex;
            this.label = label;
            this.color = color;
            this.fixed = false;
            this.x = 0.0;
            this.y = 0.0;
        }

        private Vertex(T vertex, String label, Color color, double x, double y) {
            this.vertex = vertex;
            this.label = label;
            this.color = color;
            this.fixed = true;
            this.x = x;
            this.y = y;
        }
    }

    static final class Edge<T> {
        public final boolean directed;
        public final T v1;
        public final T v2;
        public final String label;
        public final Color color;

        private Edge(boolean directed, T v1, T v2, String label, Color color) {
            this.directed = directed;
            this.v1 = v1;
            this.v2 = v2;
            this.label = label;
            this.color = color;
        }
    }

}
