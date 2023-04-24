package ee.ut.dendroloj;


import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


class SimpleTreeLayout {

    protected static Graph graph = null;

    protected static void setGraph(Graph graph){
        SimpleTreeLayout.graph = graph;
        Viewer v = graph.display();
        v.disableAutoLayout();

    }
    private record nodeMetaWrapper(CallTreeNode node, List<nodeMetaWrapper> children, double reservedWidth) {
    }

    protected static void updateGraph(MetaTreeNode root) {
        root.childStream().map(c -> wrap((CallTreeNode) c)).forEach(meta -> updateGraph(meta, 0, 0, null));

    }

    private static nodeMetaWrapper wrap(CallTreeNode node) {

        AtomicReference<Double> sum = new AtomicReference<>(0d);
        List<nodeMetaWrapper> children = node.childStream().map(c -> {
            nodeMetaWrapper childWrapper = wrap(c);
            sum.set(sum.get() + childWrapper.reservedWidth);
            return childWrapper;
        }).toList();

        return new nodeMetaWrapper(node, children, Math.max(1d, sum.get()));

    }

    private static void updateGraph(nodeMetaWrapper meta, double x, double y, CallTreeNode parent){
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
            updateGraph(meta.children().get(i), leftBoundary + padding + (reserve * i), y-1, current);

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


