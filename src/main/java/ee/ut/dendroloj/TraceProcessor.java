package ee.ut.dendroloj;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class TraceProcessor {

    static MetaTreeNode root = new MetaTreeNode();
    static TreeNode node = root;

    // Executor used to run the actual update logic.
    // This is used to prevent debuggers from stepping to execution logic when you step in at the start of a @Grow method.
    static ExecutorService exec = Executors.newCachedThreadPool();

    public static void processEntry(Method method, Object[] callArguments) throws ExecutionException, InterruptedException {
        // Obtain current thread id before dispatching to executor to distinguish between calling threads:
        // long threadId = Thread.currentThread().getId();
        exec.submit(() -> {
            node = node.addChild(new CallTreeNode(method.getName(), callArguments, method.getParameters()));
            SimpleTreeLayout.updateGraph(root);
        }).get();
    }

    public static void processExit(Object returnValue, Throwable throwable) throws ExecutionException, InterruptedException {
        // long threadId = Thread.currentThread().getId();
        exec.submit(() -> {
            node = node.done(returnValue, throwable).getParent();
            SimpleTreeLayout.updateGraph(root);
        }).get();
    }

}

// List<StackWalker.StackFrame> relevantFrames = StackWalker.getInstance(Set.of(), 3).walk(s -> s.skip(1).limit(2).collect(Collectors.toList()));
// make threadsafe?