package org.craftsmenlabs.gareth.execution

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
open class CoreConfig {

    @Bean
    open fun getTaskScheduler() = ThreadPoolTaskExecutor()

}