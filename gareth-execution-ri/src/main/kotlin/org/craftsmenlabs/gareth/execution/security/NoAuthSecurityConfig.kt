package org.craftsmenlabs.gareth.execution.security

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)
open class NoAuthSecurityConfig : WebSecurityConfigurerAdapter() {

   /* @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }*/

    override fun configure(http: HttpSecurity) {
        http.headers().disable().authorizeRequests().antMatchers("/**/*").permitAll().and().csrf().disable()
    }

}
