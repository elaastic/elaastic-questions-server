package org.elaastic.questions.player

import org.elaastic.questions.directory.User
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal

@Controller
@RequestMapping("/player/test")
class TestingPlayerController() {


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
        model.addAttribute("sequenceStatistics", sequenceStatistics)
        model.addAttribute("studentsProvideExplanation", studentsProvideExplanation ?: true)


        return "/player/assignment/sequence/components/test-steps"
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

    object sequenceStatistics {
        val nbResponsesAttempt1: Int = 10
        val nbResponsesAttempt2: Int = 8
        val nbEvaluations: Int = 5
    }
}