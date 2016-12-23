package org.craftsmenlabs.gareth2.util

import mockit.Expectations
import rx.internal.schedulers.ImmediateScheduler
import rx.schedulers.Schedulers

fun Schedulers.ioTestOverride() {
    object : Expectations() {
        init {
            Schedulers.io()
            result = ImmediateScheduler.INSTANCE
        }
    }
}

fun Schedulers.computationTestOverride() {
    object : Expectations() {
        init {
            Schedulers.computation()
            result = ImmediateScheduler.INSTANCE
        }
    }
}
