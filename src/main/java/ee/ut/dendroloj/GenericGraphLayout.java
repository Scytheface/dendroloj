package ee.ut.dendroloj;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

import java.util.concurrent.atomic.AtomicLong;

class GenericGraphLayout {

    public static Layout autoLayout() {
        return new SpringBox(false);
    }

    public static Graph assembleGraph(GraphCanvas<?> graphCanvas) {
        Graph graph = new MultiGraph("dendroloj");
        for (var vertex : graphCanvas.vertices) {
            Node node = graph.addNode(IdHelper.getNodeId(vertex.vertex));
            node.setAttribute("label", vertex.label);
            if (vertex.color != null) node.setAttribute("ui.color", vertex.color);
        }
        for (var edge : graphCanvas.edges) {
            Edge graphEdge = graph.addEdge(IdHelper.getNewEdgeId(), IdHelper.getNodeId(edge.from), IdHelper.getNodeId(edge.to), true);
            if (edge.label != null) graphEdge.setAttribute("label", edge.label);
            if (edge.color != null) graphEdge.setAttribute("ui.color", edge.color);
        }
        return graph;
    }

}
