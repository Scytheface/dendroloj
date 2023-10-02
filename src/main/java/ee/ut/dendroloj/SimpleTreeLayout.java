package ee.ut.dendroloj;

import org.graphstream.graph.Node;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class SimpleTreeLayout<T> {

    private final Function<T, Node> getNode;
    private final Function<T, List<? extends T>> getChildren;
    private final Map<T, Void> visitedNodes;

    private double x = 0.0;

    public SimpleTreeLayout(Function<T, Node> getNode, Function<T, List<? extends T>> getChildren, boolean checkCycles) {
        this.getNode = getNode;
        this.getChildren = getChildren;
        this.visitedNodes = checkCycles ? new IdentityHashMap<>() : null;
    }

    public void layout(T root) {
        x += layoutRecursive(root, x, 0.0, 1.0).width;
    }

    private LayoutResult layoutRecursive(T node, double x, double y, double minWidth) {
        final List<? extends T> children = getChildren.apply(node);

        double width = 0.0, firstChildOffset = 0.0, lastChildOffset = 0.0;
        if (children.isEmpty()) {
            width = minWidth;
        } else {
            final boolean isShallow = children.stream().allMatch(child -> getChildren.apply(child).isEmpty());
            final double childrenMinWidth = isShallow ? Math.max(0.4, Math.min(1.0, 1.0 - (children.size() - 2) * 0.2)) : 1.0;

            final int leftReferenceNode = (children.size() - 1) / 2;
            final int rightReferenceNode = children.size() / 2;

            for (int i = 0; i < children.size(); i++) {
                if (visitedNodes != null) {
                    if (visitedNodes.containsKey(node)) {
                        continue;
                    }
                    visitedNodes.put(node, null);
                }

                LayoutResult result = layoutRecursive(children.get(i), x + width, y - 2.0, childrenMinWidth);
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
        getNode.apply(node).setAttribute("xy", x + offset, y);

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
