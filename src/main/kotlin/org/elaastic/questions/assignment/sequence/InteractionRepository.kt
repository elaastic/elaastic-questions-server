package org.elaastic.questions.assignment.sequence

import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author John Tranier
 */
interface InteractionRepository : JpaRepository<Interaction, Long> {
}