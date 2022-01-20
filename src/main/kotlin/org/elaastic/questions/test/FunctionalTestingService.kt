package org.elaastic.questions.test

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserRepository
import org.elaastic.questions.player.PlayerController
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.test.interpreter.command.Command
import org.elaastic.questions.test.interpreter.command.SubmitExclusiveChoiceResponse
import org.elaastic.questions.test.interpreter.command.SubmitMultipleChoiceResponse
import org.elaastic.questions.test.interpreter.command.SubmitOpenResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.random.Random

@Service
@Transactional
class FunctionalTestingService(
    @Autowired val subjectService: SubjectService,
    @Autowired val sequenceService: SequenceService,
    @Autowired val assignmentService: AssignmentService,
    @Autowired val userRepository: UserRepository,
) {

    fun generateSubject(user: User): Subject {
        // Subject
        val subject = subjectService.save(
            Subject(
                "Test subject ${LocalDate.now()}",
                user
            )
        )

        // Questions
        subjectService.addStatement(
            subject,
            Statement(
                title = "Question 1 - Open ended",
                owner = user,
                questionType = QuestionType.OpenEnded,
                content = "An open question for testing purpose",
                rank = 0,
                expectedExplanation = "Expected explanation for question 1"
            )
        )

        subjectService.addStatement(
            subject,
            Statement(
                title = "Question 2 - Exclusive choice",
                owner = user,
                questionType = QuestionType.ExclusiveChoice,
                content = "An exclusive choice question for testing purpose",
                choiceSpecification = ExclusiveChoiceSpecification(
                    nbCandidateItem = 4,
                    expectedChoice = ChoiceItem(3, 100f)
                ),
                rank = 1,
                expectedExplanation = "Expected explanation for question 2"
            )
        )

        subjectService.addStatement(
            subject,
            Statement(
                title = "Question 3 - Multiple choice",
                owner = user,
                questionType = QuestionType.MultipleChoice,
                choiceSpecification = MultipleChoiceSpecification(
                    nbCandidateItem = 3,
                    expectedChoiceList = listOf(ChoiceItem(1, 50f), ChoiceItem(2, 50f))
                ),
                content = "A multiple choice question for testing purpose",
                rank = 2,
                expectedExplanation = "Expected explanation for question 3"
            )
        )


        // Assignments
        listOf("Face-to-face", "Blended", "Distant").forEach {
            subjectService.addAssignment(
                subject,
                Assignment(
                    owner = user,
                    title = "$it Test Assignment",
                )
            )
        }

        return subject
    }

    fun submitOpenResponse(
        user: User,
        sequenceId: Long,
        confidenceDegree: ConfidenceDegree,
        explanation: String? = null
    ) {
        val sequence = sequenceService.get(sequenceId, true)
        registerUserIfNeeded(user, sequence)

        val interaction = sequenceService.getActiveInteractionForLearner(sequence, user)
        if (interaction == null || !interaction.isResponseSubmission()) {
            error("The active interaction for this sequence and this learner is not a response submission")
        }

        sequenceService.submitResponse(
            user,
            sequence,
            PlayerController.ResponseSubmissionData(
                interactionId = interaction.id!!,
                attempt = 1,
                choiceList = null,
                confidenceDegree = confidenceDegree,
                explanation = explanation ?: "Random explanation on ${LocalDate.now()} by ${user.username}",
            )
        )
    }

    fun submitExclusiveChoiceResponse(
        user: User,
        sequenceId: Long,
        correct: Boolean,
        confidenceDegree: ConfidenceDegree,
        explanation: String? = null
    ) {
        val sequence = sequenceService.get(sequenceId, true)
        registerUserIfNeeded(user, sequence)

        val interaction = sequenceService.getActiveInteractionForLearner(sequence, user)
        if (interaction == null || !interaction.isResponseSubmission()) {
            error("The active interaction for this sequence and this learner is not a response submission")
        }

        if (!sequence.statement.isExclusiveChoice()) {
            error("This statement is not an exclusive choice... cannot submit exclusive answer")
        }
        val choiceSpecification = sequence.statement.choiceSpecification as ExclusiveChoiceSpecification
        val userChoice = if (correct) {
            choiceSpecification.expectedChoice.index
        } else {
            (0 until choiceSpecification.nbCandidateItem - 1)
                .filter {
                    it != choiceSpecification.expectedChoice.index
                }[Random.nextInt(choiceSpecification.nbCandidateItem - 1)]
        }

        sequenceService.submitResponse(
            user,
            sequence,
            PlayerController.ResponseSubmissionData(
                interactionId = interaction.id!!,
                attempt = 1,
                choiceList = listOf(userChoice),
                confidenceDegree = confidenceDegree,
                explanation = explanation ?: "Random explanation on ${LocalDate.now()} by ${user.username}",
            )
        )
    }

    fun submitMultipleChoiceResponse(
        user: User,
        sequenceId: Long,
        correct: Boolean,
        confidenceDegree: ConfidenceDegree,
        explanation: String? = null
    ) {
        val sequence = sequenceService.get(sequenceId, true)
        registerUserIfNeeded(user, sequence)

        val interaction = sequenceService.getActiveInteractionForLearner(sequence, user)
        if (interaction == null || !interaction.isResponseSubmission()) {
            error("The active interaction for this sequence and this learner is not a response submission")
        }

        if (!sequence.statement.isMultipleChoice()) {
            error("This statement is not an exclusive choice... cannot submit exclusive answer")
        }
        val choiceSpecification = sequence.statement.choiceSpecification as MultipleChoiceSpecification
        val userChoice = if (correct) {
            choiceSpecification.expectedChoiceList.map { it.index }
        } else {
            var candidates: List<Int>
            do {
                candidates = (1 until choiceSpecification.nbCandidateItem).toList().filter {
                    Random.nextInt(100) < 40
                }
            } while (candidates == choiceSpecification.expectedChoiceList.map { it.index })

            candidates
        }

        sequenceService.submitResponse(
            user,
            sequence,
            PlayerController.ResponseSubmissionData(
                interactionId = interaction.id!!,
                attempt = 1,
                choiceList = userChoice,
                confidenceDegree = confidenceDegree,
                explanation = explanation ?: "Random explanation on ${LocalDate.now()} by ${user.username}",
            )
        )
    }

    fun executeScript(sequenceId: Long, script: List<Command>) {
        script.forEach { command ->
            when (command) {
                is SubmitOpenResponse -> submitOpenResponse(
                    userRepository.getByUsername(command.username),
                    sequenceId,
                    confidenceDegree = command.confidenceDegree,
                    explanation = command.explanation
                )

                is SubmitExclusiveChoiceResponse -> submitExclusiveChoiceResponse(
                    userRepository.getByUsername(command.username),
                    sequenceId,
                    correct = command.correct,
                    confidenceDegree = command.confidenceDegree,
                    explanation = command.explanation
                )

                is SubmitMultipleChoiceResponse -> submitMultipleChoiceResponse(
                    userRepository.getByUsername(command.username),
                    sequenceId,
                    correct = command.correct,
                    confidenceDegree = command.confidenceDegree,
                    explanation = command.explanation
                )

                else -> error("Unsupported command")
            }

        }
    }

    private fun registerUserIfNeeded(user: User, sequence: Sequence) {
        val assignment = sequence.assignment!!

        // Register on the fly if needed
        if (!assignmentService.userIsRegisteredInAssignment(user, assignment)) {
            assignmentService.registerUser(user, assignment)
        }
    }
}