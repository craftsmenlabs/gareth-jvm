package org.craftsmenlabs.gareth2.rx

import rx.Scheduler
import rx.annotations.Experimental
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler
import java.util.concurrent.Executor

class KSchedulers {
    companion object {

        fun immediate(): Scheduler = Schedulers.immediate()

        fun trampoline(): Scheduler = Schedulers.immediate()

        fun newThread(): Scheduler = Schedulers.newThread()

        fun computation(): Scheduler = Schedulers.computation()

        fun io(): Scheduler = Schedulers.io()

        fun test(): TestScheduler = Schedulers.test()

        fun from(executor: Executor) = Schedulers.from(executor)

        @Experimental
        fun reset() = Schedulers.reset()

        fun start() = Schedulers.start()

        fun shutdown() = Schedulers.shutdown()
    }
}