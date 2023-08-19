package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId

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

    fun computeGrade(
        d: OptionId?,
        r: OptionId?,
        a: OptionId?,
        x: OptionId?,
        o: OptionId?
    ) =
        when {
            d != Criteria.D[OptionId.YES] -> null
            listOfNotNull(r, a, x, o).isEmpty() -> null
            r == Criteria.R[OptionId.DONT_KNOW] -> null
            a == Criteria.A[OptionId.NO_OPINION] -> null
            else -> Criteria.R.value(r) +
                    Criteria.A.value(a) +
                    Criteria.X.value(x) +
                    Criteria.O.value(o)
        }
}