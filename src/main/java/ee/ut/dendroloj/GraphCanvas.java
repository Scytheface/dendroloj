package ee.ut.dendroloj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>EXPERIMENTAL API</b>
 * <p>
 * A helper class for drawing a graph.
 * Can be passed to {@link Dendrologist#drawGraph(GraphCanvas)} to render the drawn graph on screen.
 *
 * @param <T> type of graph vertices
 */
public final class GraphCanvas<T> {

    final Map<T, String> vertices = new HashMap<>();
    final List<Edge<T>> edges = new ArrayList<>();

    public void drawVertex(T vertex, String label) {
        vertices.put(vertex, label);
    }

    public void drawEdge(T from, T to, String label) {
        edges.add(new Edge<>(from, to, label));
    }

    public void drawEdge(T from, T to) {
        edges.add(new Edge<>(from, to, null));
    }

    static final class Edge<T> {
        public final T from;
        public final T to;
        public final String label;

        Edge(T from, T to, String label) {
            this.from = from;
            this.to = to;
            this.label = label;
        }
    }

}
