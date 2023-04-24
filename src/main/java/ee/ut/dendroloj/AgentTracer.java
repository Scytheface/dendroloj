package ee.ut.dendroloj;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.instrument.Instrumentation;

class AgentTracer {
    static void init() {
        Instrumentation inst = ByteBuddyAgent.install();
        new AgentBuilder.Default()
                .type(ElementMatchers.named("ee.ut.dendroloj.TraceProcessor"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.modifiers(Visibility.PUBLIC))
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

