package org.elaastic.sequence.phase

import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.LearnerSequenceService
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.sequence.phase.descriptor.SequenceDescriptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

/** @see LearnerPhase */
@Service
class LearnerPhaseService(
    @Autowired val learnerSequenceService: LearnerSequenceService,
    @Autowired val learnerPhaseFactory: LearnerPhaseFactory,
    @Autowired val ctx: ApplicationContext,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun loadPhaseList(learnerSequence: ILearnerSequence) {
        val sequence = learnerSequence.sequence
        // Get the active phase
        // TODO We use interaction now, but we should get this info from a Phase in the end...
        val activeInteractionForLearner = learnerSequenceService.getActiveInteractionForLearner(
            learnerSequence.learner,
            sequence
        )

        if (sequence.interactions.isEmpty()) {
            logger.warn("This sequence (${sequence.id}), has no interactions defined.")
        }

        sequence.interactions.keys.forEachIndexed { index, interactionType ->
            learnerSequence.loadPhase(
                buildPhase(
                    learnerSequence,
                    interactionType,
                    index + 1,
                    active = activeInteractionForLearner?.rank == (index + 1),
                )
            )
        }
    }

    fun buildPhase(
        learnerSequence: ILearnerSequence,
        interactionType: InteractionType,
        phaseIndex: Int,
        active: Boolean,
    ): LearnerPhase {
        val learnerPhase =
            learnerPhaseFactory.build(
                interactionType,
                learnerSequence,
                phaseIndex = phaseIndex,
                active = active,
                state = if (learnerSequence.isNotStarted())
                    State.beforeStart
                else learnerSequence.sequence.getInteractionAt(phaseIndex).state,
            )

        if (learnerSequence.hasStarted() && learnerPhase.isVisible()) {
            learnerPhase.loadPhaseExecution(
                ctx.getBean<LearnerPhaseExecutionLoader>(
                    learnerPhase.getLearnerPhaseExecutionLoaderName()
                ).build(learnerPhase)
            )
        }

        return learnerPhase
    }

}