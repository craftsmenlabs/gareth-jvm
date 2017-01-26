package org.craftsmenlabs.gareth2.atdd

import com.jayway.awaitility.Awaitility
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent
import java.util.concurrent.TimeUnit
import java.util.stream.Stream


abstract class SpringApplicationWrapper : ApplicationListener<ApplicationEvent> {

    private lateinit var context: ConfigurableApplicationContext
    private var isReady: Boolean = false
    private var isStopped: Boolean = false
    fun close() {
        if (context != null)
            context.close()
    }

    fun setContext(context: ConfigurableApplicationContext) {
        this.context = context
        context.addApplicationListener(this)
    }

    override fun onApplicationEvent(event: ApplicationEvent) {
        if (event is ApplicationReadyEvent)
            isReady = true
        if (event is ContextClosedEvent)
            isStopped = true
    }

    fun isReady(): Boolean {
        return isReady
    }

    fun isStopped(): Boolean {
        return isStopped
    }

    companion object {
        fun closeAll(vararg multiple: SpringApplicationWrapper) {
            Stream.of<SpringApplicationWrapper>(*multiple).filter { i -> i != null }.forEach { app -> app.close() }
            try {
                return Awaitility
                        .await()
                        .pollInterval(1, TimeUnit.SECONDS)
                        .and()
                        .with()
                        .pollDelay(2, TimeUnit.SECONDS)
                        .atMost(Math.max(30, 6).toLong(), TimeUnit.SECONDS)
                        .until({ Stream.of<SpringApplicationWrapper>(*multiple).allMatch { app -> app == null || app.isStopped() } })
            } catch (e: Exception) {
                throw IllegalStateException("Failed to close servers")
            }
        }
    }


}