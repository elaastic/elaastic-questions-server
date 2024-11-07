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
package org.elaastic.questions.player.components.recommendation

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ChoiceType
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.common.web.MessageBuilder
import org.elaastic.questions.subject.statement.Statement
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import java.math.BigDecimal


internal class RecommendationResolverTest {

    private val mockResponseSubmission = mock<ResponseSubmissionSpecification> {
        on { studentsProvideExplanation }.doReturn(true)
    }

    private val mockExpectedChoice = mock<ChoiceItem> {
        on { index }.doReturn(2)
    }

    private val mockChoiceSpecification = mock<ExclusiveChoiceSpecification> {
        on { getChoiceType() }.doReturn(ChoiceType.EXCLUSIVE)
        on { nbCandidateItem }.doReturn(4)
        on { expectedChoice }.doReturn(mockExpectedChoice)
    }

    private val mockStatement = mock<Statement> {
        on { choiceSpecification}.doReturn(mockChoiceSpecification)
    }

    private val mockSequenceStateEndPhase1 = mock<Sequence> {
        on { recommendableAfterPhase1() }.doReturn(true)
        on { state }.doReturn(State.show)
        on { statement}.doReturn(mockStatement)
        on { getResponseSubmissionSpecification() }.doReturn(mockResponseSubmission)
    }

    private val mockSequenceStateEndPhase2 = mock<Sequence> {
        on { recommendableAfterPhase2() }.doReturn(true)
        on { recommendableAfterPhase1() }.doReturn(false)
        on { statement}.doReturn(mockStatement)
        on { state }.doReturn(State.show)
        on { getResponseSubmissionSpecification() }.doReturn(mockResponseSubmission)
    }

