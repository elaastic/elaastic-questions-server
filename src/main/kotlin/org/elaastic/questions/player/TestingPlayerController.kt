package org.elaastic.questions.player

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceGenerator
import org.elaastic.questions.assignment.sequence.interaction.*
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.components.sequenceInfo.SequenceInfoModel
import org.elaastic.questions.player.components.sequenceInfo.SequenceInfoResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal

@Controller
@RequestMapping("/player/test")
class TestingPlayerController(
        @Autowired
        val messageBuilder: MessageBuilder
) {


    @GetMapping("/steps")
    fun testSteps(authentication: Authentication,
                  model: Model,
                  @RequestParam responseSubmissionState: String?,
                  @RequestParam evaluationState: String?,
                  @RequestParam readState: String?,
                  @RequestParam showStatistics: Boolean?,
                  @RequestParam studentsProvideExplanation: Boolean?): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute("responseSubmissionState", responseSubmissionState ?: "completed")
        model.addAttribute("evaluationState", evaluationState ?: "active")
        model.addAttribute("readState", readState ?: "disabled")
        model.addAttribute("showStatistics", showStatistics ?: false)
        model.addAttribute("sequenceStatistics", SequenceStatistics)
        model.addAttribute("studentsProvideExplanation", studentsProvideExplanation ?: true)


        return "/player/assignment/sequence/components/test-steps"
    }

    object SequenceStatistics {
        val nbResponsesAttempt1: Int = 10
        val nbResponsesAttempt2: Int = 8
        val nbEvaluations: Int = 5
    }

    @GetMapping("/explanation-viewer")
    fun testExplanationViewer(authentication: Authentication,
                              model: Model): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
                "explanationViewerSituationArray",
                with(ExplanationViewerCmd) {
                    arrayOf(
                            choiceQuestionSituation {
                                sequenceId = 1
                                description = "1. Question à choix, cas général"
                                explanationsByResponse {
                                    response(listOf(1), 100, true) {
                                        explanation {
                                            nbEvaluations = 1
                                            meanGrade = BigDecimal(3)
                                            author = "Joe"
                                            content = "Explication B"
                                        }
                                        explanation {
                                            nbEvaluations = 4
                                            meanGrade = BigDecimal(5)
                                            author = "Bob"
                                            content = "Explication C"
                                        }
                                        explanation {
                                            nbEvaluations = 0
                                            author = "Arthur"
                                            content = "Explication D"
                                        }
                                    }
                                    response(listOf(2), 0, false) {
                                        explanation {
                                            nbEvaluations = 2
                                            meanGrade = BigDecimal(1.33)
                                            author = "Bill"
                                            content = "Explication A"
                                        }
                                    }
                                }
                            },
                            choiceQuestionSituation {
                                sequenceId = 2
                                description = "2. Aucune explication fournie"
                                explanationsByResponse {
                                    response(listOf(1), 100, true) {}
                                    response(listOf(2), 0, false) {}
                                }
                            },
                            choiceQuestionSituation {
                                sequenceId = 3
                                description = "3. Uniquement 2 explications correctes"
                                explanationsByResponse {
                                    response(listOf(1), 100, true) {
                                        explanation {
                                            nbEvaluations = 1
                                            meanGrade = BigDecimal(3)
                                            author = "Joe"
                                            content = "Explication B"
                                        }
                                        explanation {
                                            nbEvaluations = 4
                                            meanGrade = BigDecimal(5)
                                            author = "Bob"
                                            content = "Explication C"
                                        }
                                    }
                                }
                            },
                            choiceQuestionSituation {
                                sequenceId = 4
                                description = "4. Seulement des explications incorrectes"
                                explanationsByResponse {
                                    response(listOf(1), 100, true) {}
                                    response(listOf(2), 0, false) {
                                        explanation {
                                            nbEvaluations = 1
                                            meanGrade = BigDecimal(3)
                                            author = "Joe"
                                            content = "Explication B"
                                        }
                                        explanation {
                                            nbEvaluations = 4
                                            meanGrade = BigDecimal(5)
                                            author = "Bob"
                                            content = "Explication C"
                                        }
                                    }
                                }
                            },
                            openQuestionSituation {
                                sequenceId = 5
                                description = "5. Plusieurs explications pour une question ouverte"
                                explanations {
                                    explanation {
                                        nbEvaluations = 2
                                        meanGrade = BigDecimal(1.33)
                                        author = "Bill"
                                        content = "Explication A"
                                    }
                                    explanation {
                                        nbEvaluations = 1
                                        meanGrade = BigDecimal(3)
                                        author = "Joe"
                                        content = "Explication B"
                                    }
                                    explanation {
                                        nbEvaluations = 4
                                        meanGrade = BigDecimal(5)
                                        author = "Bob"
                                        content = "Explication C"
                                    }
                                    explanation {
                                        nbEvaluations = 0
                                        author = "Arthur"
                                        content = "Explication D"
                                    }
                                }
                            },
                            choiceQuestionSituation {
                                sequenceId = 6
                                description = "6. Question à choix multiples"
                                explanationsByResponse {
                                    response(listOf(1, 3), 100, true) {
                                        explanation {
                                            nbEvaluations = 1
                                            meanGrade = BigDecimal(3)
                                            author = "Joe"
                                            content = "Explication B"
                                        }
                                        explanation {
                                            nbEvaluations = 4
                                            meanGrade = BigDecimal(5)
                                            author = "Bob"
                                            content = "Explication C"
                                        }
                                        explanation {
                                            nbEvaluations = 0
                                            author = "Arthur"
                                            content = "Explication D"
                                        }
                                    }
                                    response(listOf(1, 2), 30, false) {
                                        explanation {
                                            nbEvaluations = 2
                                            meanGrade = BigDecimal(1.33)
                                            author = "Bill"
                                            content = "Explication A"
                                        }
                                    }
                                    response(listOf(3), 0, false) {
                                        explanation {
                                            nbEvaluations = 2
                                            meanGrade = BigDecimal(1.33)
                                            author = "Bill"
                                            content = "Explication E"
                                        }
                                        explanation {
                                            nbEvaluations = 0
                                            meanGrade = null
                                            author = "Bill"
                                            content = "Explication F"
                                        }
                                        explanation {
                                            nbEvaluations = 1
                                            meanGrade = BigDecimal(5)
                                            author = "Bill"
                                            content = "Explication G"
                                        }
                                        explanation {
                                            nbEvaluations = 4
                                            meanGrade = BigDecimal(2)
                                            author = "Bill"
                                            content = "Explication H"
                                        }
                                    }
                                }
                            }
                    )
                }
        )

        return "/player/assignment/sequence/components/test-explanation-viewer"
    }

    class ExplanationViewerSituation(
            val description: String,
            val sequenceId: Long,
            val explanationViewerModel: PlayerController.ExplanationViewerModel
    )

    @GetMapping("/response-distribution-chart")
    fun testResponseDistributionChat(authentication: Authentication,
                                     model: Model): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
                "responseDistributionChartSituations",
                listOf(
                        ResponseDistributionChartSituation(
                                description = "1 seule tentative, 2 items, pas de sans réponse",
                                model = ResponseDistributionChartModel(
                                        interactionId = 12,
                                        choiceSpecification = ChoiceSpecificationData(
                                                2,
                                                listOf(1)
                                        ),
                                        results = InteractionResult(
                                                ResultOfGroupOnAttempt(10, listOf(7, 3))
                                        ).toLegacyFormat()
                                )

                        ),
                        ResponseDistributionChartSituation(
                                description = "1 seule tentative, 4 items, avec sans réponse",
                                model = ResponseDistributionChartModel(
                                        interactionId = 14,
                                        choiceSpecification = ChoiceSpecificationData(
                                                4,
                                                listOf(3)
                                        ),
                                        results = InteractionResult(
                                                ResultOfGroupOnAttempt(10, listOf(1, 0, 5, 2), 2)
                                        ).toLegacyFormat()
                                )

                        ),
                        ResponseDistributionChartSituation(
                                description = "1 seule tentative, 10 items, avec sans réponse",
                                model = ResponseDistributionChartModel(
                                        interactionId = 110,
                                        choiceSpecification = ChoiceSpecificationData(
                                                10,
                                                listOf(7)
                                        ),
                                        results = InteractionResult(
                                                ResultOfGroupOnAttempt(60, listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 5)
                                        ).toLegacyFormat()
                                )

                        ),
                        ResponseDistributionChartSituation(
                                description = "2 tentatives, 2 items, pas de sans réponse",
                                model = ResponseDistributionChartModel(
                                        interactionId = 22,
                                        choiceSpecification = ChoiceSpecificationData(
                                                2,
                                                listOf(1)
                                        ),
                                        results = InteractionResult(
                                                ResultOfGroupOnAttempt(10, listOf(7, 3)),
                                                ResultOfGroupOnAttempt(10, listOf(9, 1))
                                        ).toLegacyFormat()
                                )

                        ),
                        ResponseDistributionChartSituation(
                                description = "2 tentatives, 4 items, avec sans réponse à la 1ère tentative",
                                model = ResponseDistributionChartModel(
                                        interactionId = 241,
                                        choiceSpecification = ChoiceSpecificationData(
                                                4,
                                                listOf(3)
                                        ),
                                        results = InteractionResult(
                                                ResultOfGroupOnAttempt(10, listOf(1, 0, 5, 2), 2),
                                                ResultOfGroupOnAttempt(10, listOf(1, 2, 1, 0), 0)
                                        ).toLegacyFormat()
                                )

                        ),
                        ResponseDistributionChartSituation(
                                description = "2 tentatives, 4 items, avec sans réponse à la 2ème tentative",
                                model = ResponseDistributionChartModel(
                                        interactionId = 242,
                                        choiceSpecification = ChoiceSpecificationData(
                                                4,
                                                listOf(3)
                                        ),
                                        results = InteractionResult(
                                                ResultOfGroupOnAttempt(10, listOf(1, 2, 5, 2), 0),
                                                ResultOfGroupOnAttempt(10, listOf(1, 0, 1, 0), 2)
                                        ).toLegacyFormat()
                                )

                        ),
                        ResponseDistributionChartSituation(
                                description = "2 tentatives, 4 items, avec sans réponse aux 2 tentatives",
                                model = ResponseDistributionChartModel(
                                        interactionId = 243,
                                        choiceSpecification = ChoiceSpecificationData(
                                                4,
                                                listOf(3)
                                        ),
                                        results = InteractionResult(
                                                ResultOfGroupOnAttempt(10, listOf(1, 1, 5, 1), 2),
                                                ResultOfGroupOnAttempt(10, listOf(1, 0, 1, 0), 2)
                                        ).toLegacyFormat()
                                )

                        ),
                        ResponseDistributionChartSituation(
                                description = "2 tentatives, 10 items, avec sans réponse",
                                model = ResponseDistributionChartModel(
                                        interactionId = 210,
                                        choiceSpecification = ChoiceSpecificationData(
                                                10,
                                                listOf(7)
                                        ),
                                        results = InteractionResult(
                                                ResultOfGroupOnAttempt(60, listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 5),
                                                ResultOfGroupOnAttempt(60, listOf(10, 9, 8, 7, 6, 5, 4, 3, 2, 1), 5)
                                        ).toLegacyFormat()
                                )

                        ),
                        ResponseDistributionChartSituation(
                                description = "Choix multiples",
                                model = ResponseDistributionChartModel(
                                        interactionId = 999,
                                        choiceSpecification = ChoiceSpecificationData(
                                                4,
                                                listOf(1, 3)
                                        ),
                                        results = InteractionResult(
                                                ResultOfGroupOnAttempt(4, listOf(2, 1, 1, 1), 2),
                                                ResultOfGroupOnAttempt(4, listOf(2, 1, 3, 1), 0)
                                        ).toLegacyFormat()
                                )

                        )
                )

        )

        return "/player/assignment/sequence/components/test-response-distribution-chart"
    }

    data class ResponseDistributionChartSituation(
            val description: String,
            val model: ResponseDistributionChartModel
    )

    data class ResponseDistributionChartModel(
            val interactionId: Long,
            val choiceSpecification: ChoiceSpecificationData,
            val results: Map<AttemptNum, Map<ItemIndex, ResponsePercentage>>
    )

    data class ChoiceSpecificationData(
            val itemCount: Int,
            val expectedChoiceList: List<Int>
    )

    @GetMapping("/statement")
    fun testStatement(authentication: Authentication,
                      model: Model,
                      @RequestParam panelClosed: Boolean?,
                      @RequestParam hideQuestionType: Boolean?,
                      @RequestParam hideStatement: Boolean?): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
                "statementPanelModel",
                StatementPanelModel(
                        panelClosed = panelClosed ?: false,
                        hideQuestionType = hideQuestionType ?: false,
                        hideStatement = hideStatement ?: false

                )
        )
        model.addAttribute(
                "statement",
                StatementInfo(
                        "Énoncé de test",
                        QuestionType.ExclusiveChoice,
                        "Le <strong>contenu</strong> de cet énoncé de test."
                )
        )

        return "/player/assignment/sequence/components/test-statement"
    }

    data class StatementPanelModel(
            val panelClosed: Boolean = false,
            val hideQuestionType: Boolean = false,
            val hideStatement: Boolean = false
    )

    data class StatementInfo(
            val title: String,
            val questionType: QuestionType,
            val content: String
    ) {
        constructor(statement: Statement) :
                this(
                        statement.title,
                        statement.questionType,
                        statement.content
                )
    }

    @GetMapping("/results")
    fun testResults(authentication: Authentication,
                    model: Model): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)

        model.addAttribute(
                "resultsSituations",
                listOf(
                        ResultsSituation(
                                description = "1. Sequence running, hasChoices, no results, no explanations",
                                resultsModel = ChoiceResultsModel(
                                        sequenceIsStopped = false,
                                        sequenceId = 1,
                                        interactionId = 1,
                                        interactionRank = 2,
                                        hasAnyResult = false,
                                        hasExplanations = false
                                )
                        ),
                        ResultsSituation(
                                description = "2. Sequence stopped, hasChoices, no results, no explanations",
                                resultsModel = ChoiceResultsModel(
                                        sequenceIsStopped = true,
                                        sequenceId = 2,
                                        interactionId = 2,
                                        interactionRank = 2,
                                        hasAnyResult = false,
                                        hasExplanations = false
                                )
                        ),
                        ResultsSituation(
                                description = "3. Sequence running, Open question, no results, no explanations",
                                resultsModel = ChoiceResultsModel(
                                        sequenceIsStopped = false,
                                        sequenceId = 3,
                                        interactionId = 3,
                                        interactionRank = 2,
                                        hasAnyResult = false,
                                        hasExplanations = false
                                )
                        ),
                        ResultsSituation(
                                description = "4. Sequence stopped, Open question, no results, no explanations",
                                resultsModel = OpenResultsModel(
                                        sequenceIsStopped = true,
                                        sequenceId = 4,
                                        interactionId = 4,
                                        interactionRank = 2,
                                        hasExplanations = false
                                )
                        ),
                        ResultsSituation(
                                description = "5. Sequence running, hasChoices, has results, no explanations",
                                resultsModel = ChoiceResultsModel(
                                        sequenceIsStopped = false,
                                        sequenceId = 5,
                                        interactionId = 5,
                                        interactionRank = 2,
                                        hasAnyResult = true,
                                        responseDistributionChartModel = ResponseDistributionChartModel(
                                                interactionId = 5,
                                                choiceSpecification = ChoiceSpecificationData(
                                                        2,
                                                        listOf(2)
                                                ),
                                                results = InteractionResult(
                                                        ResultOfGroupOnAttempt(4, listOf(1, 3), 0)
                                                ).toLegacyFormat()
                                        ),
                                        hasExplanations = false
                                )
                        ),
                        ResultsSituation(
                                description = "6. Sequence running, hasChoices, has results, has explanations",
                                resultsModel = ChoiceResultsModel(
                                        sequenceIsStopped = false,
                                        sequenceId = 6,
                                        interactionId = 6,
                                        interactionRank = 2,
                                        hasAnyResult = true,
                                        responseDistributionChartModel = ResponseDistributionChartModel(
                                                interactionId = 6,
                                                choiceSpecification = ChoiceSpecificationData(
                                                        2,
                                                        listOf(2)
                                                ),
                                                results = InteractionResult(
                                                        ResultOfGroupOnAttempt(4, listOf(1, 3), 0)
                                                ).toLegacyFormat()
                                        ),
                                        hasExplanations = true,
                                        explanationViewerModel = PlayerController.ChoiceExplanationViewerModel(
                                                explanationsByResponse = mapOf(
                                                        PlayerController.ResponseData(
                                                                listOf(1),
                                                                0,
                                                                false
                                                        )
                                                                to listOf(
                                                                PlayerController.ExplanationData(
                                                                        "explication 1",
                                                                        "Joe"
                                                                ),
                                                                PlayerController.ExplanationData(
                                                                        "explication 2",
                                                                        "Jack"
                                                                )
                                                        ),
                                                        PlayerController.ResponseData(
                                                                listOf(2),
                                                                100,
                                                                true
                                                        )
                                                                to listOf(
                                                                PlayerController.ExplanationData(
                                                                        "explication 3",
                                                                        "William"
                                                                ),
                                                                PlayerController.ExplanationData(
                                                                        "explication 4",
                                                                        "Averell"
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ),
                        ResultsSituation(
                                description = "7. Sequence running, Open question, has explanations",
                                resultsModel = OpenResultsModel(
                                        sequenceIsStopped = false,
                                        sequenceId = 7,
                                        interactionId = 7,
                                        interactionRank = 2,
                                        hasExplanations = true,
                                        explanationViewerModel = PlayerController.OpenExplanationViewerModel(
                                                explanations = listOf(
                                                        PlayerController.ExplanationData(
                                                                "explication 1",
                                                                "Joe"
                                                        ),
                                                        PlayerController.ExplanationData(
                                                                "explication 2",
                                                                "Jack"
                                                        ),

                                                        PlayerController.ExplanationData(
                                                                "explication 3",
                                                                "William"
                                                        ),
                                                        PlayerController.ExplanationData(
                                                                "explication 4",
                                                                "Averell"
                                                        )
                                                )
                                        )
                                )
                        )
                )
        )

        return "/player/assignment/sequence/components/test-results"
    }

    data class ResultsSituation(
            val description: String,
            val resultsModel: ResultsModel
    )

    interface ResultsModel {
        val sequenceIsStopped: Boolean
        val sequenceId: Long
        val interactionId: Long
        val interactionRank: Int
        fun getHasChoices(): Boolean
        val hasExplanations: Boolean
        val explanationViewerModel: PlayerController.ExplanationViewerModel?
    }

    data class ChoiceResultsModel(
            override val sequenceIsStopped: Boolean,
            override val sequenceId: Long,
            override val interactionId: Long,
            override val interactionRank: Int,
            val hasAnyResult: Boolean,
            val responseDistributionChartModel: ResponseDistributionChartModel? = null,
            override val hasExplanations: Boolean,
            override val explanationViewerModel: PlayerController.ExplanationViewerModel? = null
    ) : ResultsModel {
        override fun getHasChoices() = true
    }

    data class OpenResultsModel(
            override val sequenceIsStopped: Boolean,
            override val sequenceId: Long,
            override val interactionId: Long,
            override val interactionRank: Int,
            override val hasExplanations: Boolean,
            override val explanationViewerModel: PlayerController.ExplanationViewerModel? = null
    ) : ResultsModel {
        override fun getHasChoices() = false
    }

    @GetMapping("/sequence-info")
    fun testSequenceInfo(authentication: Authentication,
                         model: Model): String {

        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
                "sequenceInfoSituations",
                SequenceGenerator.generateAllTypes(user)
                        .map {
                            SequenceInfoSituation(
                                    describeSequence(it),
                                    SequenceInfoResolver.resolve(it, messageBuilder)!!
                            )
                        }
        )

        return "/player/assignment/sequence/components/test-sequence-info"
    }

    data class SequenceInfoSituation(
            val description: String,
            val model: SequenceInfoModel
    )

    private fun describeSequence(sequence: Sequence): String =
            "sequenceState=${sequence.state}, " +
                    "executionContext=${sequence.executionContext}, " +
                    "resultsArePublished=${sequence.resultsArePublished}" +

                    (sequence?.activeInteraction?.let {
                        ", interaction=(${it.interactionType},${it.state} )"
                    } ?: "")


}