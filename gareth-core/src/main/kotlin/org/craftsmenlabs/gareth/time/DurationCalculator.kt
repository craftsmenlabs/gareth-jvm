package org.craftsmenlabs.gareth.time

import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.model.Experiment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class DurationCalculator @Autowired constructor(
        private val durationExpressionParser: DurationExpressionParser,
        private val timeService: DateTimeService,
        private val executor: GlueLineExecutor) {

    fun getDuration(experiment: Experiment): Duration {
        val time = durationExpressionParser.parse(experiment.details.time)
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