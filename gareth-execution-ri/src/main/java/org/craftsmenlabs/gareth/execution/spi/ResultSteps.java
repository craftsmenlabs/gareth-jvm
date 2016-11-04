package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.springframework.stereotype.Service;

@Service
public class ResultSteps {

    @Success(glueLine = "^send email to (.*?)$")
    public void sendEmail(final String recipient) {
    }

    @Success(glueLine = "^send text to (.*?)$")
    public void sendText(final String recipient) {
    }

    @Failure(glueLine = "^send email to (.*?)$")
    public void sendFailureEmail(final String recipient) {
    }

    @Failure(glueLine = "^send text to (.*?)$")
    public void sendFailureText(final String recipient) {
    }


}
