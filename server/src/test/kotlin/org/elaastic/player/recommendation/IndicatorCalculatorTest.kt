package org.elaastic.player.recommendation

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.elaastic.activity.evaluation.peergrading.PeerGrading
import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.activity.response.Response
import org.elaastic.activity.response.ResponseSet
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class IndicatorCalculatorTest {

    @Test
    fun `Test compute p1`() {
        val mockResponses = mutableListOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            }
        )

        val result = IndicatorCalculator.computeP(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(0.3f))

    }

    @Test
    fun `Test compute p1 lower than 20 percent`() {
        val mockResponses = mutableListOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            }
        )

        val result = IndicatorCalculator.computeP(mockResponses)
        MatcherAssert.assertThat(result!!, Matchers.lessThan(IndicatorCalculator.LOW_THRESHOLD))

    }

    @Test
    fun `Test compute p1 greater than 80 percent`() {
        val mockResponses = mutableListOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            }
        )

        val result = IndicatorCalculator.computeP(mockResponses)
        MatcherAssert.assertThat(result, Matchers.greaterThan(IndicatorCalculator.HIGH_THRESHOLD))

    }

    @Test
    fun `Test compute p1 equal to 0`() {
        val mockResponses = mutableListOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
            }
        )

        val result = IndicatorCalculator.computeP(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(0f))

    }

    @Test
    fun `Test compute p1 equal to 100`() {
        val mockResponses = mutableListOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
            }
        )

        val result = IndicatorCalculator.computeP(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(1f))

    }

    @Test
    fun `Test compute p1 with empty list`() {

        val result = IndicatorCalculator.computeP(emptyList())
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))

    }

    @Test
    fun `Test compute p1 with null list`() {

        val result = IndicatorCalculator.computeP(null)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))

    }

    @Test
    fun `Test computePConf lower than 0`() {

        val mockResponses = listOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.TOTALLY_CONFIDENT)
            }
        )

        val result = IndicatorCalculator.computePConf(mockResponses)
        MatcherAssert.assertThat(result!!, Matchers.lessThan(0f))
    }

    @Test
    fun `Test computePConf equal to 0`() {

        val mockResponses = listOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            }
        )

        val result = IndicatorCalculator.computePConf(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(0f))
    }

    @Test
    fun `Test computePConf greater than 0`() {

        val mockResponses = listOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.TOTALLY_CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            }
        )

        val result = IndicatorCalculator.computePConf(mockResponses)
        MatcherAssert.assertThat(result, Matchers.greaterThan(0f))
    }

    @Test
    fun `Test computePConf when only correct answers`() {

        val mockResponses = listOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            }
        )

        val result = IndicatorCalculator.computePConf(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computePConf when only incorrect answers`() {

        val mockResponses = listOf(
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_CONFIDENT_AT_ALL)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.CONFIDENT)
            },
            mock<Response> {
                on { score }.doReturn(BigDecimal(0))
                on { confidenceDegree }.doReturn(ConfidenceDegree.NOT_REALLY_CONFIDENT)
            }
        )

        val result = IndicatorCalculator.computePConf(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computePConf with emptyList`() {

        val result = IndicatorCalculator.computePConf(emptyList())
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computePConf with null list`() {

        val result = IndicatorCalculator.computePConf(null)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computePPeer lower than 0`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse5 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockPeerGradings = listOf(
            createMockPeerGrading(1, correctResponse1),
            createMockPeerGrading(2, correctResponse1),
            createMockPeerGrading(1, correctResponse2),
            createMockPeerGrading(3, correctResponse1),
            createMockPeerGrading(4, correctResponse2),
            createMockPeerGrading(5, incorrectResponse2),
            createMockPeerGrading(4, incorrectResponse2),
            createMockPeerGrading(5, incorrectResponse3),
            createMockPeerGrading(5, incorrectResponse3),
            createMockPeerGrading(4, incorrectResponse4),
            createMockPeerGrading(2, incorrectResponse4),
            createMockPeerGrading(3, incorrectResponse5)
        )

        val result = IndicatorCalculator.computePPeer(mockPeerGradings)
        MatcherAssert.assertThat(result, Matchers.lessThan(0f))
    }

    @Test
    fun `Test computePPeer equal to 0`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockPeerGradings = listOf(
            createMockPeerGrading(3, correctResponse1),
            createMockPeerGrading(2, correctResponse1),
            createMockPeerGrading(1, correctResponse2),
            createMockPeerGrading(4, correctResponse1),
            createMockPeerGrading(3, correctResponse2),
            createMockPeerGrading(3, incorrectResponse2),
            createMockPeerGrading(2, incorrectResponse2),
            createMockPeerGrading(1, incorrectResponse3),
            createMockPeerGrading(4, incorrectResponse3),
            createMockPeerGrading(3, incorrectResponse4)
        )

        val result = IndicatorCalculator.computePPeer(mockPeerGradings)
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(0f))
    }

    @Test
    fun `Test computePPeer greater than 0`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockPeerGradings = listOf(
            createMockPeerGrading(3, correctResponse1),
            createMockPeerGrading(5, correctResponse1),
            createMockPeerGrading(5, correctResponse2),
            createMockPeerGrading(4, correctResponse1),
            createMockPeerGrading(3, correctResponse2),
            createMockPeerGrading(1, incorrectResponse2),
            createMockPeerGrading(2, incorrectResponse2),
            createMockPeerGrading(1, incorrectResponse3),
            createMockPeerGrading(1, incorrectResponse3),
            createMockPeerGrading(3, incorrectResponse1)
        )

        val result = IndicatorCalculator.computePPeer(mockPeerGradings)
        MatcherAssert.assertThat(result, Matchers.greaterThan(0f))
    }

    @Test
    fun `Test computePPeer when only correct answers`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }

        val mockPeerGradings = listOf(
            createMockPeerGrading(3, correctResponse1),
            createMockPeerGrading(2, correctResponse1),
            createMockPeerGrading(1, correctResponse2),
            createMockPeerGrading(4, correctResponse1),
            createMockPeerGrading(3, correctResponse2)
        )

        val result = IndicatorCalculator.computePPeer(mockPeerGradings)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computePPeer when only incorrect answers`() {

        val incorrectResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockPeerGradings = listOf(
            createMockPeerGrading(3, incorrectResponse1),
            createMockPeerGrading(2, incorrectResponse1),
            createMockPeerGrading(1, incorrectResponse2),
            createMockPeerGrading(4, incorrectResponse1),
            createMockPeerGrading(3, incorrectResponse2)
        )

        val result = IndicatorCalculator.computePPeer(mockPeerGradings)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computePPeer with emptyList`() {

        val result = IndicatorCalculator.computePPeer(emptyList())
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computePPeer with null List`() {

        val result = IndicatorCalculator.computePPeer(null)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computePPeer with null score`() {

        val incorrectResponse1 = mock<Response> {
            on { score }.doReturn(null)
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }

        val mockPeerGradings = listOf(
            createMockPeerGrading(3, incorrectResponse1),
            createMockPeerGrading(2, incorrectResponse1),
            createMockPeerGrading(1, incorrectResponse2),
            createMockPeerGrading(4, incorrectResponse1),
            createMockPeerGrading(1, correctResponse3),
            createMockPeerGrading(4, correctResponse3),
            createMockPeerGrading(3, incorrectResponse2)
        )
        val result = IndicatorCalculator.computePPeer(mockPeerGradings)
        print(result)
        MatcherAssert.assertThat(result, Matchers.lessThan(0f))
    }

    @Test
    fun `Test computeD lower than 0`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse2,
                    correctResponse3,
                    incorrectResponse1,
                    incorrectResponse2,
                    incorrectResponse3
                )
            )
            on { get(2) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse2,
                    incorrectResponse4,
                    incorrectResponse1,
                    incorrectResponse2,
                    incorrectResponse3
                )
            )
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result!!, Matchers.lessThan(0f))
    }

    @Test
    fun `Test computeD lower than 0 with multiple response switching`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(
                mutableListOf(
                    incorrectResponse4,
                    correctResponse2,
                    correctResponse3,
                    incorrectResponse1,
                    incorrectResponse2,
                    incorrectResponse3
                )
            )
            on { get(2) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    incorrectResponse4,
                    incorrectResponse4,
                    incorrectResponse1,
                    incorrectResponse2,
                    incorrectResponse3
                )
            )
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result!!, Matchers.lessThan(0f))
    }

    @Test
    fun `Test computeD equal to 0`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse2,
                    correctResponse3,
                    correctResponse4,
                    incorrectResponse2,
                    incorrectResponse3
                )
            )
            on { get(2) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse2,
                    correctResponse3,
                    correctResponse4,
                    incorrectResponse2,
                    incorrectResponse3
                )
            )
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(0f))
    }

    @Test
    fun `Test computeD equal to 0 with multiple response switching`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse5 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse2,
                    correctResponse3,
                    incorrectResponse1,
                    correctResponse5,
                    incorrectResponse3
                )
            )
            on { get(2) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    incorrectResponse4,
                    correctResponse3,
                    correctResponse4,
                    correctResponse5,
                    incorrectResponse3
                )
            )
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(0f))
    }

    @Test
    fun `Test computeD greater than 0`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse2,
                    correctResponse3,
                    incorrectResponse1,
                    incorrectResponse2,
                    incorrectResponse3
                )
            )
            on { get(2) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse2,
                    correctResponse3,
                    correctResponse4,
                    incorrectResponse2,
                    incorrectResponse3
                )
            )
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result, Matchers.greaterThan(0f))
    }

    @Test
    fun `Test computeD greater than 0 with multiple response switching`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse5 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse2 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse2,
                    correctResponse3,
                    incorrectResponse1,
                    incorrectResponse2,
                    incorrectResponse3
                )
            )
            on { get(2) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    incorrectResponse4,
                    correctResponse3,
                    correctResponse4,
                    correctResponse5,
                    incorrectResponse3
                )
            )
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result, Matchers.greaterThan(0f))
    }

    @Test
    fun `Test computeD with emptyList1`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse5 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(emptyList<Response>().toMutableList())
            on { get(2) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    incorrectResponse4,
                    correctResponse3,
                    correctResponse4,
                    correctResponse5,
                    incorrectResponse3
                )
            )
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computeD with emptyList2`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse5 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }
        val incorrectResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    incorrectResponse4,
                    correctResponse3,
                    correctResponse4,
                    correctResponse5,
                    incorrectResponse3
                )
            )
            on { get(2) }.doReturn(emptyList<Response>().toMutableList())
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun `Test computeD with p2 equal to 1`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse5 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val incorrectResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(0))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(
                mutableListOf(
                    incorrectResponse1,
                    correctResponse3,
                    correctResponse4,
                    correctResponse5
                )
            )
            on { get(2) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse3,
                    correctResponse4,
                    correctResponse5
                )
            )
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result, Matchers.greaterThan(0f))
    }

    @Test
    fun `Test computeD with p1 and p2 equal to 1`() {

        val correctResponse1 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse3 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse4 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }
        val correctResponse5 = mock<Response> {
            on { score }.doReturn(BigDecimal(100))
        }

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse3,
                    correctResponse4,
                    correctResponse5
                )
            )
            on { get(2) }.doReturn(
                mutableListOf(
                    correctResponse1,
                    correctResponse3,
                    correctResponse4,
                    correctResponse5
                )
            )
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(0f))
    }

    @Test
    fun `Test computeD with both lists empty`() {

        val mockResponses = mock<ResponseSet> {
            on { get(1) }.doReturn(emptyList<Response>().toMutableList())
            on { get(2) }.doReturn(emptyList<Response>().toMutableList())
        }
        val result = IndicatorCalculator.computeD(mockResponses)
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(0f))
    }

    @Test
    fun `Test tooLow with p1 too low`() {

        MatcherAssert.assertThat(IndicatorCalculator.tooLow(0.1f), CoreMatchers.`is`(true))
    }

    @Test
    fun `Test tooLow with p1 not too low`() {

        MatcherAssert.assertThat(IndicatorCalculator.tooLow(0.6f), CoreMatchers.`is`(false))
    }

    @Test
    fun `Test tooLow with p1 equal to 20 percent`() {

        MatcherAssert.assertThat(
            IndicatorCalculator.tooLow(IndicatorCalculator.LOW_THRESHOLD),
            CoreMatchers.`is`(false)
        )
    }

    @Test
    fun `Test tooLow with p1 equal to 0`() {

        MatcherAssert.assertThat(IndicatorCalculator.tooLow(0.0f), CoreMatchers.`is`(true))
    }

    @Test
    fun `Test veryHigh with p1 very high`() {

        MatcherAssert.assertThat(IndicatorCalculator.veryHigh(0.9f), CoreMatchers.`is`(true))
    }

    @Test
    fun `Test veryHigh with p1 not very high`() {

        MatcherAssert.assertThat(IndicatorCalculator.veryHigh(0.6f), CoreMatchers.`is`(false))
    }

    @Test
    fun `Test veryHigh with p1 equal to 0`() {

        MatcherAssert.assertThat(IndicatorCalculator.veryHigh(0.0f), CoreMatchers.`is`(false))
    }

    @Test
    fun `Test veryHigh with p1 equal to 80 percent`() {

        MatcherAssert.assertThat(
            IndicatorCalculator.veryHigh(IndicatorCalculator.HIGH_THRESHOLD),
            CoreMatchers.`is`(false)
        )
    }

    @Test
    fun `Test adequate with p1 adequate`() {

        MatcherAssert.assertThat(IndicatorCalculator.adequate(0.4f), CoreMatchers.`is`(true))
    }

    @Test
    fun `Test adequate with p1 very high`() {

        MatcherAssert.assertThat(IndicatorCalculator.adequate(0.9f), CoreMatchers.`is`(false))
    }

    @Test
    fun `Test adequate with p1 tooLow`() {

        MatcherAssert.assertThat(IndicatorCalculator.adequate(0.15f), CoreMatchers.`is`(false))
    }

    @Test
    fun `Test adequate with p1 equal to 20 percent`() {

        MatcherAssert.assertThat(
            IndicatorCalculator.adequate(IndicatorCalculator.LOW_THRESHOLD),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun `Test adequate with p1 equal to 80 percent`() {

        MatcherAssert.assertThat(
            IndicatorCalculator.adequate(IndicatorCalculator.HIGH_THRESHOLD),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun `Test adequate with p1 equal to 0`() {

        MatcherAssert.assertThat(IndicatorCalculator.adequate(0.0f), CoreMatchers.`is`(false))
    }

    fun createMockPeerGrading(gradeArg: Int, responseArg: Response) =
        mock<PeerGrading> {
            on { grade }.doReturn(BigDecimal(gradeArg))
            on { response }.doReturn(responseArg)
        }
}