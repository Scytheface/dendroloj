package ee.ut.dendroloj;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

class GenericTreeLayout {

    private static final AtomicLong edgeIdCounter = new AtomicLong(0);

    public static <T> Graph assembleGraph(T root, Function<T, String> getLabel, Function<T, T[]> getChildren) {
        Graph graph = new SingleGraph("dendroloj");
        addToGraph(graph, root, null, 0.0, 0.0, getLabel, getChildren);
        return graph;
    }

    private static <T> LayoutResult addToGraph(Graph graph, T node, Node parent, double x, double y,
                                               Function<T, String> getLabel, Function<T, T[]> getChildren) {
        if (node == null) {
            return new LayoutResult(1.0, 0.0);
        }

        String nodeId = getNodeId(node);
        boolean visited;
        Node current = graph.getNode(nodeId);
        if (current == null) {
            visited = false;
            current = graph.addNode(nodeId);
            current.setAttribute("label", getLabel.apply(node));
        } else {
            visited = true;
        }
        if (parent != null) {
            graph.addEdge(getNewEdgeId(), parent, current, true);
        }
        if (visited) {
            return new LayoutResult(1.0, 0.0);
        }

        double width = 0.0, firstChildOffset = 0.0, lastChildOffset = 0.0;
        final T[] children = getChildren.apply(node);
        if (isEmpty(children)) {
            width = 1.0;
        } else {
            final int leftReferenceNode = (children.length - 1) / 2;
            final int rightReferenceNode = children.length / 2;

            for (int i = 0; i < children.length; i++) {
                LayoutResult result = addToGraph(graph, children[i], current, x + width, y - 2.0, getLabel, getChildren);
                if (i == leftReferenceNode) {
                    firstChildOffset = width + result.offset;
                }
                if (i == rightReferenceNode) {
                    lastChildOffset = width + result.offset;
                }
                width += result.width;
            }
        }

        double offset = 0.5 * (firstChildOffset + lastChildOffset);
        current.setAttribute("xy", x + offset, y);

        return new LayoutResult(width, offset);
    }

    private static boolean isEmpty(Object[] array) {
        for (Object element : array) {
            if (element != null) {
                return false;
            }
        }
        return true;
    }

    private static String getNodeId(Object object) {
        // TODO: This value is actually not guaranteed to be unique. Figure out a way to guarantee uniqueness.
        return Integer.toHexString(System.identityHashCode(object));
    }

    private static String getNewEdgeId() {
        return Long.toHexString(edgeIdCounter.getAndIncrement());
    }

}
