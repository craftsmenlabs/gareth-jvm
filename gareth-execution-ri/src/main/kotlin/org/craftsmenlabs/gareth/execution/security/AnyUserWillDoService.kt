package org.craftsmenlabs.gareth.execution.security

import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
open class AnyUserWillDoService : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails? {
        return User(userName, userName, true, true, true, true,
                AuthorityUtils.createAuthorityList("user"))
    }
}


