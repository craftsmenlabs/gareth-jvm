package org.craftsmenlabs.gareth.jpa

import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JPAPersistenceConfig {

    @Bean
    open fun h2servletRegistration(): ServletRegistrationBean {
        val h2servlet = org.h2.server.web.WebServlet()
        val registrationBean = ServletRegistrationBean(h2servlet)
        registrationBean.addInitParameter("webAllowOthers", "true")
        registrationBean.addInitParameter("db.tcpServer", "-tcpAllowOthers")
        registrationBean.addUrlMappings("/console/*")
        return registrationBean
    }

}