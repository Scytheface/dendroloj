package ee.ut.dendroloj;


import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


class SimpleTreeLayout {

    public static final MetaTreeNode root = new MetaTreeNode();

    private static Graph graph = null;
    private static JSlider stepSlider = null;

    private static boolean internalSliderChange = false;

    private static final List<List<Element>> steps = new ArrayList<>();
    private static int activeStep = -1;

    public static void init(Graph graph, JSlider stepSlider) {
        SimpleTreeLayout.graph = graph;
        SimpleTreeLayout.stepSlider = stepSlider;
        stepSlider.setMinimum(0);
        stepSlider.setMaximum(0);
        stepSlider.addChangeListener(event -> {
            if (!internalSliderChange) {
                setActiveStep(stepSlider.getValue());
            }
        });
    }

    public static void addStepAndUpdateGraph() {
        // This method causes massive performance issues and extremely high memory usage for relatively small graphs.
        // TODO: Investigate why and fix it. (Running Katsed.fib(16) with Dendrologist enabled provides a reproduction of the issue.)

        boolean isLatestStepActive = activeStep == steps.size() - 1;
        if (isLatestStepActive) {
            activeStep += 1;
        }

        List<Element> newElements = new ArrayList<>();
        root.childStream()
                .map(c -> wrap((CallTreeNode) c))
                .forEach(meta -> updateGraph(meta, !isLatestStepActive, newElements,
                        0, 0, Math.max(1.0, meta.reservedWidth / 20), null));
        steps.add(newElements);

        internalSliderChange = true;
        stepSlider.setMaximum(steps.size() - 1);
        if (isLatestStepActive) {
            stepSlider.setValue(activeStep);
        }
        internalSliderChange = false;
    }

    private static void setActiveStep(int newActiveStep) {
        if (newActiveStep == activeStep) {
            return;
        }

        if (newActiveStep > activeStep) {
            for (int i = activeStep + 1; i <= newActiveStep; i++) {
                // Show elements
                for (Element element : steps.get(i)) {
                    element.removeAttribute("ui.hide");
                }
            }
        } else {
            for (int i = activeStep; i > newActiveStep; i--) {
                // Hide elements
                for (Element element : steps.get(i)) {
                    element.setAttribute("ui.hide");
                }
            }
        }

        activeStep = newActiveStep;
    }

    private record NodeMetaWrapper(CallTreeNode node, List<NodeMetaWrapper> children, double reservedWidth) {
    }

    private static NodeMetaWrapper wrap(CallTreeNode node) {
        List<NodeMetaWrapper> children = node.childStream().map(SimpleTreeLayout::wrap).toList();
        double sum = children.stream().mapToDouble(c -> c.reservedWidth).sum();

        return new NodeMetaWrapper(node, children, Math.max(1d, sum));
    }

    private static void updateGraph(NodeMetaWrapper meta, boolean hideNewElements, List<Element> newElements,
                                    double x, double y, double layerHeight, CallTreeNode parent) {
        // TODO: Fix issues with laying out multiple call graphs at the same time.

        // Currently mutable arguments and returns values show the value they had when they were first added to the graph.
        // TODO: Show old values of mutable values when scrolling through history?

        double leftBoundary = x - meta.reservedWidth / 2;

        CallTreeNode current = meta.node;
        String nodeId = current.toString();
        Node node = graph.getNode(nodeId);
        if (node == null) {
            node = graph.addNode(nodeId);
            node.setAttribute("label", current.argumentString());
            if (hideNewElements) {
                node.setAttribute("ui.hide");
            }
            newElements.add(node);
        }
        node.setAttribute("xy", x, y);

        double reserve = meta.reservedWidth / meta.children.size();
        double padding = reserve / 2;
        for (int i = 0; i < meta.children.size(); i++) {
            updateGraph(meta.children().get(i), hideNewElements, newElements,
                    leftBoundary + padding + (reserve * i), y - layerHeight, layerHeight, current);
        }

        if (current.hasReturned() && current.getThrown() != null) {
            node.setAttribute("ui.class", "error");
        }

        if (parent != null) {
            String parentId = parent.toString();
            String toEdgeID = parentId + nodeId;
            String fromEdgeID = nodeId + parentId;

            Edge edgeTo = graph.getEdge(toEdgeID);
            if (edgeTo == null) {
                edgeTo = graph.addEdge(toEdgeID, graph.getNode(parentId), node, true);
                if (hideNewElements) {
                    edgeTo.setAttribute("ui.hide");
                }
                newElements.add(edgeTo);
            }

            if (current.hasReturned()) {
                edgeTo.setAttribute("ui.class", "returned");
                if (current.getThrown() != null) {
                    return;
                }
                Edge edgeFrom = graph.getEdge(fromEdgeID);
                if (edgeFrom == null) {
                    edgeFrom = graph.addEdge(fromEdgeID, node, graph.getNode(parentId), true);
                    edgeFrom.setAttribute("label", current.returnValueString());
                    if (hideNewElements) {
                        edgeFrom.setAttribute("ui.hide");
                    }
                    newElements.add(edgeFrom);
                }
            }
        }
    }
}


