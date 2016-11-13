package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Success;
import org.craftsmenlabs.gareth.execution.RunContext;
import org.springframework.stereotype.Service;

@Service
public class ResultSteps
{

	@Success(glueLine = "^send email to (.*?)$")
	public void sendEmail(RunContext runContext, String recipient)
	{
		runContext.storeString("emailtext", "success" + runContext.getLong("bananas"));
	}

	@Success(glueLine = "^send text to (.*?)$")
	public void sendText(RunContext runContext, String recipient)
	{
	}

	@Failure(glueLine = "^send email to (.*?)$")
	public void sendFailureEmail(RunContext runContext, String recipient)
	{
	}

	@Failure(glueLine = "^send text to (.*?)$")
	public void sendFailureText(RunContext runContext, String recipient)
	{

	}

}
