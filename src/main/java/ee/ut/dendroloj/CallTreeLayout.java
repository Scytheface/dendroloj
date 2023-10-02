package ee.ut.dendroloj;


import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


class CallTreeLayout {

    public static final MetaTreeNode root = new MetaTreeNode();
    private static TreeNode currentNode = CallTreeLayout.root;

    private static Graph graph = null;
    private static JSlider stepSlider = null;

    private static boolean internalSliderChange = false;

    private static final List<List<Element>> steps = new ArrayList<>();
    private static int activeStep = -1;

    public static synchronized void init(Graph graph, JSlider stepSlider) {
        if (CallTreeLayout.graph != null) {
            throw new IllegalStateException("Attempt to initialize CallTreeLayout more than once");
        }
        CallTreeLayout.graph = graph;
        CallTreeLayout.stepSlider = stepSlider;
        stepSlider.setEnabled(false);
        stepSlider.setMinimum(0);
        stepSlider.setMaximum(0);
        stepSlider.addChangeListener(event -> {
            if (!internalSliderChange) {
                setActiveStep(stepSlider.getValue());
            }
        });
    }

    public static void processCall(Method method, Object[] callArguments) {
        currentNode = currentNode.addChild(new CallTreeNode(method, callArguments));
        addStepAndUpdateGraph();
    }

    public static void processReturn(Object returnValue, Throwable throwable) {
        currentNode = currentNode.done(returnValue, throwable).getParent();
        addStepAndUpdateGraph();
    }

    public static void setCurrentNodeColor(Color color) {
        if (graph != null) {
            Node node = graph.getNode(currentNode.getId());
            if (node != null) {
                node.setAttribute("ui.color", color);
            }
        }
    }

    private static void addStepAndUpdateGraph() {
        // This method causes massive performance issues and extremely high memory usage for relatively small graphs.
        // TODO: Investigate why and fix it. (Running Katsed.fib(16) with Dendrologist enabled provides a reproduction of the issue.)

        boolean isLatestStepActive = activeStep == steps.size() - 1;

        List<Element> newElements = new ArrayList<>();
        SimpleTreeLayout<CallTreeNode> layout = new SimpleTreeLayout<>(n -> graph.getNode(n.getId()), TreeNode::getChildren, false);
        for (CallTreeNode node : root.getChildren()) {
            updateGraph(node, !isLatestStepActive, newElements, null);
            layout.layout(node);
        }
        steps.add(newElements);

        internalSliderChange = true;
        stepSlider.setEnabled(true);
        stepSlider.setMaximum(steps.size() - 1);
        if (isLatestStepActive) {
            activeStep = steps.size() - 1;
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

    private static void updateGraph(CallTreeNode current, boolean hideNewElements, List<Element> newElements, CallTreeNode parent) {
        // Currently mutable arguments and return values show the value they had when they were first added to the graph.
        // TODO: Show old values of mutable values when scrolling through history?

        String nodeId = current.getId();
        Node node = graph.getNode(nodeId);
        if (node == null) {
            node = graph.addNode(nodeId);
            if (Dendrologist.captureArgsDuringCall) {
                node.setAttribute("label", current.argumentString());
            }
            if (hideNewElements) {
                node.setAttribute("ui.hide");
            }
            newElements.add(node);
        }

        if (current.hasReturned() && !node.hasAttribute("_returned")) {
            node.setAttribute("_returned", true);

            if (current.getThrown() != null) {
                node.setAttribute("ui.class", "error");
            }

            if (Dendrologist.captureArgsDuringReturn) {
                String oldArgumentString = (String) node.getAttribute("label");
                String newArgumentString = current.argumentString();
                if (oldArgumentString == null || oldArgumentString.isEmpty()) {
                    node.setAttribute("label", newArgumentString);
                } else if (!newArgumentString.equals(oldArgumentString)) {
                    node.setAttribute("label", oldArgumentString + "\n" + newArgumentString);
                }
            }
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

        for (CallTreeNode child : current.getChildren()) {
            updateGraph(child, hideNewElements, newElements, current);
        }
    }


}


