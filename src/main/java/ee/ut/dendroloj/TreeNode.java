package ee.ut.dendroloj;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

class CallTreeNode extends TreeNode {
    private final String signature;
    private final Object[] callArguments;
    private final Parameter[] methodType;

    private boolean hasReturned = false;
    private Object returnValue = null;
    private Throwable thrown = null;

    public CallTreeNode(String signature, Object[] callArguments, Parameter[] methodType) {
        this.signature = signature;
        this.callArguments = callArguments;
        this.methodType = methodType;
    }

    public Object[] getCallArguments() {
        return callArguments;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public Throwable getThrown() {
        return thrown;
    }

    protected boolean hasReturned() {
        return hasReturned;
    }

    @Override
    public TreeNode done(Object returnValue, Throwable throwable) {
        this.returnValue = returnValue;
        this.thrown = throwable;
        hasReturned = true;
        return this;
    }

    @Override
    public Stream<CallTreeNode> childStream() {
        return super.childStream().map(c -> (CallTreeNode) c);
    }

    public String argumentString() {
        return (Dendrologist.showMethodNames ? signature : "") + '(' + valuesToString(callArguments) + ')';
    }

    public String returnValueString() {
        // TODO: Is there a way to distinguish a null return value from returning nothing (return type void).
        return returnValue == null ? "" : valuesToString(new Object[]{returnValue});
    }

    private static String valuesToString(Object[] values) {
        String formatted = Arrays.deepToString(values);
        return formatted.substring(1, formatted.length() - 1);
    }
}

class MetaTreeNode extends TreeNode {
    @Override
    public MetaTreeNode done(Object arg, Throwable throwable) {
        throw new UnsupportedOperationException(String.format("done() called on MetaTreeNode with argument: %s, throwable: %s", arg, throwable));
    }
}

abstract class TreeNode {
    private TreeNode parent = null;
    private final ArrayList<TreeNode> children = new ArrayList<>();

    public TreeNode addChild(TreeNode child) {
        children.add(child);
        child.parent = this;
        return child;
    }

    public TreeNode getParent() {
        return parent;
    }

    public Stream<? extends TreeNode> childStream() {
        return children.stream();
    }

    public int childCount() {
        return children.size();
    }

    public abstract TreeNode done(Object arg, Throwable throwable);
}
