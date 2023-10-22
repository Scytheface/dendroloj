package ee.ut.dendroloj;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

class GenericGraphLayout {

    private static final AtomicLong edgeIdCounter = new AtomicLong(0);

    public static Layout autoLayout() {
        return new SpringBox(false);
    }

    public static <V, E> Graph assembleGraph(GraphCanvas<?> graphCanvas) {
        Graph graph = new MultiGraph("dendroloj");
        for (var vertex : graphCanvas.vertices.entrySet()) {
            Node node = graph.addNode(getNodeId(vertex.getKey()));
            node.setAttribute("label", vertex.getValue());
        }
        for (var edge : graphCanvas.edges) {
            Edge graphEdge = graph.addEdge(getNewEdgeId(), getNodeId(edge.from), getNodeId(edge.to), true);
            if (edge.label != null) graphEdge.setAttribute("label", edge.label);
        }
        return graph;
    }

    public static <V, E> Graph assembleGraph(Iterable<V> vertices, Function<V, Iterable<E>> outgoingEdges, Function<E, V> to,
                                             Function<V, String> vertexLabel, Function<E, String> edgeLabel) {
        Graph graph = new MultiGraph("dendroloj");
        for (V vertex : vertices) {
            String nodeId = getNodeId(vertex);
            Node node = graph.addNode(nodeId);
            if (vertexLabel != null) node.setAttribute("label", vertexLabel.apply(vertex));
        }
        for (V vertex : vertices) {
            String nodeId = getNodeId(vertex);
            for (E edge : outgoingEdges.apply(vertex)) {
                Edge graphEdge = graph.addEdge(getNewEdgeId(), nodeId, getNodeId(to.apply(edge)), true);
                if (edgeLabel != null) graphEdge.setAttribute("label", edgeLabel.apply(edge));
            }
        }
        return graph;
    }

    public static Graph assembleGraph(int vertexCount, WeightProvider weights, String[] labels) {
        Graph graph = new MultiGraph("dendroloj");
        for (int i = 0; i < vertexCount; i++) {
            Node node = graph.addNode(Integer.toHexString(i));
            node.setAttribute("label", labels == null ? i : labels[i]);
        }
        for (int from = 0; from < vertexCount; from++) {
            for (int to = 0; to < vertexCount; to++) {
                Number weight = weights.getWeight(from, to);
                if (weight != null && (from != to || weight.doubleValue() != 0.0)) {
                    Edge edge = graph.addEdge(getNewEdgeId(), Integer.toHexString(from), Integer.toHexString(to), true);
                    edge.setAttribute("label", weight.toString());
                }
            }
        }
        return graph;
    }

    @FunctionalInterface
    interface WeightProvider {
        Number getWeight(int from, int to);
    }

    private static String getNodeId(Object object) {
        if (object instanceof Number) {
            // For primitives use value
            return object.toString();
        } else {
            // For classes use reference identity
            // TODO: This value is actually not guaranteed to be unique. Figure out a way to guarantee uniqueness.
            return Integer.toHexString(System.identityHashCode(object));
        }
    }

    private static String getNewEdgeId() {
        return Long.toHexString(edgeIdCounter.getAndIncrement());
    }

}
