package ee.ut.dendroloj;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

    public void drawVertex(T vertex, String label) {
        drawVertex(vertex, label, null);
    }

    public void drawVertex(T vertex, String label, Color color) {
        vertices.add(new Vertex<>(vertex, label, color));
    }

    public void drawEdge(T from, T to) {
        drawEdge(from, to, null, null);
    }

    public void drawEdge(T from, T to, String label) {
        drawEdge(from, to, label, null);
    }

    public void drawEdge(T from, T to, String label, Color color) {
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
