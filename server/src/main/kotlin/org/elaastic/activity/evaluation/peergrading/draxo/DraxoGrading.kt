/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.activity.evaluation.peergrading.draxo

import org.elaastic.activity.evaluation.peergrading.draxo.criteria.Criteria
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionId
import java.math.BigDecimal

object DraxoGrading {

    fun computeGrade(draxoEvaluation: DraxoEvaluation) =
        DraxoGrading.computeGrade(
            draxoEvaluation[Criteria.D],
            draxoEvaluation[Criteria.R],
            draxoEvaluation[Criteria.A],
            draxoEvaluation[Criteria.X],
            draxoEvaluation[Criteria.O],
        )

    fun computeGrade(draxoPeerGrading: DraxoPeerGrading) =
        computeGrade(
            draxoPeerGrading.criteriaD,
            draxoPeerGrading.criteriaR,
            draxoPeerGrading.criteriaA,
            draxoPeerGrading.criteriaX,
            draxoPeerGrading.criteriaO,
        )

    /**
     * Compute the grade of a peer grading The grade is the sum of the
     * values of the criteria, otherwise it is null. If the peer grading is
     * unDerstandable and one of the other criteria is filled. If the peer
     * grading is not unDerstandable, the grade is null. If the grader does not
     * know if the response is Relevant, the grade is null. If the grader has
     * no opinion on, if he Agreed, the grade is null.
     *
     * @param d the unDerstandable criteria
     * @param r the Relevant criteria
     * @param a the Agreed criteria
     * @param x the Exhaustive criteria
     * @param o the Optimal criteria
     * @return the grade of the peer grading
     */
    fun computeGrade(
        d: OptionId?,
        r: OptionId?,
        a: OptionId?,
        x: OptionId?,
        o: OptionId?
    ): BigDecimal? =
        when {
            d != Criteria.D[OptionId.YES] -> null // if the peer grading is not unDerstandable, the grade is null
            listOfNotNull(r, a, x, o).isEmpty() -> null // if the peer grading is unDerstandable but no other criteria are filled, the grade is null
            r == Criteria.R[OptionId.DONT_KNOW] -> null // if the grader does not know if the response is Relevant, the grade is null
            a == Criteria.A[OptionId.NO_OPINION] -> null // if the grader has no opinion on, if he Agreed, the grade is null
            else -> Criteria.R.value(r) +
                    Criteria.A.value(a) +
                    Criteria.X.value(x) +
                    Criteria.O.value(o) // else the grade is the sum of the values of the criteria
        }
}