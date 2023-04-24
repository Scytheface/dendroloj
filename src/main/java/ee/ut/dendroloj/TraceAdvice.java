package ee.ut.dendroloj;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;
class TraceAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(
            @Advice.Origin Method method,
            @Advice.AllArguments Object[] callArguments
    ) {
        TraceProcessor.processEntry(method, callArguments);
    }

    @Advice.OnMethodExit(onThrowable = RuntimeException.class)
    public static void onExit(
            @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
            @Advice.Thrown Throwable throwable
            ) {
        TraceProcessor.processExit(returnValue, throwable);
    }
}


