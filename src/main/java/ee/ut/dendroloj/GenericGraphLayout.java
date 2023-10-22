package ee.ut.dendroloj;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

import java.util.concurrent.atomic.AtomicLong;

class GenericGraphLayout {

    private static final AtomicLong edgeIdCounter = new AtomicLong(0);

    public static Layout autoLayout() {
        return new SpringBox(false);
    }

    public static Graph assembleGraph(GraphCanvas<?> graphCanvas) {
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