    private val mockMessageBuilder = mock<MessageBuilder> {
        on { message("player.sequence.recommendation.skipPhase2.message") }.doReturn("We recommend that you skip phase 2, which won't be beneficial to learners, and to directly start phase 3.")
        on { message("player.sequence.recommendation.skipPhase2.weak_benefits.message") }.doReturn("We recommend that you skip phase 2, which will barely be be beneficial to learners, and to directly start phase 3.")
        on { message("player.sequence.recommendation.provide_hint.message") }.doReturn("We recommend that you provide learners with a hint before starting phase 2.")
        on { message("player.sequence.recommendation.reopenPhase1.message") }.doReturn("We recommend that you reopen phase 1 since there are no answers.")
        on { message("player.sequence.recommendation.noExplanation.message") }.doReturn("There is no rationale for correct answer(s).")
        on { message("player.sequence.recommendation.p1Null.message") }.doReturn("There are no correct answers.")
        on { message("player.sequence.recommendation.expected_benefits.non_significant") }.doReturn("According to our studies, similar sequences weren't beneficial to learners.")
        on { message("player.sequence.recommendation.expected_benefits.weak") }.doReturn("According to our studies, similar sequences were barely beneficial to learners.")
        on { message("player.sequence.recommendation.explanation.unsatisfying_condition") }.doReturn("In this situation, the confrontation of viewpoint won't provide the expected outcome.")
        on { message("player.sequence.recommendation.focus_on_incorrect_detailed") }.doReturn("We recommend that you discuss incorrect answers in details.")
        on { message("player.sequence.recommendation.focus_on_correct_detailed") }.doReturn("We recommend that you discuss correct answers in details.")
        on { message("player.sequence.recommendation.focus_on_incorrect_brief") }.doReturn("We recommend that you briefly discuss incorrect answers.")
        on { message("player.sequence.recommendation.focus_on_correct_brief") }.doReturn("We recommend that you briefly discuss correct answers.")
        on { message("player.sequence.recommendation.focus_on_incorrect") }.doReturn("We recommend that you discuss incorrect answers.")
        on { message("player.sequence.recommendation.focus_on_correct") }.doReturn("We recommend that you discuss correct answers.")
        on { message("player.sequence.recommendation.p1VeryHigh.message") }.doReturn("More than 70% of the answers are correct. Learners have enough knowledge to answer correctly alone.")
        on { message("player.sequence.recommendation.p1TooLow.message") }.doReturn("Less than 30% of the answers are correct. Learners don't have enough knowledge about the topic.")
        on { message("player.sequence.recommendation.p1NoAnswers.message") }.doReturn("There is no correct answers. The confrontation of viewpoints will be useless.")
        on { message("player.sequence.recommendation.p1VeryHigh_skip.message") }.doReturn("More than 70% of the answers are correct. A brief discussion is enough.")
        on { message("player.sequence.recommendation.p1TooLow_skip.message") }.doReturn("Less than 30% of the answers are correct. A detailed discussion is required.")
        on { message("player.sequence.recommendation.pConfPos.message") }.doReturn("Learners who provided a correct answer have a higher mean confidence degree than those who provided an incorrect one. The rationales will be correctly evaluated.")
        on { message("player.sequence.recommendation.pConfZero.message") }.doReturn("Learners who provided a correct answer have an identical mean confidence degree than those who provided an incorrect one. The rationales will be correctly evaluated.")
        on { message("player.sequence.recommendation.pConfNeg.message") }.doReturn("Learners who provided a correct answer have a lower mean confidence degree than those who provided an incorrect one. The rationales won't be correctly evaluated.")
        on { message("player.sequence.recommendation.pConfPosSkip.message") }.doReturn("Learners who provided a correct answer have a higher mean confidence degree than those who provided an incorrect one. This means that correct answers are more popular than incorrect ones.")
        on { message("player.sequence.recommendation.pConfZeroSkip.message") }.doReturn("Learners who provided a correct answer have a identical mean confidence degree than those who provided an incorrect one. This means that correct answers are as popular as incorrect ones.")
        on { message("player.sequence.recommendation.pConfNegSkip.message") }.doReturn("Learners who provided a correct answer have a lower mean confidence degree than those who provided an incorrect one. This means that correct answers are less popular than incorrect ones.")
        on { message("player.sequence.recommendation.pPeerPos.message") }.doReturn("Rationales associated to a correct answer have a higher mean grade than those associated to an incorrect one. This means that correct answers are more popular than incorrect ones.")
        on { message("player.sequence.recommendation.pPeerZero.message") }.doReturn("Rationales associated to a correct answer have a identical mean grade than those associated to an incorrect one. This means that correct answers are as popular as incorrect ones.")
        on { message("player.sequence.recommendation.pPeerNeg.message") }.doReturn("Rationales associated to a correct answer have a lower mean grade than those associated to an incorrect one. This means that correct answers are less popular than incorrect ones.")
        on { message("player.sequence.recommendation.dPos.message") }.doReturn("The second vote has more correct answers than the first one. Which means that the evaluation phase was beneficial to learners. A detailed discussion is required.")
        on { message("player.sequence.recommendation.dZero.message") }.doReturn("The second vote has as many correct answers as the first one. Which means that the evaluation phase was not beneficial to learners. A detailed discussion is required.")
        on { message("player.sequence.recommendation.dNeg.message") }.doReturn("The second vote has less correct answers than the first one. Which means that the evaluation phase was harmful to learners. A detailed discussion is required.")
    }

    private val mockCorrectResponseConf0 = mock<Response> {
        on { score }.doReturn(BigDecimal(100))
        on { fake }.doReturn(false)
        on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
        on { explanation }.doReturn("Hello World, and Universe")
    }

    private val mockCorrectResponseConf1 = mock<Response> {
        on { score }.doReturn(BigDecimal(100))
        on { fake }.doReturn(false)
        on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
        on { explanation }.doReturn("Hello World, and Universe")
    }

    private val mockCorrectResponseConf2 = mock<Response> {
        on { score }.doReturn(BigDecimal(100))
        on { fake }.doReturn(false)
        on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
        on { explanation }.doReturn("Hello World, and Universe")
    }

    private val mockCorrectResponseConf3 = mock<Response> {
        on { score }.doReturn(BigDecimal(100))
        on { fake }.doReturn(false)
        on { confidenceDegree }.doReturn(ConfidenceDegree.TOTALLY_CONFIDENT)
        on { explanation }.doReturn("Hello World, and Universe")
    }

