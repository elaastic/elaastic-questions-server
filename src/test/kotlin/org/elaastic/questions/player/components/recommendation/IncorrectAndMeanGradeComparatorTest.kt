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

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.Sequence
import org.junit.jupiter.api.Test
import com.nhaarman.mockitokotlin2.*
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import java.math.BigDecimal
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.player.components.explanationViewer.ExplanationData
import java.math.RoundingMode

internal class IncorrectAndMeanGradeComparatorTest {

    val joe: ExplanationData = ExplanationData(
            author = "Joe",
            meanGrade = BigDecimal("3.5"),
            score = BigDecimal("0")
    )

    val jack: ExplanationData = ExplanationData(
            author = "Jack",
            meanGrade = BigDecimal("1.5"),
            score = BigDecimal("0")
    )

    val william: ExplanationData = ExplanationData(
            author = "William",
            meanGrade = BigDecimal("5"),
            score = BigDecimal("100")
    )

    val averell: ExplanationData = ExplanationData(
            author = "Averell",
            meanGrade = BigDecimal("1"),
            score = BigDecimal("100")
    )

    val luke: ExplanationData = ExplanationData(
            author = "Luke",
            meanGrade = BigDecimal("1"),
            score = BigDecimal("100")
    )

    val scoreNull: ExplanationData = ExplanationData(
            meanGrade = BigDecimal("1")
    )

    val scoreNull2: ExplanationData = ExplanationData(
            meanGrade = BigDecimal("1")
    )

    val gradeNull: ExplanationData = ExplanationData(
            score = BigDecimal("100")
    )

    val gradeNull2: ExplanationData = ExplanationData(
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
