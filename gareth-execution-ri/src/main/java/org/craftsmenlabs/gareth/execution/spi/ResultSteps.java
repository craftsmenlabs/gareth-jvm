package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.api.execution.ExecutionRunContext;
import org.springframework.stereotype.Service;

@Service
public class ResultSteps {

    @Success(glueLine = "^send email to (.*?)$")
    public void sendEmail(ExecutionRunContext runContext, String recipient) {
        runContext.storeString("result", "sending success mail to " + recipient);
    }

    @Success(glueLine = "^send text to (.*?)$")
    public void sendText(ExecutionRunContext runContext, String recipient) {
        runContext.storeString("result", "sending success text to " + recipient);
    }

    @Failure(glueLine = "^send email to (.*?)$")
    public void sendFailureEmail(ExecutionRunContext runContext, String recipient) {
        runContext.storeString("result", "sending failure mail to " + recipient);
    }


    @Failure(glueLine = "^send text to (.*?)$")
    public void sendFailureText(ExecutionRunContext runContext, String recipient) {
        runContext.storeString("result", "sending failure text to " + recipient);
    }

}
