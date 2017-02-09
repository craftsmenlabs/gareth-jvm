package org.craftsmenlabs.gareth.jpa

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.persistence.AttributeConverter
import javax.persistence.Converter


@Converter(autoApply = true)
class DateTimeConverter : AttributeConverter<LocalDateTime, Timestamp> {

    override fun convertToDatabaseColumn(localDateTime: LocalDateTime?): Timestamp? {
        return if (localDateTime != null) Timestamp.valueOf(localDateTime) else null
    }

    override fun convertToEntityAttribute(timestamp: Timestamp?): LocalDateTime? {
        return if (timestamp != null) timestamp!!.toLocalDateTime() else null
    }
}