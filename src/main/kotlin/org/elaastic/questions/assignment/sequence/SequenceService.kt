package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanation
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanationRepository
import org.elaastic.questions.assignment.sequence.interaction.InteractionRepository
import org.elaastic.questions.assignment.sequence.interaction.InteractionService
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.assignment.sequence.interaction.specification.EvaluationSpecification
import org.elaastic.questions.assignment.sequence.interaction.specification.ReadSpecification
import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
@Transactional
class SequenceService(
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val fakeExplanationRepository: FakeExplanationRepository,
        @Autowired val interactionService: InteractionService,
        @Autowired val interactionRepository: InteractionRepository
) {
    fun get(user: User, id: Long, fetchInteractions: Boolean = false): Sequence { // TODO Test
        return sequenceRepository.findOneById(id)?.let { sequence ->
            if (sequence.owner != user) throw AccessDeniedException("You are not autorized to access to this sequence")

            if (fetchInteractions) {
                interactionRepository.findAllBySequence(sequence).map {
                    sequence.interactions[it.interactionType] = it
                }
            }

            sequence
        } ?: throw EntityNotFoundException("There is no sequence for id \"$id\"")
    }

    fun findAllFakeExplanation(user: User, sequenceId: Long): List<FakeExplanation> {
        return fakeExplanationRepository.findAllByStatement(
                get(user, sequenceId).statement
        )
    }

    fun start(user: User,
              sequence: Sequence,
              executionContext: ExecutionContext,
              studentsProvideExplanation: Boolean,
              nbResponseToEvaluate: Int): Sequence {

        require(user == sequence.owner) {
            "Only the owner of a sequence is allowed to start it"
        }

        require(sequence.interactions.isEmpty()) {
            "The sequence has already been started"
        }

        sequence.interactions[InteractionType.ResponseSubmission] =
                interactionService.create(
                        sequence,
                        ResponseSubmissionSpecification(
                                studentsProvideExplanation,
                                studentsProvideConfidenceDegree = studentsProvideExplanation
                        ),
                        1,
                        State.show
                )

        sequence.interactions[InteractionType.Evaluation] =
                interactionService.create(
                        sequence,
                        EvaluationSpecification(
                                nbResponseToEvaluate
                        ),
                        2,
                        if (executionContext == ExecutionContext.FaceToFace)
                            State.beforeStart
                        else State.show
                )

        sequence.interactions[InteractionType.Read] =
                interactionService.create(
                        sequence,
                        ReadSpecification(),
                        3,
                        when(executionContext) {
                            ExecutionContext.FaceToFace, ExecutionContext.Blended -> State.beforeStart
                            ExecutionContext.Distance -> State.show
                        }

                )

        if(executionContext == ExecutionContext.FaceToFace)
            sequence.selectActiveInteraction(InteractionType.ResponseSubmission)
        else sequence.selectActiveInteraction(InteractionType.Read)
        
        sequence.let {
            it.state = State.show
            it.executionContext = executionContext
            it.resultsArePublished = (executionContext == ExecutionContext.Distance)
            sequenceRepository.save(it)
        }

        // TODO Build teacher predefined answers

        return sequence
    }

    fun stop(user: User, sequence: Sequence): Sequence {
        require(user == sequence.owner) {
            "Only the owner of the sequence is allowed to stop it"
        }

        sequence.let {
            it.state = State.afterStop
            sequenceRepository.save(it)
            return it
        }
    }

    fun reopen(user: User, sequence: Sequence): Sequence {
        require(user == sequence.owner) {
            "Only the owner of the sequence is allowed to reopen it"
        }
        require(sequence.isStopped()) {
            "This sequence is not stopped, it cannot be reopen"
        }

        sequence.let {
            it.state = State.show
            sequenceRepository.save(it)
            return it
        }
    }
}