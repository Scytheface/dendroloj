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
 * @param <T> type of graph vertices
 */
public final class GraphCanvas<T> {

    final List<Vertex<T>> vertices = new ArrayList<>();
    final List<Edge<T>> edges = new ArrayList<>();

    public void drawVertex(T vertex) {
        drawVertex(vertex, vertex.toString(), null);
    }

    public void drawVertex(T vertex, String label) {
        drawVertex(vertex, label, null);
    }

    public void drawVertex(T vertex, String label, Color color) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        vertices.add(new Vertex<>(vertex, label, color));
    }

    public void drawEdge(T from, T to) {
        drawEdge(from, to, null, null);
    }

    public void drawEdge(T from, T to, String label) {
        drawEdge(from, to, label, null);
    }

    public void drawEdge(T from, T to, String label, Color color) {
        if (from == null || to == null) throw new NullPointerException("Target vertices must not be null");
        edges.add(new Edge<>(from, to, label, color));
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
        public final T from;
        public final T to;
        public final String label;
        public final Color color;

        private Edge(T from, T to, String label, Color color) {
            this.from = from;
            this.to = to;
            this.label = label;
            this.color = color;
        }
    }

}
