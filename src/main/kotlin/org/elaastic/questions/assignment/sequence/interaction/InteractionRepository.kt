package org.elaastic.questions.assignment.sequence.interaction

import org.springframework.data.jpa.repository.JpaRepository


interface InteractionRepository : JpaRepository<Interaction, Long>