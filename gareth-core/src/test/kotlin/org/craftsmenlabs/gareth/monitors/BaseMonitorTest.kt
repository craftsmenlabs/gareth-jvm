package org.craftsmenlabs.gareth.monitors

import mockit.Expectations
import mockit.Injectable
import mockit.Mocked
import org.assertj.core.api.Assertions.assertThat
import org.craftsmenlabs.Captors
import org.craftsmenlabs.gareth.GlueLineExecutor
import org.craftsmenlabs.gareth.jpa.ExperimentStorage
import org.craftsmenlabs.gareth.model.Experiment
import org.craftsmenlabs.gareth.model.ExperimentRunEnvironment
import org.craftsmenlabs.gareth.model.ExperimentState
import org.craftsmenlabs.gareth.providers.ExperimentProvider
import org.craftsmenlabs.gareth.time.TimeService
import org.craftsmenlabs.monitorintegration.computationTestOverride
import org.craftsmenlabs.monitorintegration.ioTestOverride
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers

class BaseMonitorTest {

    val ID1 = 111L
    val ID2 = 222L
    val ID3 = 333L

    @Injectable
    lateinit var experimentProvider: ExperimentProvider

    @Injectable
    lateinit var dateTimeService: TimeService

    @Injectable
    lateinit var glueLineExecutor: GlueLineExecutor

    @Injectable
    lateinit var experimentStorage: ExperimentStorage

    @Injectable
    lateinit var experimentRunEnvironment: ExperimentRunEnvironment

    lateinit var monitor: TestBaseMonitor

    @Mocked
    lateinit var schedulers: Schedulers;

    var shouldThrowEx = mutableListOf<Boolean>()

    @Before
    fun setUp() {
        schedulers.ioTestOverride()
        schedulers.computationTestOverride()

        monitor = TestBaseMonitor(experimentProvider, dateTimeService, experimentStorage, ExperimentState.NEW, shouldThrowEx)
    }

    @Test
    fun shouldContinueToWorkWhenStreamThrowsAnException() {
        shouldThrowEx.add(false)
        shouldThrowEx.add(true)
        shouldThrowEx.add(false)

        val experiments = listOf(
                Experiment.createDefault().copy(id = ID1),
                Experiment.createDefault().copy(id = ID2),
                Experiment.createDefault().copy(id = ID3))

        object : Expectations() {
            init {
                experimentProvider.observable
                result = experiments.toObservable()
            }
        }

        monitor.start()

        val captors = Captors.experimentStorage_save(experimentStorage)

        assertThat(captors).hasSize(2)
        assertThat(captors[0].id).isEqualTo(ID1)
        assertThat(captors[1].id).isEqualTo(ID3)
    }

    class TestBaseMonitor(
            experimentProvider: ExperimentProvider,
            dateTimeService: TimeService,
            experimentStorage: ExperimentStorage,
            experimentState: ExperimentState,
            val results : List<Boolean>)
        : BaseMonitor(experimentProvider,
            dateTimeService,
            experimentStorage,
            experimentState) {

        var index = 0

        override fun extend(observable: Observable<Experiment>): Observable<Experiment> {
            val shouldThrowEx: Boolean? = results.getOrNull(index++)
            if(shouldThrowEx?:false){
                throw IllegalStateException("TestException");
            } else {
                return observable
            }
        }
    }
}