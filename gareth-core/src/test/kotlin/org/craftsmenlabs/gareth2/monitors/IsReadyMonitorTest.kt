package org.craftsmenlabs.gareth2.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import mockit.Verifications
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.gareth2.ExperimentStorage
import org.craftsmenlabs.gareth2.GluelineLookup
import org.craftsmenlabs.gareth2.model.Experiment
import org.craftsmenlabs.gareth2.model.ExperimentDetails
import org.craftsmenlabs.gareth2.model.ExperimentResults
import org.craftsmenlabs.gareth2.model.ExperimentTiming
import org.craftsmenlabs.gareth2.providers.ExperimentProvider
import org.craftsmenlabs.gareth2.time.DateTimeService
import org.craftsmenlabs.gareth2.util.computationTestOverride
import org.craftsmenlabs.gareth2.util.ioTestOverride
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
    lateinit var dateTimeService: DateTimeService

    @Injectable
    lateinit var gluelineLookup: GluelineLookup

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    lateinit var monitor: IsReadyMonitor

    @Before
    fun setUp() {
        monitor = IsReadyMonitor(experimentProvider, dateTimeService, experimentStorage, gluelineLookup)
    }

    @Test
    fun shouldOnlyOperateOnNewExperiments() {
        val details = ExperimentDetails("name", "baseline", "assume", "time", "success", "failure", 111)
        val timingNew = ExperimentTiming(localDateTime1)
        val timingReady = ExperimentTiming(localDateTime2, localDateTime3)
        val results = ExperimentResults()
        val experimentNew = Experiment(details, timingNew, results, "42")
        val experimentReady = Experiment(details, timingReady, results, "42")
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
                gluelineLookup.isExperimentReady(experimentNew)
                times = 1

                gluelineLookup.isExperimentReady(experimentReady)
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
        val experiment1 = Experiment(details, timingNew1, results, "42")
        val experiment2 = Experiment(details, timingNew2, results, "42")
        val experiments = listOf(experiment1, experiment2)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                gluelineLookup.isExperimentReady(experiment1)
                result = false

                gluelineLookup.isExperimentReady(experiment2)
                result = true

                dateTimeService.now()
                result = localDateTime3
            }
        }

        monitor.start();

        object : Verifications() {
            init {
                experimentStorage.save(experiment1)
                times = 0

                experimentStorage.save(experiment2)
                times = 1
            }
        }
    }

    @Test
    fun shouldUpdateReadyTimestampWhenReady(@Mocked schedulers: Schedulers) {
        val details = ExperimentDetails("name", "baseline", "assume", "time", "success", "failure", 111)
        val timingNew = ExperimentTiming(localDateTime1)
        val results = ExperimentResults()
        val experiment = Experiment(details, timingNew, results, "42")
        val experiments = listOf(experiment)

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()

                gluelineLookup.isExperimentReady(experiment)
                result = true

                dateTimeService.now()
                result = localDateTime2
            }
        }

        schedulers.ioTestOverride()
        schedulers.computationTestOverride()

        monitor.start();

        assertThat(experiment.timing.ready).isSameAs(localDateTime2)
    }
}
