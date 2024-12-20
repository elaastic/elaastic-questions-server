package org.elaastic.player.recommendation

import org.elaastic.player.explanations.ExplanationData
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class CorrectAndMeanGradeComparatorTest {

    val joe: ExplanationData = ExplanationData(
        responseId = 1L,
        meanGrade = BigDecimal("3.5"),
        score = BigDecimal("0")
    )

    val jack: ExplanationData = ExplanationData(
        responseId = 2L,
        meanGrade = BigDecimal("1.5"),
        score = BigDecimal("0")
    )

    val william: ExplanationData = ExplanationData(
        responseId = 3L,
        meanGrade = BigDecimal("5"),
        score = BigDecimal("100")
    )

    val averell: ExplanationData = ExplanationData(
        responseId = 4L,
        meanGrade = BigDecimal("1"),
        score = BigDecimal("100")
    )

    val luke: ExplanationData = ExplanationData(
        responseId = 5L,
        meanGrade = BigDecimal("1"),
        score = BigDecimal("100")
    )

    val scoreNull: ExplanationData = ExplanationData(
        responseId = 6L,
        meanGrade = BigDecimal("1")
    )

    val scoreNull2: ExplanationData = ExplanationData(
        responseId = 7L,
        meanGrade = BigDecimal("1")
    )

    val gradeNull: ExplanationData = ExplanationData(
        responseId = 8L,
        score = BigDecimal("100")
    )

    val gradeNull2: ExplanationData = ExplanationData(
        responseId = 9L,
        score = BigDecimal("100")
    )


    @Test
    fun `Test comparison of both correct and same evaluation`() {

        val res = CorrectAndMeanGradeComparator().compare(luke, averell)
        assert(res == 0)
    }

    @Test
    fun `Test comparison of correct and incorrect`() {

        val res = CorrectAndMeanGradeComparator().compare(averell, joe)
        assert(res > 0)
    }

    @Test
    fun `Test comparison of incorrect and correct`() {

        val res = CorrectAndMeanGradeComparator().compare(joe, averell)
        assert(res < 0)
    }

    @Test
    fun `Test comparison of both correct and first has a better evaluation`() {

        val res = CorrectAndMeanGradeComparator().compare(william, averell)
        assert(res > 0)
    }

    @Test
    fun `Test comparison of both correct and second has a better evaluation`() {

        val res = CorrectAndMeanGradeComparator().compare(averell, william)
        assert(res < 0)
    }

    @Test
    fun `Test comparison of both incorrect and first has a better evaluation`() {

        val res = CorrectAndMeanGradeComparator().compare(joe, jack)
        assert(res > 0)
    }

    @Test
    fun `Test comparison of both incorrect and second has a better evaluation`() {

        val res = CorrectAndMeanGradeComparator().compare(jack, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with first null`() {

        val res = CorrectAndMeanGradeComparator().compare(null, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with second null`() {

        val res = CorrectAndMeanGradeComparator().compare(joe, null)
        assert(res > 0)
    }

    @Test
    fun `Test comparison with both null`() {

        val res = CorrectAndMeanGradeComparator().compare(null, null)
        assert(res == 0)
    }

    @Test
    fun `Test comparison with first score null`() {

        val res = CorrectAndMeanGradeComparator().compare(scoreNull, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with second score null`() {

        val res = CorrectAndMeanGradeComparator().compare(joe, scoreNull)
        assert(res > 0)
    }

    @Test
    fun `Test comparison with both score null`() {

        val res = CorrectAndMeanGradeComparator().compare(scoreNull, scoreNull2)
        assert(res == 0)
    }

    @Test
    fun `Test comparison with first grade null`() {

        val res = CorrectAndMeanGradeComparator().compare(gradeNull, william)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with second grade null`() {
        val res = CorrectAndMeanGradeComparator().compare(william, gradeNull)
        assert(res > 0)
    }

    @Test
    fun `Test comparison with both grade null`() {

        val res = CorrectAndMeanGradeComparator().compare(gradeNull, gradeNull2)
        assert(res == 0)
    }
}