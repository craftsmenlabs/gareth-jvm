package org.craftsmenlabs.gareth.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.Captors
import org.craftsmenlabs.gareth.GluelineValidator
import org.craftsmenlabs.gareth.jpa.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentResults
import org.craftsmenlabs.gareth.model.ExperimentTiming
import org.craftsmenlabs.gareth.model.Gluelines
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.craftsmenlabs.monitorintegration.computationTestOverride
import org.craftsmenlabs.monitorintegration.ioTestOverride
import org.junit.Before
import org.junit.Test
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers
import java.time.LocalDateTime

class IsReadyMonitorTest {

    val localDateTime1 = LocalDateTime.now().minusHours(1)
    val localDateTime2 = LocalDateTime.now().minusHours(2)
    val localDateTime3 = LocalDateTime.now().minusHours(3)

    @Injectable
    lateinit var experimentProvider: ExperimentProvider

    @Injectable
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var gluelineValidator: GluelineValidator

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: IsReadyMonitor

    @Mocked
    lateinit var schedulers: Schedulers;

    @Before
    fun setUp() {
        schedulers.ioTestOverride()
        schedulers.computationTestOverride()

        monitor = IsReadyMonitor(experimentProvider, dateTimeService, experimentStorage, gluelineValidator)
    }

    @Test
    fun shouldOnlyOperateOnNewExperiments() {
        val glueLines = Gluelines("baseline", "assumption", "success", "failure", "time")
        val timingNew = ExperimentTiming(localDateTime1)
        val timingReady = ExperimentTiming(localDateTime2, localDateTime3)
        val results = ExperimentResults()
        val experimentNew = Experiment(id = 0, name = "name", glueLines = glueLines, timing = timingNew, results = results)
        val experimentReady = experimentNew.copy(timing = timingReady)
        val experiments = listOf(experimentNew, experimentReady)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()
            }
        }

        monitor.start();

    }

    @Test
    fun shouldUpdateReadyTimestampWhenReady() {
        val glueLines = Gluelines("baseline", "assumption", "success", "failure", "time")
        val timingNew = ExperimentTiming(localDateTime1)
        val results = ExperimentResults()
        val experiment = Experiment(id = 111, name = "name", glueLines = glueLines, timing = timingNew, results = results)
        val experiments = listOf(experiment)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                gluelineValidator.gluelinesAreValid(glueLines)
                result = true

                dateTimeService.now()
                result = localDateTime2
            }
        }

        monitor.start();

        val storageCaptor = Captors.experimentStorage_save(experimentStorage)

        assertThat(storageCaptor).hasSize(1);

        assertThat(storageCaptor[0]).isEqualTo(
                experiment.copy(
                        timing = experiment.timing.copy(ready = localDateTime2)))
    }
}
