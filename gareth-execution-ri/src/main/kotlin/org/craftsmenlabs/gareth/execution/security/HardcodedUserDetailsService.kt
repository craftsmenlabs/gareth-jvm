package org.craftsmenlabs.gareth.execution.security

import org.springframework.context.annotation.Profile
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
@Profile("test")
open class HardcodedUserDetailsService : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails? {
        if (userName == "user") {
            return User("user", "secret", true, true, true, true,
                    AuthorityUtils.createAuthorityList("user"))
        }
        return null
    }
}


