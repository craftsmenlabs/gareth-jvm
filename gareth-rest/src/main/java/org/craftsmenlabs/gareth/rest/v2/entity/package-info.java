@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(type = LocalDateTime.class,
                value = LocalDateTimeAdapter.class)
})
package org.craftsmenlabs.gareth.rest.v2.entity;

import org.craftsmenlabs.gareth.rest.adapter.LocalDateTimeAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.time.LocalDateTime;