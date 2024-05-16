package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
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