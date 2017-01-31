package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.model.ExecutionRunContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetSaleAmounts
{

	@Autowired
	private MockDB mockDB;

	@Baseline(glueLine = "^sale of (.*?)$")
	public void getSaleOfItem(final ExecutionRunContext context, final String item)
	{
		if (item == "fruit")
		{
			context.storeLong("fruit", 42);
		}

		if (item == "widgets")
		{
			context.storeLong("peaches", 50);
		}
	}

}
