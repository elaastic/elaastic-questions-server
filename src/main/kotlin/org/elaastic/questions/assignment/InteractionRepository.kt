package org.elaastic.questions.assignment

import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author John Tranier
 */
interface InteractionRepository : JpaRepository<Interaction, Long> {
}