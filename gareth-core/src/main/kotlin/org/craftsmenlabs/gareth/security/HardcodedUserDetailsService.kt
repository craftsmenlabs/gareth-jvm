package org.craftsmenlabs.gareth.security

import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
open class HardcodedUserDetailsService : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails? {
        if (userName == "user") {
            return User("user", "secret", true, true, true, true,
                    AuthorityUtils.createAuthorityList("user"))
        }
        return null
    }
}


