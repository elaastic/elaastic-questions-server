package org.elaastic.questions.assignment.sequence.interaction.response

import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.springframework.data.jpa.repository.JpaRepository


interface ResponseRepository : JpaRepository<Response, Long> {

    fun findAllByInteraction(interaction: Interaction): List<Response>
}