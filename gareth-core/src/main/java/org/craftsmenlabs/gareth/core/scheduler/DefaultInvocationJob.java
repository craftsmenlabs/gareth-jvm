package org.craftsmenlabs.gareth.core.scheduler;

import com.xeiam.sundial.Job;
import com.xeiam.sundial.exceptions.JobInterruptException;
import org.craftsmenlabs.gareth.api.context.ExperimentContext;
import org.craftsmenlabs.gareth.api.invoker.MethodInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Created by hylke on 17/08/15.
 */
public class DefaultInvocationJob extends Job {

    private static final Logger logger = LoggerFactory.getLogger(DefaultInvocationJob.class);

    @Override
    public void doRun() throws JobInterruptException {
        final MethodInvoker methodInvoker = getJobContext().getRequiredValue("methodInvoker");
        final ExperimentContext experimentContext = getJobContext().getRequiredValue("experimentContext");

        try {
            logger.debug("Invoking assumption");
            methodInvoker.invoke(experimentContext.getAssume());
            experimentContext.setAssumeRun(LocalDateTime.now());
            if (null != experimentContext.getSuccess()) {
                logger.debug("Invoking success");
                methodInvoker.invoke(experimentContext.getSuccess());
                experimentContext.setSuccessRun(LocalDateTime.now());
            }
        } catch (final Exception e) {
            if (null != experimentContext.getFailure()) {
                logger.debug("Invoking failure");
                methodInvoker.invoke(experimentContext.getFailure());
                experimentContext.setFailureRun(LocalDateTime.now());
            }
        }
    }
}
