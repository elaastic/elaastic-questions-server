package org.elaastic.questions

import org.springframework.data.repository.CrudRepository

/**
 * @author John Tranier
 */
interface UserRepository : CrudRepository<User, Long> {
    
}