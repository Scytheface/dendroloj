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
        double x = 0.0;
        for (CallTreeNode node : root.getChildren()) {
            x += updateGraph(node, !isLatestStepActive, newElements, x, 0.0, 1.0, null).width + 1.0;
        }
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

    private static LayoutResult updateGraph(CallTreeNode current, boolean hideNewElements, List<Element> newElements,
                                            double x, double y, double minWidth, CallTreeNode parent) {
        // Currently mutable arguments and return values show the value they had when they were first added to the graph.
        // TODO: Show old values of mutable values when scrolling through history?

        String nodeId = current.getId();
        Node node = graph.getNode(nodeId);
        if (node == null) {
            node = graph.addNode(nodeId);
            node.setAttribute("label", current.argumentString());
            if (hideNewElements) {
                node.setAttribute("ui.hide");
            }
            newElements.add(node);
        }

        if (current.hasReturned() && current.getThrown() != null) {
            node.setAttribute("ui.class", "error");
        }

        if (parent != null) {
            String parentId = parent.getId();
            String toEdgeId = parentId + nodeId;
            String fromEdgeId = nodeId + parentId;

            Edge edgeTo = graph.getEdge(toEdgeId);
            if (edgeTo == null) {
                edgeTo = graph.addEdge(toEdgeId, graph.getNode(parentId), node, true);
                if (hideNewElements) {
                    edgeTo.setAttribute("ui.hide");
                }
                newElements.add(edgeTo);
            }

            if (current.hasReturned()) {
                edgeTo.setAttribute("ui.class", "returned");
                if (current.getThrown() == null) {
                    Edge edgeFrom = graph.getEdge(fromEdgeId);
                    if (edgeFrom == null) {
                        edgeFrom = graph.addEdge(fromEdgeId, node, graph.getNode(parentId), true);
                        edgeFrom.setAttribute("label", current.returnValueString());
                        if (hideNewElements) {
                            edgeFrom.setAttribute("ui.hide");
                        }
                        newElements.add(edgeFrom);
                    }
                }
            }
        }

        double width = 0.0, firstChildOffset = 0.0, lastChildOffset = 0.0;
        final List<CallTreeNode> children = current.getChildren();
        if (children.isEmpty()) {
            width = minWidth;
        } else {
            final boolean isShallow = current.getChildren().stream().allMatch(child -> child.getChildren().isEmpty());
            final double childrenMinWidth = isShallow ? Math.max(0.4, Math.min(1.0, 1.0 - (current.getChildren().size() - 2) * 0.2)) : 1.0;

            final int leftReferenceNode = (children.size() - 1) / 2;
            final int rightReferenceNode = children.size() / 2;

            for (int i = 0; i < children.size(); i++) {
                LayoutResult result = updateGraph(children.get(i), hideNewElements, newElements, x + width, y - 2.0, childrenMinWidth, current);
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
        node.setAttribute("xy", x + offset, y);

        return new LayoutResult(width, offset);
    }

    private static class LayoutResult {
        public final double width;
        public final double offset;

        public LayoutResult(double width, double offset) {
            this.width = width;
            this.offset = offset;
        }
    }
}


