package ee.ut.dendroloj;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.ModifierAdjustment;
import net.bytebuddy.description.modifier.SyntheticState;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;

class AgentTracer {

    public static void init() {
        // Void System.err during install to suppress warning about dynamic agent loading.
        // This is a temporary hack. See https://github.com/Scytheface/dendroloj/issues/1 for more info.
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(OutputStream.nullOutputStream()));
        Instrumentation inst;
        try {
            inst = ByteBuddyAgent.install();
        } finally {
            // Restore original System.err.
            // Thread safety: We expect that there are no other threads manipulating System.err during the agent install.
            System.setErr(originalErr);
        }

        // TraceProcessor is originally private to ensure that users cannot access it. It is marked public here, because generated code needs to access it from outside.
        // TraceProcessor and its methods are marked synthetic to ensure that they are skipped by default when stepping through code during debugging.
        new AgentBuilder.Default()
                .type(ElementMatchers.named("ee.ut.dendroloj.TraceProcessor"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.modifiers(Visibility.PUBLIC, SyntheticState.SYNTHETIC)
                                .visit(new ModifierAdjustment().withMethodModifiers(SyntheticState.SYNTHETIC))
                )
                .installOn(inst);

        new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(ElementMatchers.declaresMethod(ElementMatchers.isAnnotatedWith(Grow.class)))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.visit(Advice.to(TraceAdvice.class)
                                .on(ElementMatchers.isAnnotatedWith(Grow.class)))
                )
                .installOn(inst);
    }

}

