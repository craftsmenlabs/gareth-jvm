package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.execution.RunContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleOfFruit
{

	@Autowired
	private MockDB mockDB;

	@Assume(glueLine = "^sale of fruit has risen by (\\d+?) per cent$")
	public void hasRisenByPercent(final RunContext context, final int percentage)
	{
		long baseLine = context.getLong("fruit");
		if (percentage < 60)
		{
			throw new RuntimeException("Expected percentage > 60");
		}
	}

}
