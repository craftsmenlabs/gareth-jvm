package org.craftsmenlabs.gareth.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(final String dateString) throws Exception {
        final Instant instant = Instant.parse(dateString);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return dateTime;
    }

    @Override
    public String marshal(final LocalDateTime dateTime) throws Exception {
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }
}
