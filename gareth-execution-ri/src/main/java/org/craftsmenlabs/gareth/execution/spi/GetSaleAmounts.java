package org.craftsmenlabs.gareth.execution.spi;

import org.craftsmenlabs.ExperimentDefinition;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.model.ExecutionRunContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetSaleAmounts implements ExperimentDefinition
{

	@Autowired
	private MockDB mockDB;

	@Baseline(glueLine = "^sale of (.*?)$")
	public void getSaleOfItem(final ExecutionRunContext context, final String item)
	{
		context.storeString("getting value for ", item);
		if (item.equals("fruit"))
		{

			context.storeLong("fruit", 42);
		}

		if (item.equals("widgets"))
		{
			
			context.storeLong("peaches", 50);
		}
	}

}
