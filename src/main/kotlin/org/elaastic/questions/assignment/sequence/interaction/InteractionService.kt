package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.assignment.sequence.interaction.specification.InteractionSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class InteractionService(
        @Autowired val interactionRepository: InteractionRepository
) {

    fun create(sequence: Sequence,
               interactionSpecification: InteractionSpecification,
               rank: Int,
               state: State = State.beforeStart): Interaction =
            Interaction(
                    interactionType = interactionSpecification.getType(),
                    rank = rank,
                    specification = interactionSpecification,
                    owner = sequence.owner,
                    sequence = sequence,
                    state = state
            ).let(interactionRepository::save)
}