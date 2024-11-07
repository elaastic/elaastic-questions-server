package org.elaastic.player.recommendation

import org.elaastic.player.explanations.ExplanationData
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class IncorrectAndConfidenceDegreeComparatorTest {

    val joe: ExplanationData = ExplanationData(
        responseId = 1L,
        confidenceDegree = ConfidenceDegree.CONFIDENT,
        score = BigDecimal("0")
    )

    val jack: ExplanationData = ExplanationData(
        responseId = 2L,
        confidenceDegree = ConfidenceDegree.NOT_CONFIDENT_AT_ALL,
        score = BigDecimal("0")
    )

    val william: ExplanationData = ExplanationData(
        responseId = 3L,
        confidenceDegree = ConfidenceDegree.TOTALLY_CONFIDENT,
        score = BigDecimal("100")
    )

    val averell: ExplanationData = ExplanationData(
        responseId = 4L,
        confidenceDegree = ConfidenceDegree.CONFIDENT,
        score = BigDecimal("100")
    )

    val luke: ExplanationData = ExplanationData(
        responseId = 5L,
        confidenceDegree = ConfidenceDegree.CONFIDENT,
        score = BigDecimal("100")
    )

    val scoreNull: ExplanationData = ExplanationData(
        responseId = 6L,
        confidenceDegree = ConfidenceDegree.TOTALLY_CONFIDENT
    )

    val scoreNull2: ExplanationData = ExplanationData(
        responseId = 7L,
        confidenceDegree = ConfidenceDegree.TOTALLY_CONFIDENT
    )

    val confidenceDegreeNull: ExplanationData = ExplanationData(
        responseId = 8L,
        score = BigDecimal("100")
    )

    val confidenceDegreeNull2: ExplanationData = ExplanationData(
        responseId = 9L,
        score = BigDecimal("100")
    )


    @Test
    fun `Test comparison of both correct and same confidence degree`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(luke, averell)
        assert(res == 0)
    }

    @Test
    fun `Test comparison of correct and incorrect`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(averell, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison of incorrect and correct`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(joe, averell)
        assert(res > 0)
    }

    @Test
    fun `Test comparison of both correct and first has a better confidence degree`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(william, averell)
        assert(res > 0)
    }

    @Test
    fun `Test comparison of both correct and second has a better confidence degree`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(averell, william)
        assert(res < 0)
    }

    @Test
    fun `Test comparison of both incorrect and first has a better confidence degree`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(joe, jack)
        assert(res > 0)
    }

    @Test
    fun `Test comparison of both incorrect and second has a better confidence degree`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(jack, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with first null`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(null, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with second null`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(joe, null)
        assert(res > 0)
    }

    @Test
    fun `Test comparison with both null`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(null, null)
        assert(res == 0)
    }

    @Test
    fun `Test comparison with first score null`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(scoreNull, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with second score null`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(joe, scoreNull)
        assert(res > 0)
    }

    @Test
    fun `Test comparison with both score null`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(scoreNull, scoreNull2)
        assert(res == 0)
    }

    @Test
    fun `Test comparison with first confidence degree null`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(confidenceDegreeNull, william)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with second confidence degree null`() {
        val res = IncorrectAndConfidenceDegreeComparator().compare(william, confidenceDegreeNull)
        assert(res > 0)
    }

    @Test
    fun `Test comparison with both confidence degree null`() {

        val res = IncorrectAndConfidenceDegreeComparator().compare(confidenceDegreeNull, confidenceDegreeNull2)
        assert(res == 0)
    }
}