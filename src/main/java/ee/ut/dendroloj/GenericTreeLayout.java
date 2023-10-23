package ee.ut.dendroloj;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.List;
import java.util.function.Function;

class GenericTreeLayout {

    public static <T> Graph assembleGraph(T root, Function<T, String> getLabel, Function<T, List<T>> getChildren) {
        Graph graph = new MultiGraph("dendroloj");
        addToGraph(graph, root, null, 0.0, 0.0, getLabel, getChildren);
        return graph;
    }

    private static <T> LayoutResult addToGraph(Graph graph, T node, Node parent, double x, double y,
                                               Function<T, String> getLabel, Function<T, List<T>> getChildren) {
        if (node == null) {
            return new LayoutResult(1.0, 0.0);
        }

        String nodeId = IdHelper.getNodeId(node);
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
            graph.addEdge(IdHelper.getNewEdgeId(), parent, current, true);
            // if (visited) {
            //     edge.setAttribute("ui.class", "error");
            // }
        }
        if (visited) {
            current.enteringEdges().forEach(edge -> edge.setAttribute("ui.class", "error"));
            return new LayoutResult(1.0, 0.0);
        }

        double width = 0.0, firstChildOffset = 0.0, lastChildOffset = 0.0;
        final List<T> children = getChildren.apply(node);
        if (isEmpty(children)) {
            width = 1.0;
        } else {
            final int leftReferenceNode = (children.size() - 1) / 2;
            final int rightReferenceNode = children.size() / 2;

            int i = 0;
            for (T child : children) {
                LayoutResult result = addToGraph(graph, child, current, x + width, y - 2.0, getLabel, getChildren);
                if (i == leftReferenceNode) {
                    firstChildOffset = width + result.offset;
                }
                if (i == rightReferenceNode) {
                    lastChildOffset = width + result.offset;
                }
                width += result.width;
                i += 1;
            }
        }

        double offset = 0.5 * (firstChildOffset + lastChildOffset);
        current.setAttribute("xy", x + offset, y);

        return new LayoutResult(width, offset);
    }

    private static boolean isEmpty(List<?> array) {
        for (Object element : array) {
            if (element != null) {
                return false;
            }
        }
        return true;
    }

}
