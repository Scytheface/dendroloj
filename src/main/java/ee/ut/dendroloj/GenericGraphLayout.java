package ee.ut.dendroloj;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.IntFunction;

class GenericGraphLayout {

    private static final AtomicLong edgeIdCounter = new AtomicLong(0);

    public static <V, E> Graph assembleGraph(Iterable<V> vertices, Iterable<E> edges, Function<E, V> from, Function<E, V> to,
                                             Function<V, String> vertexLabel, Function<E, String> edgeLabel) {
        Graph graph = new MultiGraph("dendroloj");

        return graph;
    }

    public static <V, E> Graph assembleGraph(Iterable<V> vertices, Function<V, Iterable<E>> outgoingEdges, Function<E, V> to,
                                             Function<V, String> vertexLabel, Function<E, String> edgeLabel) {
        Graph graph = new MultiGraph("dendroloj");

        return graph;
    }

    public static Graph assembleGraph(int vertexCount, WeightProvider weights, IntFunction<String> vertexLabel) {
        Graph graph = new MultiGraph("dendroloj");
        for (int i = 0; i < vertexCount; i++) {
            Node node = graph.addNode(Integer.toHexString(i));
            node.setAttribute("label", vertexLabel.apply(i));
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
