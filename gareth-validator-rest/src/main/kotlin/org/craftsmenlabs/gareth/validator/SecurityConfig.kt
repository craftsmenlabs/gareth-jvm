package org.craftsmenlabs.gareth.validator


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
open class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var userService: UserDetailsService


    //@Bean
    //override fun authenticationManagerBean(): AuthenticationManager {
    //    return super.authenticationManagerBean()
    //}


    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService<UserDetailsService>(userService)
    }


    override fun configure(http: HttpSecurity) {
        http.headers().disable().authorizeRequests().anyRequest()
                .permitAll()
                .and().csrf().disable()
    }
}

@Service
open class AnyUserWillDoService : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails? {
        return User(userName, userName, true, true, true, true,
                AuthorityUtils.createAuthorityList("user"))
    }
}

