package ee.ut.dendroloj;

import java.lang.reflect.Method;


class TraceProcessor {

    static MetaTreeNode root = new MetaTreeNode();
    static TreeNode node = root;

    public static void processEntry(Method method, Object[] callArguments){
        node = node.addChild(new CallTreeNode(method.getName(), callArguments, method.getParameters()));
        SimpleTreeLayout.updateGraph(root);

    }

    public static void processExit(Object returnValue, Throwable throwable){
        node = node.done(returnValue, throwable).getParent();
        SimpleTreeLayout.updateGraph(root);

    }


}

// List<StackWalker.StackFrame> relevantFrames = StackWalker.getInstance(Set.of(), 3).walk(s -> s.skip(1).limit(2).collect(Collectors.toList()));
// make threadsafe?