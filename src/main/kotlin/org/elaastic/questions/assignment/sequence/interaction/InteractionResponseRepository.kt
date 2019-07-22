package org.elaastic.questions.assignment.sequence.interaction

import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author John Tranier
 */
interface InteractionResponseRepository : JpaRepository<InteractionResponse, Long> {
}