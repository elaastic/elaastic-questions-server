package org.elaastic.questions.player.phase

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.LearnerSequenceService
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.descriptor.PhaseConfig
import org.elaastic.questions.player.phase.descriptor.SequenceDescriptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class LearnerPhaseService(
    @Autowired val learnerSequenceService: LearnerSequenceService,
    @Autowired val learnerPhaseFactoryResolver: LearnerPhaseFactoryResolver,
    @Autowired val sequenceDescriptor: SequenceDescriptor,
    @Autowired val ctx: ApplicationContext,
    ) {

    fun loadPhaseList(learnerSequence: ILearnerSequence) {
        // Get the active phase
        // TODO We use interaction now, but we should get this info from a Phase in the end...
        val activeInteractionForLearner = learnerSequenceService.getActiveInteractionForLearner(
            learnerSequence.learner,
            learnerSequence.sequence
        )

        sequenceDescriptor.phaseDescriptorList.forEachIndexed() { index, phaseDescriptor ->
            learnerSequence.loadPhase(
                getPhase(
                    learnerSequence,
                    phaseDescriptor.type,
                    index + 1,
                    active = activeInteractionForLearner?.rank == (index + 1),
                    phaseDescriptor.config
                )
            )
        }
    }

    fun getPhase(
        learnerSequence: ILearnerSequence,
        phaseType: PhaseType,
        phaseIndex: Int,
        active: Boolean,
        phaseConfig: PhaseConfig?
    ): LearnerPhase =
        run {

            val learnerPhase =
                learnerPhaseFactoryResolver
                    .resolve(phaseType)
                    .build(
                        learnerSequence,
                        phaseIndex = phaseIndex,
                        active = active,
                        state = if (learnerSequence.isNotStarted())
                            State.beforeStart
                        else learnerSequence.getInteractionAt(phaseIndex).state,
                        phaseConfig = phaseConfig
                    )

            if (learnerSequence.hasStarted()) {
                learnerPhase.loadPhaseExecution(
                    ctx.getBean<LearnerPhaseExecutionService>(
                        learnerPhase.phaseType.learnerPhaseExecutionServiceBeanName
                    ).build(learnerPhase)
                )
            }

            learnerPhase
        }

}