package org.craftsmenlabs.gareth.validator.time

import org.craftsmenlabs.gareth.validator.GlueLineExecutor
import org.craftsmenlabs.gareth.validator.model.ExperimentDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class DurationCalculator @Autowired constructor(
        private val durationExpressionParser: DurationExpressionParser,
        private val executor: GlueLineExecutor) {

    fun getDuration(experiment: ExperimentDTO): Duration {
        val time = durationExpressionParser.parse(experiment.glueLines.time)
        if (time != null) {
            return time
        } else {
            return executor.getDuration(experiment)
        }
    }

    fun getDifferenceInSeconds(now: LocalDateTime, execute: LocalDateTime?, duration: Duration): Long {
        val assumePlanned = execute?.plus(duration)
        return ChronoUnit.SECONDS.between(now, assumePlanned)
    }

}