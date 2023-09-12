package ee.ut.dendroloj;


import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


class SimpleTreeLayout {

    protected static Graph graph = null;

    public static void setGraph(Graph graph) {
        SimpleTreeLayout.graph = graph;
    }

    public static void updateGraph(MetaTreeNode root) {
        root.childStream()
                .map(c -> wrap((CallTreeNode) c))
                .forEach(meta -> updateGraph(meta, 0, 0, Math.max(1.0, meta.reservedWidth / 20), null));
    }

    private record NodeMetaWrapper(CallTreeNode node, List<NodeMetaWrapper> children, double reservedWidth) {
    }

    private static NodeMetaWrapper wrap(CallTreeNode node) {
        List<NodeMetaWrapper> children = node.childStream().map(SimpleTreeLayout::wrap).toList();
        double sum = children.stream().mapToDouble(c -> c.reservedWidth).sum();

        return new NodeMetaWrapper(node, children, Math.max(1d, sum));
    }

    private static void updateGraph(NodeMetaWrapper meta, double x, double y, double layerHeight, CallTreeNode parent) {
        double leftBoundary = x - meta.reservedWidth / 2;

        CallTreeNode current = meta.node;
        String nodeId = current.toString();
        Node node = graph.getNode(nodeId);
        if (node == null) {
            node = graph.addNode(nodeId);
            node.setAttribute("label", current.argumentString());
        }
        node.setAttribute("xy", x, y);

        double reserve = meta.reservedWidth / meta.children.size();
        double padding = reserve / 2;
        for (int i = 0; i < meta.children.size(); i++)
            updateGraph(meta.children().get(i), leftBoundary + padding + (reserve * i), y - layerHeight, layerHeight, current);

        if (parent != null) {
            String parentId = parent.toString();
            String toEdgeID = parentId + nodeId;
            String fromEdgeID = nodeId + parentId;
            Edge edgeTo = graph.getEdge(toEdgeID);
            if (edgeTo == null)
                edgeTo = graph.addEdge(toEdgeID, graph.getNode(parentId), node, true);

            if (current.hasReturned()) {
                edgeTo.setAttribute("ui.class", "returned");
                if (current.getThrown() != null) {
                    node.setAttribute("ui.class", "error");
                    return;
                }
                Edge edgeFrom = graph.getEdge(fromEdgeID);
                if (edgeFrom == null)
                    edgeFrom = graph.addEdge(fromEdgeID, node, graph.getNode(parentId), true);
                Object returnValue = current.getReturnValue();
                if (returnValue != null)
                    edgeFrom.setAttribute("label", returnValue.toString());
            }
        }
    }
}


