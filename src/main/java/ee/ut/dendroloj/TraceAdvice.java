package ee.ut.dendroloj;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TraceAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(
            @Advice.Origin Method method,
            @Advice.AllArguments Object[] callArguments
    ) throws InterruptedException, ExecutionException {
        TraceProcessor.processEntry(method, callArguments);
    }

    @Advice.OnMethodExit(onThrowable = RuntimeException.class)
    public static void onExit(
            @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
            @Advice.Thrown Throwable throwable
    ) throws ExecutionException, InterruptedException {
        TraceProcessor.processExit(returnValue, throwable);
    }
}


