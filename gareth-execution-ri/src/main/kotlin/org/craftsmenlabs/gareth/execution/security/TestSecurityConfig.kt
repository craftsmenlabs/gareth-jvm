package org.craftsmenlabs.gareth.execution.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Profile("test")
open class TestSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var userService: UserDetailsService

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService<UserDetailsService>(userService)
    }

    override fun configure(http: HttpSecurity) {
        http.headers().disable().authorizeRequests().antMatchers("**/*.html")
                .permitAll().antMatchers("/**/*").authenticated()
                .and().httpBasic()
                .and().userDetailsService(userService)
                .csrf().disable()
    }


}
