/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.player

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceGenerator
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistribution
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistributionOnAttempt
import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.components.command.CommandModel
import org.elaastic.questions.player.components.command.CommandModelFactory
import org.elaastic.questions.player.components.evaluationPhase.EvaluationPhaseModel
import org.elaastic.questions.player.components.explanationViewer.*
import org.elaastic.questions.player.components.responseDistributionChart.ChoiceSpecificationData
import org.elaastic.questions.player.components.responseDistributionChart.ResponseDistributionChartModel
import org.elaastic.questions.player.components.responsePhase.ResponseFormModel
import org.elaastic.questions.player.components.responsePhase.ResponsePhaseModel
import org.elaastic.questions.player.components.results.ChoiceResultsModel
import org.elaastic.questions.player.components.results.OpenResultsModel
import org.elaastic.questions.player.components.results.ResultsModel
import org.elaastic.questions.player.components.sequenceInfo.SequenceInfoModel
import org.elaastic.questions.player.components.sequenceInfo.SequenceInfoResolver
import org.elaastic.questions.player.components.statement.StatementInfo
import org.elaastic.questions.player.components.statement.StatementPanelModel
import org.elaastic.questions.player.components.steps.SequenceStatistics
import org.elaastic.questions.player.components.steps.StepsModel
import org.elaastic.questions.player.components.studentResults.LearnerExclusiveChoiceResults
import org.elaastic.questions.player.components.studentResults.LearnerMultipleChoiceResults
import org.elaastic.questions.player.components.studentResults.LearnerOpenResults
import org.elaastic.questions.player.components.studentResults.LearnerResultsModel
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
    fun testSteps(
        authentication: Authentication,
        model: Model,
        @RequestParam responseSubmissionState: StepsModel.PhaseState?,
        @RequestParam evaluationState: StepsModel.PhaseState?,
        @RequestParam readState: StepsModel.PhaseState?,
        @RequestParam showStatistics: Boolean?,
        @RequestParam studentsProvideExplanation: Boolean?
    ): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
            "stepsModel",
            StepsModel(
                responseSubmissionState = responseSubmissionState ?: StepsModel.PhaseState.COMPLETED,
                evaluationState = evaluationState ?: StepsModel.PhaseState.ACTIVE,
                readState = readState ?: StepsModel.PhaseState.DISABLED,
                showStatistics = showStatistics ?: false,
                studentsProvideExplanation = studentsProvideExplanation ?: true
            )
        )
        model.addAttribute("sequenceStatistics", SequenceStatistics(10, 8, 5))

        return "/player/assignment/sequence/components/test-steps"
    }

    @GetMapping("/explanation-viewer")
    fun testExplanationViewer(
        authentication: Authentication,
        model: Model
    ): String {
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
                                    author = "Joe Walson (@Jwal)"
                                    content = "Explication B"
                                }
                                explanation {
                                    nbEvaluations = 4
                                    meanGrade = BigDecimal(5)
                                    author = "Bob Hart (@Bhar)"
                                    content = "Explication C"
                                }
                                explanation {
                                    nbEvaluations = 0
                                    author = "Arthur Rodriguez (@Arod)"
                                    content = "Explication D"
                                }
                            }
                            response(listOf(2), 0, false) {
                                explanation {
                                    nbEvaluations = 2
                                    meanGrade = BigDecimal(1.33)
                                    author = "Bill Gates (@Bgat)"
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
                                    author = "Joe Walson (@Jwal)"
                                    content = "Explication B"
                                }
                                explanation {
                                    nbEvaluations = 4
                                    meanGrade = BigDecimal(5)
                                    author = "Bob Hart (@Bhar)"
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
                                    author = "Joe Walson (@Jwal)"
                                    content = "Explication B"
                                }
                                explanation {
                                    nbEvaluations = 4
                                    meanGrade = BigDecimal(5)
                                    author = "Bob Hart (@Bhar)"
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
                                author = "Bill Gates (@Bgat)"
                                content = "Explication A"
                            }
                            explanation {
                                nbEvaluations = 1
                                meanGrade = BigDecimal(3)
                                author = "Joe Walson (@Jwal)"
                                content = "Explication B"
                            }
                            explanation {
                                nbEvaluations = 4
                                meanGrade = BigDecimal(5)
                                author = "Bob Hart (@Bhar)"
                                content = "Explication C"
                            }
                            explanation {
                                nbEvaluations = 0
                                author = "Arthur Rodriguez (@Arod)"
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
                                    author = "Joe Walson (@Jwal)"
                                    content = "Explication B"
                                }
                                explanation {
                                    nbEvaluations = 4
                                    meanGrade = BigDecimal(5)
                                    author = "Bob Hart (@Bhar)"
                                    content = "Explication C"
                                }
                                explanation {
                                    nbEvaluations = 0
                                    author = "Arthur Rodriguez (@Arod)"
                                    content = "Explication D"
                                }
                            }
                            response(listOf(1, 2), 30, false) {
                                explanation {
                                    nbEvaluations = 2
                                    meanGrade = BigDecimal(1.33)
                                    author = "Bill Gates (@Bgat)"
                                    content = "Explication A"
                                }
                            }
                            response(listOf(3), 0, false) {
                                explanation {
                                    nbEvaluations = 2
                                    meanGrade = BigDecimal(1.33)
                                    author = "Bill Gates (@Bgat)"
                                    content = "Explication E"
                                }
                                explanation {
                                    nbEvaluations = 0
                                    meanGrade = null
                                    author = "Bill Gates (@Bgat)"
                                    content = "Explication F"
                                }
                                explanation {
                                    nbEvaluations = 1
                                    meanGrade = BigDecimal(5)
                                    author = "Bill Gates (@Bgat)"
                                    content = "Explication G"
                                }
                                explanation {
                                    nbEvaluations = 4
                                    meanGrade = BigDecimal(2)
                                    author = "Bill Gates (@Bgat)"
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
        val explanationViewerModel: ExplanationViewerModel
    )

    @GetMapping("/response-distribution-chart")
    fun testResponseDistributionChat(
        authentication: Authentication,
        model: Model
    ): String {
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
                        results = ResponsesDistribution(
                            ResponsesDistributionOnAttempt(10, arrayOf(7, 3), 0)
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
                        results = ResponsesDistribution(
                            ResponsesDistributionOnAttempt(10, arrayOf(1, 0, 5, 2), 2)
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
                        results = ResponsesDistribution(
                            ResponsesDistributionOnAttempt(60, arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
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
                        results = ResponsesDistribution(
                            ResponsesDistributionOnAttempt(10, arrayOf(7, 3)),
                            ResponsesDistributionOnAttempt(10, arrayOf(9, 1))
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
                        results = ResponsesDistribution(
                            ResponsesDistributionOnAttempt(10, arrayOf(1, 0, 5, 2), 2),
                            ResponsesDistributionOnAttempt(10, arrayOf(1, 2, 1, 0), 0)
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
                        results = ResponsesDistribution(
                            ResponsesDistributionOnAttempt(10, arrayOf(1, 2, 5, 2), 0),
                            ResponsesDistributionOnAttempt(10, arrayOf(1, 0, 1, 0), 2)
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
                        results = ResponsesDistribution(
                            ResponsesDistributionOnAttempt(10, arrayOf(1, 1, 5, 1), 2),
                            ResponsesDistributionOnAttempt(10, arrayOf(1, 0, 1, 0), 2)
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
                        results = ResponsesDistribution(
                            ResponsesDistributionOnAttempt(60, arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 5),
                            ResponsesDistributionOnAttempt(60, arrayOf(10, 9, 8, 7, 6, 5, 4, 3, 2, 1), 5)
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
                        results = ResponsesDistribution(
                            ResponsesDistributionOnAttempt(4, arrayOf(2, 1, 1, 1), 2),
                            ResponsesDistributionOnAttempt(4, arrayOf(2, 1, 3, 1), 0)
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

    @GetMapping("/statement")
    fun testStatement(
        authentication: Authentication,
        model: Model,
        @RequestParam panelClosed: Boolean?,
        @RequestParam hideQuestionType: Boolean?,
        @RequestParam hideStatement: Boolean?
    ): String {
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

    @GetMapping("/my-results")
    fun testMyResults(
        authentication: Authentication,
        model: Model
    ): String {
        val user: User = authentication.principal as User
        model.addAttribute("user", user)

        model.addAttribute(
            "myResultsSituations",
            listOf(
                MyResultsSituation(
                    description = "Choix exclusif, incorrect puis correct, avec explications",
                    learnerResultsModel = LearnerExclusiveChoiceResults(
                        explanationFirstTry = ExplanationData(content = "I was wrong"),
                        explanationSecondTry = ExplanationData(content = "And I've changed my mind"),
                        choiceFirstTry = LearnerChoice(listOf(2)),
                        choiceSecondTry = LearnerChoice(listOf(1)),
                        scoreFirstTry = 0,
                        scoreSecondTry = 100,
                        expectedChoice = ExclusiveChoiceSpecification(
                            nbCandidateItem = 4,
                            expectedChoice = ChoiceItem(1, 1.0f),
                        )
                    )
                ),
                MyResultsSituation(
                    description = "Choix exclusif, incorrect puis correct, sans explications",
                    learnerResultsModel = LearnerExclusiveChoiceResults(
                        explanationFirstTry = null,
                        explanationSecondTry = null,
                        choiceFirstTry = LearnerChoice(listOf(2)),
                        choiceSecondTry = LearnerChoice(listOf(1)),
                        scoreFirstTry = 0,
                        scoreSecondTry = 100,
                        expectedChoice = ExclusiveChoiceSpecification(
                            nbCandidateItem = 4,
                            expectedChoice = ChoiceItem(1, 1.0f),
                        )
                    )
                ),
                MyResultsSituation(
                    description = "Choix exclusif, réponses identiques, sans explications",
                    learnerResultsModel = LearnerExclusiveChoiceResults(
                        explanationFirstTry = null,
                        explanationSecondTry = null,
                        choiceFirstTry = LearnerChoice(listOf(2)),
                        choiceSecondTry = LearnerChoice(listOf(2)),
                        scoreFirstTry = 0,
                        scoreSecondTry = 0,
                        expectedChoice = ExclusiveChoiceSpecification(
                            nbCandidateItem = 4,
                            expectedChoice = ChoiceItem(1, 1.0f),
                        )
                    )
                ),
                MyResultsSituation(
                    description = "Choix multiple, résultats qui s'améliorent, avec explications",
                    learnerResultsModel = LearnerMultipleChoiceResults(
                        explanationFirstTry = ExplanationData(content = "so-so"),
                        explanationSecondTry = ExplanationData(content = "a bit better"),
                        choiceFirstTry = LearnerChoice(listOf(2, 4)),
                        choiceSecondTry = LearnerChoice(listOf(2, 3, 4)),
                        scoreFirstTry = 0,
                        scoreSecondTry = 50,
                        expectedChoice = MultipleChoiceSpecification(
                            nbCandidateItem = 4,
                            expectedChoiceList = listOf(
                                ChoiceItem(2, 0.5f),
                                ChoiceItem(3, 0.5f)
                            ),
                        )
                    )
                ),
                MyResultsSituation(
                    description = "Question ouverte - 2 explications",
                    learnerResultsModel = LearnerOpenResults(
                        explanationFirstTry = ExplanationData(content = "1st guess"),
                        explanationSecondTry = ExplanationData(content = "2nd guess"),
                    )
                ),
                MyResultsSituation(
                    description = "Question ouverte - 1 seule explication",
                    learnerResultsModel = LearnerOpenResults(
                        explanationFirstTry = ExplanationData(content = "Only one"),
                        explanationSecondTry = null
                    )
                ),
                MyResultsSituation(
                    description = "Question ouverte - 2 explications identiques",
                    learnerResultsModel = LearnerOpenResults(
                        explanationFirstTry = ExplanationData(content = "same explanation"),
                        explanationSecondTry = ExplanationData(content = "same explanation"),
                    )
                ),
                MyResultsSituation(
                    description = "Question ouverte - seulement la 2ème",
                    learnerResultsModel = LearnerOpenResults(
                        explanationFirstTry = null,
                        explanationSecondTry = ExplanationData(content = "Just the 2nd"),
                    )
                ),
                MyResultsSituation(
                    description = "Question ouverte - no explanations",
                    learnerResultsModel = LearnerOpenResults(
                        explanationFirstTry = null,
                        explanationSecondTry = null,
                    )
                )

            )
        )

        return "/player/assignment/sequence/components/test-my-results"
    }

    data class MyResultsSituation(
        val description: String,
        val learnerResultsModel: LearnerResultsModel
    )

    @GetMapping("/results")
    fun testResults(
        authentication: Authentication,
        model: Model
    ): String {
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
                        hasAnyResult = false,
                        hasExplanations = false
                    )
                ),
                ResultsSituation(
                    description = "2. Sequence stopped, hasChoices, no results, no explanations",
                    resultsModel = ChoiceResultsModel(
                        sequenceIsStopped = true,
                        sequenceId = 2,
                        hasAnyResult = false,
                        hasExplanations = false
                    )
                ),
                ResultsSituation(
                    description = "3. Sequence running, Open question, no results, no explanations",
                    resultsModel = ChoiceResultsModel(
                        sequenceIsStopped = false,
                        sequenceId = 3,
                        hasAnyResult = false,
                        hasExplanations = false
                    )
                ),
                ResultsSituation(
                    description = "4. Sequence stopped, Open question, no results, no explanations",
                    resultsModel = OpenResultsModel(
                        sequenceIsStopped = true,
                        sequenceId = 4
                    )
                ),
                ResultsSituation(
                    description = "5. Sequence running, hasChoices, has results, no explanations",
                    resultsModel = ChoiceResultsModel(
                        sequenceIsStopped = false,
                        sequenceId = 5,
                        hasAnyResult = true,
                        responseDistributionChartModel = ResponseDistributionChartModel(
                            interactionId = 5,
                            choiceSpecification = ChoiceSpecificationData(
                                2,
                                listOf(2)
                            ),
                            results = ResponsesDistribution(
                                ResponsesDistributionOnAttempt(4, arrayOf(1, 3), 0)
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
                        hasAnyResult = true,
                        responseDistributionChartModel = ResponseDistributionChartModel(
                            interactionId = 6,
                            choiceSpecification = ChoiceSpecificationData(
                                2,
                                listOf(2)
                            ),
                            results = ResponsesDistribution(
                                ResponsesDistributionOnAttempt(4, arrayOf(1, 3), 0)
                            ).toLegacyFormat()
                        ),
                        hasExplanations = true,
                        explanationViewerModel = ChoiceExplanationViewerModel(
                            explanationsByResponse = mapOf(
                                ResponseData(
                                    listOf(1),
                                    0,
                                    false
                                )
                                        to listOf(
                                    ExplanationData(
                                        "explication 1",
                                        "Joe Walson (@Jwal)"
                                    ),
                                    ExplanationData(
                                        "explication 2",
                                        "Jack DiCaprio (@Jdic)"
                                    )
                                ),
                                ResponseData(
                                    listOf(2),
                                    100,
                                    true
                                )
                                        to listOf(
                                    ExplanationData(
                                        "explication 3",
                                        "Wiliam Shakespeare (@Wsha)"
                                    ),
                                    ExplanationData(
                                        "explication 4",
                                        "Averell Collignon (@Acol)"
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
                        explanationViewerModel = OpenExplanationViewerModel(
                            explanations = listOf(
                                ExplanationData(
                                    "explication 1",
                                    "Joe Walson (@Jwal)"
                                ),
                                ExplanationData(
                                    "explication 2",
                                    "Jack DiCaprio (@Jdic)"
                                ),

                                ExplanationData(
                                    "explication 3",
                                    "Wiliam Shakespeare (@Wsha)"
                                ),
                                ExplanationData(
                                    "explication 4",
                                    "Averell Collignon (@Acol)"
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

    @GetMapping("/sequence-info")
    fun testSequenceInfo(
        authentication: Authentication,
        model: Model
    ): String {

        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
            "sequenceInfoSituations",
            SequenceGenerator.generateAllTypes(user)
                .map {
                    SequenceInfoSituation(
                        describeSequence(it),
                        SequenceInfoResolver.resolve(true, it, messageBuilder)
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

                (sequence.activeInteraction?.let {
                    ", interaction=(${it.interactionType},${it.state} )"
                } ?: "")


    @GetMapping("/command")
    fun testCommand(
        authentication: Authentication,
        model: Model
    ): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
            "commandSituations",
            SequenceGenerator.generateAllTypes(user)
                .map {
                    CommandSituation(
                        describeSequence(it),
                        CommandModelFactory.build(user, it)
                    )
                }
        )

        return "/player/assignment/sequence/components/test-command"
    }

    data class CommandSituation(
        val description: String,
        val model: CommandModel
    )

    @GetMapping("/response-phase")
    fun testResponsePhase(
        authentication: Authentication,
        model: Model
    ): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
            "responsePhaseModel",
            ResponsePhaseModel(
                sequenceId = 622L,
                interactionId = 1731L,
                userActiveInteractionState = State.show,
                responseSubmitted = false,
                responseFormModel = ResponseFormModel(
                    interactionId = 1731L,
                    attempt = 1,
                    responseSubmissionSpecification = ResponseSubmissionSpecification(
                        studentsProvideExplanation = true,
                        studentsProvideConfidenceDegree = true
                    ),
                    timeToProvideExplanation = true,
                    hasChoices = true,
                    multipleChoice = true,
                    firstAttemptChoices = arrayOf(2),
                    firstAttemptExplanation = "Hello World",
                    firstAttemptConfidenceDegree = ConfidenceDegree.CONFIDENT,
                    nbItem = 3
                )

            )
        )

        return "/player/assignment/sequence/components/test-response-phase"
    }

    @GetMapping("/evaluation-phase")
    fun testEvaluationPhase(
        authentication: Authentication,
        model: Model
    ): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
            "evaluationPhaseModel",
            EvaluationPhaseModel(
                sequenceId = 12,
                interactionId = 123,
                choices = true,
                activeInteractionRank = 2,
                userHasCompletedPhase2 = false,
                responsesToGrade = listOf(
                    org.elaastic.questions.player.components.evaluationPhase.ResponseData(
                        id = 1,
                        choiceList = listOf(1),
                        explanation = "1st explanation"
                    ),
                    org.elaastic.questions.player.components.evaluationPhase.ResponseData(
                        id = 2,
                        choiceList = listOf(2),
                        explanation = "2nd explanation"
                    )
                ),
                secondAttemptAllowed = true,
                secondAttemptAlreadySubmitted = false,
                responseFormModel = ResponseFormModel(
                    interactionId = 1731L,
                    attempt = 2,
                    responseSubmissionSpecification = ResponseSubmissionSpecification(
                        studentsProvideExplanation = true,
                        studentsProvideConfidenceDegree = true
                    ),
                    timeToProvideExplanation = true,
                    hasChoices = true,
                    multipleChoice = true,
                    firstAttemptChoices = arrayOf(2),
                    firstAttemptExplanation = "Hello World",
                    firstAttemptConfidenceDegree = ConfidenceDegree.CONFIDENT,
                    nbItem = 3
                ),
                userActiveInteractionState = State.show,
                userHasPerformedEvaluation = false
            )
        )

        return "/player/assignment/sequence/components/test-evaluation-phase"
    }
}
