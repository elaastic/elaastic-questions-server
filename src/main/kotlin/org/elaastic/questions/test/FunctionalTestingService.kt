package org.elaastic.questions.test

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserRepository
import org.elaastic.questions.player.PlayerController
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.test.interpreter.command.*
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
    @Autowired val responseService: ResponseService,
    @Autowired val peerGradingService: PeerGradingService,
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

    fun startSequence(
        sequence: Sequence,
        executionContext: ExecutionContext,
        studentsProvideExplanation: Boolean,
        nbResponseToEvaluate: Int
    ) =
        sequenceService.start(
            sequence.owner,
            sequence,
            executionContext,
            studentsProvideExplanation,
            nbResponseToEvaluate
        )

    fun submitResponse(
        phase: Phase,
        user: User,
        sequence: Sequence,
        correct: Boolean,
        confidenceDegree: ConfidenceDegree,
        explanation: String? = null
    ) {
        registerUserIfNeeded(user, sequence)

        // Check preconditions depending on phase
        val interaction = sequenceService.getActiveInteractionForLearner(sequence, user)
        require(interaction != null) { "The interaction must not be null after registration" }

        when (phase) {
            Phase.PHASE_1 -> {
                require(interaction.isResponseSubmission()) {
                    "The active interaction for this sequence and this learner is not a response submission"
                }
                require(
                    !responseService.hasResponseForUser(
                        user,
                        sequence,
                        1
                    )
                ) { "This user has already submitted its 1st attempt " }
            }

            Phase.PHASE_2 -> {
                require(interaction.isEvaluation()) {
                    "The active interaction for this sequence and this learner is not evaluation"
                }
                require(sequence.isSecondAttemptAllowed()) { "This sequence does not allow a 2nd attempt" }
                require(
                    !responseService.hasResponseForUser(
                        user,
                        sequence,
                        2
                    )
                ) { "This user has already submitted its 2nd attempt " }
            }
        }

        sequenceService.submitResponse(
            user,
            sequence,
            PlayerController.ResponseSubmissionData(
                interactionId = interaction.id!!,
                attempt = phase.index,
                choiceList = generateChoiceResponse(sequence.statement.choiceSpecification, correct),
                confidenceDegree = confidenceDegree,
                explanation = explanation ?: "Random explanation on ${LocalDate.now()} by ${user.username}",
            )
        )


    }


    fun evaluate(
        user: User,
        sequence: Sequence,
        evaluationStrategy: EvaluationStrategy,
    ) {

        // Evaluate other responses
        responseService.findAllRecommandedResponsesForUser(
            sequence = sequence,
            attempt = sequence.whichAttemptEvaluate(),
            user = user
        ).forEach { response: Response ->
            peerGradingService.createOrUpdate(user, response, evaluationStrategy.evaluate(response))
        }
    }

    fun publishResults(sequence: Sequence) =
        sequenceService.publishResults(sequence.owner, sequence)

    private fun generateChoiceResponse(
        choiceSpecification: ChoiceSpecification?,
        correct: Boolean
    ): List<Int>? =
        when (choiceSpecification) {
            null -> null
            is ExclusiveChoiceSpecification -> generateExclusiveChoiceResponse(choiceSpecification, correct)
            is MultipleChoiceSpecification -> generateMultipleChoiceResponse(choiceSpecification, correct)
            else -> error("Hmm... That shouldn't happened")
        }


    /**
     * Generate a user choice depending on the question specification & the response must be correct or not
     */
    private fun generateExclusiveChoiceResponse(
        choiceSpecification: ExclusiveChoiceSpecification,
        correct: Boolean,
    ): List<Int> {
        return listOf(
            if (correct) {
                choiceSpecification.expectedChoice.index
            } else {
                (1 until choiceSpecification.nbCandidateItem + 1)
                    .filter {
                        it != choiceSpecification.expectedChoice.index
                    }[Random.nextInt(choiceSpecification.nbCandidateItem - 1)]
            }
        )
    }

    /**
     * Generate a user choice depending on the question specification & the response must be correct or not
     */
    private fun generateMultipleChoiceResponse(
        choiceSpecification: MultipleChoiceSpecification,
        correct: Boolean,
    ): List<Int> =
        if (correct) {
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

    fun executeScript(sequenceId: Long, script: List<Command>) {
        val sequence = sequenceService.get(sequenceId, true)

        script.forEach { command ->
            when (command) {
                is StartSequence -> startSequence(
                    sequence,
                    command.executionContext,
                    command.studentsProvideExplanation,
                    command.nbResponseToEvaluate
                )

                is SubmitResponse -> submitResponse(
                    phase = command.phase,
                    user = userRepository.getByUsername(command.username),
                    sequence = sequence,
                    correct = command.correct,
                    confidenceDegree = command.confidenceDegree,
                    explanation = command.explanation
                )

                is Evaluate -> evaluate(
                    userRepository.getByUsername(command.username),
                    sequence,
                    evaluationStrategy = command.strategy,
                )

                is PublishResults -> publishResults(sequence)

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