package org.elaastic.player.recommendation

import org.elaastic.player.explanations.ExplanationData
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class IncorrectAndMeanGradeComparatorTest {

    val joe: ExplanationData = ExplanationData(
        responseId = 1L,
        author = "Joe",
        meanGrade = BigDecimal("3.5"),
        score = BigDecimal("0")
    )

    val jack: ExplanationData = ExplanationData(
        responseId = 2L,
        author = "Jack",
        meanGrade = BigDecimal("1.5"),
        score = BigDecimal("0")
    )

    val william: ExplanationData = ExplanationData(
        responseId = 3L,
        author = "William",
        meanGrade = BigDecimal("5"),
        score = BigDecimal("100")
    )

    val averell: ExplanationData = ExplanationData(
        responseId = 4L,
        author = "Averell",
        meanGrade = BigDecimal("1"),
        score = BigDecimal("100")
    )

    val luke: ExplanationData = ExplanationData(
        responseId = 5L,
        author = "Luke",
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

        val res = IncorrectAndMeanGradeComparator().compare(luke, averell)
        assert(res == 0)
    }

    @Test
    fun `Test comparison of correct and incorrect`() {

        val res = IncorrectAndMeanGradeComparator().compare(averell, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison of incorrect and correct`() {

        val res = IncorrectAndMeanGradeComparator().compare(joe, averell)
        assert(res > 0)
    }

    @Test
    fun `Test comparison of both correct and first has a better evaluation`() {

        val res = IncorrectAndMeanGradeComparator().compare(william, averell)
        assert(res > 0)
    }

    @Test
    fun `Test comparison of both correct and second has a better evaluation`() {

        val res = IncorrectAndMeanGradeComparator().compare(averell, william)
        assert(res < 0)
    }

    @Test
    fun `Test comparison of both incorrect and first has a better evaluation`() {

        val res = IncorrectAndMeanGradeComparator().compare(joe, jack)
        assert(res > 0)
    }

    @Test
    fun `Test comparison of both incorrect and second has a better evaluation`() {

        val res = IncorrectAndMeanGradeComparator().compare(jack, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with first null`() {

        val res = IncorrectAndMeanGradeComparator().compare(null, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with second null`() {

        val res = IncorrectAndMeanGradeComparator().compare(joe, null)
        assert(res > 0)
    }

    @Test
    fun `Test comparison with both null`() {

        val res = IncorrectAndMeanGradeComparator().compare(null, null)
        assert(res == 0)
    }

    @Test
    fun `Test comparison with first score null`() {

        val res = IncorrectAndMeanGradeComparator().compare(scoreNull, joe)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with second score null`() {

        val res = IncorrectAndMeanGradeComparator().compare(joe, scoreNull)
        assert(res > 0)
    }

    @Test
    fun `Test comparison with both score null`() {

        val res = IncorrectAndMeanGradeComparator().compare(scoreNull, scoreNull2)
        assert(res == 0)
    }

    @Test
    fun `Test comparison with first grade null`() {

        val res = IncorrectAndMeanGradeComparator().compare(gradeNull, william)
        assert(res < 0)
    }

    @Test
    fun `Test comparison with second grade null`() {
        val res = IncorrectAndMeanGradeComparator().compare(william, gradeNull)
        assert(res > 0)
    }

    @Test
    fun `Test comparison with both grade null`() {

        val res = IncorrectAndMeanGradeComparator().compare(gradeNull, gradeNull2)
        assert(res == 0)
    }

    @Test
    fun `Test sorted list`() {

        val l = listOf(joe, jack, averell, william, luke).sortedWith(IncorrectAndMeanGradeComparator()).reversed()
        assert(l[0] == joe)
        assert(l[1] == jack)
        assert(l[2] == william)
        assert(l[3] == luke)
        assert(l[4] == averell)
    }
}