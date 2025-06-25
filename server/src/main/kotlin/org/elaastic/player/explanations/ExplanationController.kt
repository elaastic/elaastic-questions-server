package org.elaastic.player.explanations

import org.elaastic.player.sequence.SequenceModelFactory
import org.elaastic.sequence.LearnerSequenceService
import org.elaastic.sequence.SequenceService
import org.elaastic.sequence.phase.LearnerPhaseService
import org.elaastic.sequence.phase.LearnerPhaseType
import org.elaastic.sequence.phase.descriptor.SequenceDescriptor
import org.elaastic.sequence.phase.result.LearnerResultPhaseViewModel
import org.elaastic.user.PrincipalUserResolver
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ExplanationController(
    private val sequenceService: SequenceService,
    private val sequenceModelFactory: SequenceModelFactory,
    private val learnerSequenceService: LearnerSequenceService,
    private val learnerPhaseService: LearnerPhaseService,
    private val sequenceDescriptor: SequenceDescriptor
) {
    @GetMapping("/all-explanations/{sequenceId}/modal")
    fun allExplanations(
        authentication: Authentication,
        model: Model,
        @PathVariable sequenceId: Long,
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        val sequence = sequenceService.get(sequenceId)
        val isTeacher = sequence.owner == user

        val explanationViewerModel = if (isTeacher) {
            sequenceModelFactory.buildForTeacher(user, sequence).resultsModel!!.explanationViewerModel
        } else {
            val learnerSequence = learnerSequenceService.getLearnerSequence(
                user,
                sequenceService.loadInteractions(sequence)
            )

            (learnerPhaseService.buildPhase(
                learnerSequence,
                sequenceDescriptor.phaseDescriptorList.find { it.type == LearnerPhaseType.RESULT }!!,
                1,
                active = true
            ).getViewModel() as LearnerResultPhaseViewModel).sequenceResultsModel.explanationViewerModel
        }

        model["sequenceId"] = sequenceId
        model["explanationViewerModel"] = explanationViewerModel!!
        model["isTeacher"] = isTeacher
        return "player/assignment/sequence/components/explanation-viewer/_all-explanations-modal.html :: allExplanationsPopup"
    }
}