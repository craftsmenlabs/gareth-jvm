package org.craftsmenlabs.gareth.execution.spi;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.craftsmenlabs.ExperimentDefinition;
import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleOfWidgets implements ExperimentDefinition
{

	@Autowired
	private MockDB mockDB;

	@Assume(glueLine = "^sale of widgets has risen by (\\d+?) per cent$")
	public boolean hasRisenByPercent(final int percentage)
	{
		return percentage > 20;
	}

	@Time(glueLine = "next Easter")
	public Duration nextEaster()
	{
		return Duration.of(10L, ChronoUnit.DAYS);
	}

}
