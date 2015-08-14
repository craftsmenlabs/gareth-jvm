package org.craftsmenlabs.gareth.core.scheduler.akka;

import akka.actor.ActorSystem;
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.api.scheduler.AssumeScheduler;
import org.craftsmenlabs.gareth.core.invoker.MethodInvokerImpl;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by hylke on 14/08/15.
 */
public class AkkaAssumeScheduler implements AssumeScheduler {


    private final ActorSystem actorSystem;

    private final MethodInvoker methodInvoker;

    private final boolean ignoreInvocationExceptions;

    public AkkaAssumeScheduler(final Builder builder) {
        this.methodInvoker = builder.methodInvoker;
        this.actorSystem = builder.actorSystem;
        this.ignoreInvocationExceptions = builder.ignoreInvocationExceptions;
    }

    @Override
    public void schedule(final Method assumeMethod, final Duration duration, final Method successMethod, final Method failureMethod) {

        try {
            actorSystem.scheduler().scheduleOnce(scala.concurrent.duration.Duration.create(duration.toMillis(), TimeUnit.MILLISECONDS), () -> {
                try {
                    methodInvoker.invoke(assumeMethod);
                    if (null != successMethod) {
                        methodInvoker.invoke(successMethod);
                    }
                } catch (final Exception e) {
                    if (null != failureMethod) {
                        methodInvoker.invoke(failureMethod);
                    }
                }
            }, actorSystem.dispatcher());

        } catch (final GarethUnknownDefinitionException | GarethInvocationException e) {
            if (!ignoreInvocationExceptions) {
                throw e;
            }
        }
    }

    public static class Builder {

        private ReflectionHelper reflectionHelper = new ReflectionHelper();

        private MethodInvoker methodInvoker = new MethodInvokerImpl(reflectionHelper);

        private ActorSystem actorSystem = ActorSystem.create();

        private boolean ignoreInvocationExceptions;


        public Builder setActorSystem(final ActorSystem actorSystem) {
            this.actorSystem = actorSystem;
            return this;
        }

        public Builder setIgnoreInvocationExceptions(final boolean ignoreInvocationExceptions) {
            this.ignoreInvocationExceptions = ignoreInvocationExceptions;
            return this;
        }

        public Builder setMethodInvoker(final MethodInvoker methodInvoker) {
            this.methodInvoker = methodInvoker;
            return this;
        }

        public AssumeScheduler build() {
            return new AkkaAssumeScheduler(this);
        }
    }
}
