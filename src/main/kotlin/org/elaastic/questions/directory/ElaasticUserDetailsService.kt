package org.elaastic.questions.directory

import org.elaastic.questions.security.ElaasticPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * @author John Tranier
 */
@Service
class ElaasticUserDetailsService(
        @Autowired val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        userRepository.findByUsername(username)?.let {
            return ElaasticPrincipal(it)
        } ?: throw UsernameNotFoundException(username)
    }

}