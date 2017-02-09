package org.craftsmenlabs.gareth.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import mockit.Verifications
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.Captors
import org.craftsmenlabs.gareth.ExperimentStorage
import org.craftsmenlabs.gareth.GlueLineLookup
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentDetails
import org.craftsmenlabs.gareth.model.ExperimentResults
import org.craftsmenlabs.gareth.model.ExperimentTiming
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
    lateinit var glueLineLookup: GlueLineLookup

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: IsReadyMonitor

    @Mocked
    lateinit var schedulers: Schedulers;

    @Before
    fun setUp() {
        schedulers.ioTestOverride()
        schedulers.computationTestOverride()

        monitor = IsReadyMonitor(experimentProvider, dateTimeService, experimentStorage, glueLineLookup)
    }

    @Test
    fun shouldOnlyOperateOnNewExperiments() {
        val details = ExperimentDetails("name", "baseline", "assume", "time", "success", "failure", 111)
        val timingNew = ExperimentTiming(localDateTime1)
        val timingReady = ExperimentTiming(localDateTime2, localDateTime3)
        val results = ExperimentResults()
        val experimentNew = Experiment(details, timingNew, results)
        val experimentReady = Experiment(details, timingReady, results)
        val experiments = listOf(experimentNew, experimentReady)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()
            }
        }

        monitor.start();

        object : Verifications() {
            init {
                glueLineLookup.isExperimentReady(experimentNew)
                times = 1

                glueLineLookup.isExperimentReady(experimentReady)
                times = 0
            }
        }
    }

    @Test
    fun shouldOnlyUpdateExperimentsWithGluelines() {
        val details = ExperimentDetails("name", "baseline", "assume", "time", "success", "failure", 111)
        val timingNew1 = ExperimentTiming(localDateTime1)
        val timingNew2 = ExperimentTiming(localDateTime2)
        val results = ExperimentResults()
        val experiment1 = Experiment(details, timingNew1, results)
        val experiment2 = Experiment(details, timingNew2, results)
        val experiments = listOf(experiment1, experiment2)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                glueLineLookup.isExperimentReady(experiment1)
                result = false

                glueLineLookup.isExperimentReady(experiment2)
                result = true

                dateTimeService.now()
                result = localDateTime3
            }
        }

        monitor.start();

        val captor = Captors.experimentStorage_save(experimentStorage)

        assertThat(captor).hasSize(1);
        assertThat(captor[0]).isEqualTo(experiment2.copy(timing = experiment2.timing.copy(ready = localDateTime3)))
    }

    @Test
    fun shouldUpdateReadyTimestampWhenReady() {
        val details = ExperimentDetails("name", "baseline", "assume", "time", "success", "failure", 111)
        val timingNew = ExperimentTiming(localDateTime1)
        val results = ExperimentResults()
        val experiment = Experiment(details, timingNew, results)
        val experiments = listOf(experiment)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                glueLineLookup.isExperimentReady(experiment)
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
