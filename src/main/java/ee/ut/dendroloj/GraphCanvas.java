package ee.ut.dendroloj;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>EXPERIMENTAL API</b>
 * <p>
 * A helper class for drawing a graph.
 * Can be passed to {@link Dendrologist#drawGraph(GraphCanvas)} to render the drawn graph on screen.
 *
 * @param <V> type of graph vertices
 */
public final class GraphCanvas<V> {

    final List<Vertex<V>> vertices = new ArrayList<>();
    final List<Edge<V>> edges = new ArrayList<>();

    public void drawVertex(V vertex) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        drawVertex(vertex, vertex.toString(), null);
    }

    public void drawVertex(V vertex, String label) {
        drawVertex(vertex, label, null);
    }

    public void drawVertex(V vertex, String label, Color color) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        vertices.add(new Vertex<>(vertex, label, color));
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

        private Vertex(T vertex, String label, Color color) {
            this.vertex = vertex;
            this.label = label;
            this.color = color;
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
