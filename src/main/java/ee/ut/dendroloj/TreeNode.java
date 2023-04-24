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

    public Object getReturnValue() {
        return returnValue;
    }

    private Object returnValue = null;

    public CallTreeNode(String signature, Object[] callArguments, Parameter[] methodType) {
        this.signature = signature;
        this.callArguments = callArguments;
        this.methodType = methodType;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public TreeNode done(Object returnValue, Throwable throwable) {
        setReturnValue(returnValue);
        setThrown(throwable);
        hasReturned = true;
        return this;
    }

    protected boolean hasReturned(){
        return hasReturned;
    }

    @Override
    public Stream<CallTreeNode> childStream(){
        return super.childStream().map(c -> (CallTreeNode) c);
    }

    public String argumentString(){
        return Arrays.deepToString(callArguments).replaceAll("\\[\\]", "");
    }
}

class MetaTreeNode extends TreeNode {
    @Override
    public MetaTreeNode done(Object arg, Throwable throwable) {
        setThrown(throwable);
        throw new UnsupportedOperationException(String.format("done() called on MetaTreeNode with argument: %s, throwable: %s", arg, throwable));
    }
}

abstract class TreeNode {
    private TreeNode parent = null;
    private ArrayList<TreeNode> children = new ArrayList<>();

    private Throwable thrown = null;

    public TreeNode addChild(TreeNode child) {
        children.add(child);
        child.parent = this;
        return child;
    }

    public TreeNode getParent() {
        return parent;
    }

    public abstract TreeNode done(Object arg, Throwable throwable);

    public Stream<? extends TreeNode> childStream(){
        return children.stream();
    }

    public int childCount(){
        return children.size();
    }

    protected void setThrown(Throwable thrown){
        this.thrown = thrown;
    }

    public Throwable getThrown() {
        return thrown;
    }
}
