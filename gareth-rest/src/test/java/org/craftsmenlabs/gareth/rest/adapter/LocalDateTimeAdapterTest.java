package org.craftsmenlabs.gareth.rest.adapter;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.*;

/**
 * Created by hylke on 18/09/15.
 */
public class LocalDateTimeAdapterTest {

    private LocalDateTimeAdapter localDateTimeAdapter;

    @Before
    public void setUp() throws Exception {
        localDateTimeAdapter = new LocalDateTimeAdapter();
    }

    @Test
    public void testUnmarshal() throws Exception {
        final LocalDateTime localDateTime = localDateTimeAdapter.unmarshal("2015-09-14T17:51:31Z");
        assertNotNull(localDateTime);
        assertEquals(2015, localDateTime.getYear());
        assertEquals(Month.SEPTEMBER, localDateTime.getMonth());
        assertEquals(14, localDateTime.getDayOfMonth());
        assertEquals(17, localDateTime.getHour());
        assertEquals(51, localDateTime.getMinute());
        assertEquals(31, localDateTime.getSecond());

    }

    @Test
    public void testMarshal() throws Exception {
        final LocalDateTime localDateTime = LocalDateTime.of(2015, Month.SEPTEMBER, 14, 17, 51, 31);
        final String localDateTimePresentation = localDateTimeAdapter.marshal(localDateTime);
        assertEquals("2015-09-14T17:51:31Z", localDateTimePresentation);
    }
}