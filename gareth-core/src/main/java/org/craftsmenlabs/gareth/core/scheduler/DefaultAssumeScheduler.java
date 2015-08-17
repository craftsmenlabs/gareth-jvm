package org.craftsmenlabs.gareth.core.scheduler;

import com.xeiam.sundial.SundialJobScheduler;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.exception.GarethInvocationException;
import org.craftsmenlabs.gareth.api.exception.GarethUnknownDefinitionException;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.craftsmenlabs.gareth.api.scheduler.AssumeScheduler;
import org.craftsmenlabs.gareth.core.invoker.MethodInvokerImpl;
import org.craftsmenlabs.gareth.core.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.*;

/**
 * Created by hylke on 17/08/15.
 */
public class DefaultAssumeScheduler implements AssumeScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAssumeScheduler.class);

    private final MethodInvoker methodInvoker;
    private final boolean ignoreInvocationExceptions;


    private DefaultAssumeScheduler(final Builder builder) {
        this.methodInvoker = builder.methodInvoker;
        this.ignoreInvocationExceptions = builder.ignoreInvocationExceptions;
        SundialJobScheduler.startScheduler();
    }

    @Override
    public void schedule(final ExperimentContext experimentContext) {
        final Duration time = experimentContext.getTime();
        final Calendar now = new GregorianCalendar();
        now.add(Calendar.MILLISECOND, new Long(time.toMillis()).intValue());
        try {

            // Job params
            final Map<String, Object> jobParams = new HashMap<>();
            jobParams.put("experimentContext", experimentContext);
            jobParams.put("methodInvoker", methodInvoker);

            final String jobName = experimentContext.getExperimentName() + "-" + new Random().nextInt();
            final String triggerName = jobName + "-trigger";

            SundialJobScheduler.addJob(jobName, DefaultInvocationJob.class.getName(), jobParams, false);
            SundialJobScheduler.addSimpleTrigger(triggerName, jobName, 0, 1, now.getTime(), null);

            // Start the job

            //SundialJobScheduler.startJob(jobName, jobParams);


        } catch (final GarethUnknownDefinitionException | GarethInvocationException e) {
            logger.error("Problem during assumption invocation", e);
            if (!ignoreInvocationExceptions) {
                throw e;
            }
        }
    }

    public static class Builder {
        private ReflectionHelper reflectionHelper = new ReflectionHelper();

        private MethodInvoker methodInvoker = new MethodInvokerImpl(reflectionHelper);

        private boolean ignoreInvocationExceptions;

        public Builder setIgnoreInvocationExceptions(final boolean ignoreInvocationExceptions) {
            this.ignoreInvocationExceptions = ignoreInvocationExceptions;
            return this;
        }

        public Builder setMethodInvoker(final MethodInvoker methodInvoker) {
            this.methodInvoker = methodInvoker;
            return this;
        }

        public AssumeScheduler build() {
            return new DefaultAssumeScheduler(this);
        }
    }

}
