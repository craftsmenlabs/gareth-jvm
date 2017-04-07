package org.craftsmenlabs.gareth.api.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Optional;
import org.craftsmenlabs.gareth.model.GlueLineType;
import org.junit.Test;

public class GlueLineTypeTest
{

	@Test
	public void safeValueOfExistingValue() throws Exception
	{
		final Optional<GlueLineType> glueLineType = GlueLineType.safeValueOf("BASELINE");
		assertTrue(glueLineType.isPresent());
		assertEquals(GlueLineType.BASELINE, glueLineType.get());
	}

	@Test
	public void safeValueOfNonExistingValue() throws Exception
	{
		final Optional<GlueLineType> glueLineType = GlueLineType.safeValueOf("NONEXISTING");
		assertFalse(glueLineType.isPresent());
	}

	@Test
	public void safeValueOfWithNull() throws Exception
	{
		final Optional<GlueLineType> glueLineType = GlueLineType.safeValueOf(null);
		assertFalse(glueLineType.isPresent());
	}

}