package ee.ut.dendroloj;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class TraceProcessor {

    // Executor used to run the actual update logic.
    // This is used to prevent debuggers from stepping to execution logic when you step in at the start of a @Grow method.
    // Using a single threaded executor eliminates most possible race conditions.
    private static final ExecutorService exec = Executors.newSingleThreadExecutor(TraceProcessor::createThread);

    private static final String PROCESSOR_THREAD_NAME = "dendroloj-traceprocessor";

    static {
        // No-op submit to create thread and load all necessary classes.
        // If classes are not loaded beforehand then Byte Buddy instrumentation code called during class loading
        // will show up when stepping into code annotated with @Grow.
        exec.submit(() -> {
        });
    }

    // Important: createThread, processEntry and processExit must call only Java standard library methods.
    // Any other methods will show up in the debugger when stepping into code annotated with @Grow.

    private static Thread createThread(Runnable runnable) {
        Thread thread = new Thread(runnable, PROCESSOR_THREAD_NAME);
        thread.setDaemon(true);
        return thread;
    }

    public static void processEntry(Method method, Object[] callArguments) throws ExecutionException, InterruptedException {
        // To avoid recursion during toString calls on arguments, check if we are already in the graph processing thread and exit early if so.
        if (Thread.currentThread().getName().equals(PROCESSOR_THREAD_NAME)) return;
        exec.submit(() -> CallTreeLayout.processCall(method, callArguments)).get();
    }

    public static void processExit(Object returnValue, Throwable throwable) throws ExecutionException, InterruptedException {
        // To avoid recursion during toString calls on arguments, check if we are already in the graph processing thread and exit early if so.
        if (Thread.currentThread().getName().equals(PROCESSOR_THREAD_NAME)) return;
        exec.submit(() -> CallTreeLayout.processReturn(returnValue, throwable)).get();
    }

}

// List<StackWalker.StackFrame> relevantFrames = StackWalker.getInstance(Set.of(), 3).walk(s -> s.skip(1).limit(2).collect(Collectors.toList()));
// make threadsafe?