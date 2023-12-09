package ee.ut.dendroloj;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

class GenericGraphLayout {

    public static Layout autoLayout() {
        return new SpringBox(false);
    }

    public static Graph assembleGraph(GraphCanvas<?> graphCanvas) {
        Graph graph = new MultiGraph("dendroloj");
        for (var vertex : graphCanvas.vertices) {
            Node node = graph.addNode(IdHelper.getNodeId(vertex.vertex));
            node.setAttribute("label", vertex.label);
            if (vertex.fixed) {
                node.setAttribute("layout._fixed");
                node.setAttribute("layout.frozen");
                node.setAttribute("xy", vertex.x, vertex.y);
            }
            if (vertex.color != null) node.setAttribute("ui.color", vertex.color);
        }
        for (var edge : graphCanvas.edges) {
            Node v1 = graph.getNode(IdHelper.getNodeId(edge.v1));
            Node v2 = graph.getNode(IdHelper.getNodeId(edge.v2));
            if (v1 == null && v2 == null) {
                throw new IllegalArgumentException("Attempt to render edge " + formatEdge(edge) + ", but vertices " + edge.v1 + " and " + edge.v2 + " were not found");
            }
            if (v1 == null) {
                throw new IllegalArgumentException("Attempt to render edge " + formatEdge(edge) + ", but vertex " + edge.v1 + " was not found");
            }
            if (v2 == null) {
                throw new IllegalArgumentException("Attempt to render edge " + formatEdge(edge) + ", but vertex " + edge.v2 + " was not found");
            }
            Edge graphEdge = graph.addEdge(IdHelper.getNewEdgeId(), v1, v2, edge.directed);
            if (edge.label != null) graphEdge.setAttribute("label", edge.label);
            if (edge.color != null) graphEdge.setAttribute("ui.color", edge.color);
            if (edge.collapsed) graphEdge.setAttribute("ui._collapse");
            if (edge.arrowOnly) graphEdge.setAttribute("ui.class", "arrowonly");
        }
        return graph;
    }

    private static String formatEdge(GraphCanvas.Edge<?> edge) {
        return edge.v1 + (edge.directed ? " -> " : " -- ") + edge.v2;
    }

}