    private val mockIncorrectResponseConf0 = mock<Response> {
        on { score }.doReturn(BigDecimal(0))
        on { fake }.doReturn(false)
        on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
        on { explanation }.doReturn("Hello World, and Universe")
    }

    private val mockIncorrectResponseConf1 = mock<Response> {
        on { score }.doReturn(BigDecimal(0))
        on { fake }.doReturn(false)
        on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
        on { explanation }.doReturn("Hello World, and Universe")
    }

    private val mockIncorrectResponseConf2 = mock<Response> {
        on { score }.doReturn(BigDecimal(0))
        on { fake }.doReturn(false)
        on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
        on { explanation }.doReturn("Hello World, and Universe")
    }

    private val mockIncorrectResponseConf3 = mock<Response> {
        on { score }.doReturn(BigDecimal(0))
        on { fake }.doReturn(false)
        on { confidenceDegree }.doReturn(ConfidenceDegree.TOTALLY_CONFIDENT)
        on { explanation }.doReturn("Hello World, and Universe")
    }

    @Test
    fun `Test recommendation when there are less than 10 answers`() {


        val mockResponses = mutableListOf(
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                }
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }
        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase1, mockMessageBuilder)
        assertThat(actual, `is`(nullValue()))
    }

    @Test
    fun `Test recommendation when there are no explanations`() {


        val mockResponses = mutableListOf(
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                }
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }
        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase1, mockMessageBuilder)
        val expected = RecommendationModel(
                noCorrectExplanation = true,
                message = mockMessageBuilder.message("player.sequence.recommendation.skipPhase2.message"),
                popupDetailedExplanation = PopupDetailedExplanation.NO_EXPLANATION_FOR_CORRECT_ANSWERS
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when there are no correct explanations`() {

        val mockResponses = mutableListOf(
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("Hey there")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("Hey there")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("Hey there")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("Hey there")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("Hey there")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("Hey there")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("Hey there")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("Hey there")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("Hey there")
                }
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase1, mockMessageBuilder)
        val expected = RecommendationModel(
                noCorrectExplanation = true,
                message = mockMessageBuilder.message("player.sequence.recommendation.skipPhase2.message"),
                popupDetailedExplanation = PopupDetailedExplanation.NO_EXPLANATION_FOR_CORRECT_ANSWERS
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when p1 greater than 80 percent and pconf lower than 0`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf1,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockCorrectResponseConf1,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf2
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase1, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.skipPhase2.weak_benefits.message"),
                explanationP1 = ExplanationP1.VERY_HIGH,
                popupDetailedExplanation = PopupDetailedExplanation.WEAK_BENEFITS
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when p1 greater than 80 percent and pconf greater than 0`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf0,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf0
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase1, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.skipPhase2.weak_benefits.message"),
                explanationP1 = ExplanationP1.VERY_HIGH,
                popupDetailedExplanation = PopupDetailedExplanation.WEAK_BENEFITS
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when p1 lower than 20 percent and pconf lower than 0`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf1,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockCorrectResponseConf1,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase1, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.skipPhase2.message"),
                explanationP1 = ExplanationP1.TOO_LOW,
                explanationPConf = ExplanationPConf.PCONF_NEG,
                popupDetailedExplanation = PopupDetailedExplanation.NON_SIGNIFICANT_BENEFITS
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when p1 lower than 20 percent and pconf greater than 0`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf2,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockCorrectResponseConf3,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf0,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf0,
                mockIncorrectResponseConf1
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase1, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.provide_hint.message"),
                explanationP1 = ExplanationP1.TOO_LOW,
                explanationPConf = ExplanationPConf.PCONF_POS,
                popupDetailedExplanation = PopupDetailedExplanation.WEAK_BENEFITS
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when p1 lower than 20 percent and pconf equal to 0`() {

        val mockResponses = mutableListOf(
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockCorrectResponseConf3,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf3,
                mockIncorrectResponseConf3,
                mockIncorrectResponseConf3,
                mockIncorrectResponseConf3,
                mockIncorrectResponseConf3
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase1, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.provide_hint.message"),
                explanationP1 = ExplanationP1.TOO_LOW,
                explanationPConf = ExplanationPConf.PCONF_ZERO,
                popupDetailedExplanation = PopupDetailedExplanation.WEAK_BENEFITS
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when p1 lower than 20 percent and pconf is null`() {

        val mockResponses = mutableListOf(
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(null)
                    on { explanation }.doReturn("Hello World, and Universe")
                },
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase1, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.provide_hint.message"),
                explanationP1 = ExplanationP1.TOO_LOW,
                popupDetailedExplanation = PopupDetailedExplanation.WEAK_BENEFITS
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped and no explanations for correct answers`() {


        val mockResponses = mutableListOf(
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(0))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
                    on { explanation }.doReturn("")
                }
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }
        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                noCorrectExplanation = true,
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_incorrect_detailed"),
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_INCORRECT,
                recommendedExplanationsComparator = IncorrectAndConfidenceDegreeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped and p1 greater than 80 percent and pconf lower than 0`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf1,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf3,
                mockCorrectResponseConf1,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf3
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_incorrect_brief"),
                explanationP1 = ExplanationP1.VERY_HIGH_SKIP,
                explanationPConf = ExplanationPConf.PCONF_NEG_SKIP,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_INCORRECT,
                recommendedExplanationsComparator = IncorrectAndConfidenceDegreeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped and p1 greater than 80 percent and pconf greater than 0`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf0,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf0
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct_brief"),
                explanationP1 = ExplanationP1.VERY_HIGH_SKIP,
                explanationPConf = ExplanationPConf.PCONF_POS_SKIP,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndConfidenceDegreeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped and p1 greater than 80 percent and pconf equal to 0`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf2
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct_brief"),
                explanationP1 = ExplanationP1.VERY_HIGH_SKIP,
                explanationPConf = ExplanationPConf.PCONF_ZERO_SKIP,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndConfidenceDegreeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped and p1 greater than 80 percent and pconf is null`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf2,
                mockCorrectResponseConf0,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf2,
                mockCorrectResponseConf1,
                mockCorrectResponseConf3,
                mockCorrectResponseConf1,
                mockCorrectResponseConf1,
                mockCorrectResponseConf3
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct"),
                explanationP1 = ExplanationP1.VERY_HIGH_SKIP,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndConfidenceDegreeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped and p1 lower than 20 percent and pconf lower than 0`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf1,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockCorrectResponseConf1,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_incorrect_detailed"),
                explanationP1 = ExplanationP1.TOO_LOW_SKIP,
                explanationPConf = ExplanationPConf.PCONF_NEG_SKIP,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_INCORRECT,
                recommendedExplanationsComparator = IncorrectAndConfidenceDegreeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped and p1 lower than 20 percent and pconf greater than 0`() {

        val mockResponses = mutableListOf(
                mockCorrectResponseConf2,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct_detailed"),
                explanationP1 = ExplanationP1.TOO_LOW_SKIP,
                explanationPConf = ExplanationPConf.PCONF_POS_SKIP,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndConfidenceDegreeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped p1 lower than 20 percent and pconf equal to 0`() {

        val mockResponses = mutableListOf(
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockIncorrectResponseConf2,
                mockCorrectResponseConf3,
                mockCorrectResponseConf2,
                mockIncorrectResponseConf3,
                mockIncorrectResponseConf3,
                mockIncorrectResponseConf3,
                mockIncorrectResponseConf3,
                mockIncorrectResponseConf3
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct_detailed"),
                explanationP1 = ExplanationP1.TOO_LOW_SKIP,
                explanationPConf = ExplanationPConf.PCONF_ZERO_SKIP,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndConfidenceDegreeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped p1 lower than 20 percent and pconf is null`() {

        val mockResponses = mutableListOf(
                mock<Response> {
                    on { score }.doReturn(BigDecimal(100))
                    on { fake }.doReturn(false)
                    on { confidenceDegree }.doReturn(null)
                    on { explanation }.doReturn("Hello World, and Universe")
                },
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1,
                mockIncorrectResponseConf1
        )

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_incorrect"),
                explanationP1 = ExplanationP1.TOO_LOW_SKIP,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_INCORRECT,
                recommendedExplanationsComparator = IncorrectAndConfidenceDegreeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when phase 2 skipped and p1 is null`() {

        val mockResponses = mutableListOf<Response>()

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mockResponses)
        }

        val actual = RecommendationResolver.resolve(mockResponseSet, emptyList(), mockSequenceStateEndPhase2, mockMessageBuilder)
        assertThat(actual, `is`(nullValue()))
    }

    @Test
    fun `Test recommendation when ppeer greater than 0 and d greater than 0`() {

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mutableListOf(mockCorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2))
            on { get(2) }.doReturn(mutableListOf(mockCorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2))
        }

        val mockPeerGradings = listOf(
                createMockPeerGrading(5, mockCorrectResponseConf2),
                createMockPeerGrading(3, mockCorrectResponseConf2),
                createMockPeerGrading(4, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(5, mockIncorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(3, mockIncorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf2)
        )

        val actual = RecommendationResolver.resolve(mockResponseSet, mockPeerGradings, mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct"),
                explanationD = ExplanationD.D_POS,
                explanationPPeer = ExplanationPPeer.PPEER_POS,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndMeanGradeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when ppeer greater than 0 and d lower than 0`() {

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mutableListOf(mockCorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2))
            on { get(2) }.doReturn(mutableListOf(mockCorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2))
        }

        val mockPeerGradings = listOf(
                createMockPeerGrading(5, mockCorrectResponseConf2),
                createMockPeerGrading(3, mockCorrectResponseConf2),
                createMockPeerGrading(4, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(5, mockIncorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(3, mockIncorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf2)
        )

        val actual = RecommendationResolver.resolve(mockResponseSet, mockPeerGradings, mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct_detailed"),
                explanationD = ExplanationD.D_NEG,
                explanationPPeer = ExplanationPPeer.PPEER_POS,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndMeanGradeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when ppeer greater than 0 and d equal to 0`() {

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mutableListOf(mockCorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2))
            on { get(2) }.doReturn(mutableListOf(mockCorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2, mockIncorrectResponseConf2))
        }

        val mockPeerGradings = listOf(
                createMockPeerGrading(5, mockCorrectResponseConf2),
                createMockPeerGrading(3, mockCorrectResponseConf2),
                createMockPeerGrading(4, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(5, mockIncorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(3, mockIncorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf2)
        )

        val actual = RecommendationResolver.resolve(mockResponseSet, mockPeerGradings, mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct_detailed"),
                explanationD = ExplanationD.D_ZERO,
                explanationPPeer = ExplanationPPeer.PPEER_POS,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndMeanGradeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when ppeer lower than 0 and d greater than 0`() {

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mutableListOf(mockIncorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1))
            on { get(2) }.doReturn(mutableListOf(mockCorrectResponseConf1, mockCorrectResponseConf2, mockCorrectResponseConf2, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockIncorrectResponseConf1, mockCorrectResponseConf1))
        }

        val mockPeerGradings = listOf(
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockCorrectResponseConf2),
                createMockPeerGrading(5, mockIncorrectResponseConf1),
                createMockPeerGrading(3, mockCorrectResponseConf1),
                createMockPeerGrading(4, mockCorrectResponseConf1),
                createMockPeerGrading(2, mockCorrectResponseConf1),
                createMockPeerGrading(5, mockCorrectResponseConf1),
                createMockPeerGrading(2, mockCorrectResponseConf1),
                createMockPeerGrading(2, mockCorrectResponseConf1),
                createMockPeerGrading(3, mockCorrectResponseConf1),
                createMockPeerGrading(2, mockCorrectResponseConf1),
                createMockPeerGrading(3, mockCorrectResponseConf1)
        )

        val actual = RecommendationResolver.resolve(mockResponseSet, mockPeerGradings, mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_incorrect"),
                explanationD = ExplanationD.D_POS,
                explanationPPeer = ExplanationPPeer.PPEER_NEG,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_INCORRECT,
                recommendedExplanationsComparator = IncorrectAndMeanGradeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when ppeer lower than 0 and d lower than 0`() {

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mutableListOf(mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1))
            on { get(2) }.doReturn(mutableListOf(mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1))
        }

        val mockPeerGradings = listOf(
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockCorrectResponseConf2),
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(3, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(5, mockIncorrectResponseConf1),
                createMockPeerGrading(4, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(3, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(4, mockIncorrectResponseConf1)
        )

        val actual = RecommendationResolver.resolve(mockResponseSet, mockPeerGradings, mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_incorrect_detailed"),
                explanationD = ExplanationD.D_NEG,
                explanationPPeer = ExplanationPPeer.PPEER_NEG,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_INCORRECT,
                recommendedExplanationsComparator = IncorrectAndMeanGradeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when ppeer lower than 0 and d equal to 0`() {

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mutableListOf(mockCorrectResponseConf1, mockCorrectResponseConf2, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1))
            on { get(2) }.doReturn(mutableListOf(mockCorrectResponseConf1, mockCorrectResponseConf2, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1))
        }

        val mockPeerGradings = listOf(
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockCorrectResponseConf2),
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(3, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(5, mockIncorrectResponseConf1),
                createMockPeerGrading(4, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(3, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(4, mockIncorrectResponseConf1)
        )

        val actual = RecommendationResolver.resolve(mockResponseSet, mockPeerGradings, mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_incorrect_detailed"),
                explanationD = ExplanationD.D_ZERO,
                explanationPPeer = ExplanationPPeer.PPEER_NEG,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_INCORRECT,
                recommendedExplanationsComparator = IncorrectAndMeanGradeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when ppeer equal to 0 and d greater than 0`() {

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mutableListOf(mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1))
            on { get(2) }.doReturn(mutableListOf(mockCorrectResponseConf1, mockCorrectResponseConf2, mockCorrectResponseConf2, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1))
        }

        val mockPeerGradings = listOf(
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(4, mockCorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(3, mockIncorrectResponseConf1),
                createMockPeerGrading(4, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1)
        )

        val actual = RecommendationResolver.resolve(mockResponseSet, mockPeerGradings, mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct"),
                explanationD = ExplanationD.D_POS,
                explanationPPeer = ExplanationPPeer.PPEER_ZERO,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndMeanGradeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when ppeer equal to 0 and d lower than 0`() {

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mutableListOf(mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1))
            on { get(2) }.doReturn(mutableListOf(mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1))
        }

        val mockPeerGradings = listOf(
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockCorrectResponseConf2),
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(3, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1)
        )

        val actual = RecommendationResolver.resolve(mockResponseSet, mockPeerGradings, mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct_detailed"),
                explanationD = ExplanationD.D_NEG,
                explanationPPeer = ExplanationPPeer.PPEER_ZERO,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndMeanGradeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `Test recommendation when ppeer equal to 0 and d equal to 0`() {

        val mockResponseSet = mock<ResponseSet> {
            on { get(1) }.doReturn(mutableListOf(mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1))
            on { get(2) }.doReturn(mutableListOf(mockCorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockIncorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1, mockCorrectResponseConf1))
        }

        val mockPeerGradings = listOf(
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(2, mockCorrectResponseConf2),
                createMockPeerGrading(1, mockCorrectResponseConf2),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(1, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1),
                createMockPeerGrading(2, mockIncorrectResponseConf1)
        )

        val actual = RecommendationResolver.resolve(mockResponseSet, mockPeerGradings, mockSequenceStateEndPhase2, mockMessageBuilder)
        val expected = RecommendationModel(
                message = mockMessageBuilder.message("player.sequence.recommendation.focus_on_correct_detailed"),
                explanationD = ExplanationD.D_ZERO,
                explanationPPeer = ExplanationPPeer.PPEER_ZERO,
                popupDetailedExplanation = PopupDetailedExplanation.POPULAR_ANSWERS_CORRECT,
                recommendedExplanationsComparator = CorrectAndMeanGradeComparator()
        )
        assertThat(actual, equalTo(expected))
    }

    private fun createMockPeerGrading(gradeArg: Int, responseArg: Response) =
            mock<PeerGrading> {
                on { grade }.doReturn(BigDecimal(gradeArg))
                on { response }.doReturn(responseArg)
            }
}
