package org.elaastic.questions.player.components.recommendation

import org.elaastic.player.explanations.ExplanationData

class IncorrectAndMeanGradeComparator : Comparator<ExplanationData> {
    override fun compare(expl1: ExplanationData?, expl2: ExplanationData?): Int =

        /* better safe than sorry */
        if (expl1 == null && expl2 == null) 0
        else if (expl1 == null && expl2 != null) -1
        else if (expl1 != null && expl2 == null) 1
        else if (expl1!!.score == expl2!!.score) {
            if (expl1.meanGrade == null && expl2.meanGrade == null) 0
            else if (expl1.meanGrade == null && expl2.meanGrade != null) -1
            else if (expl1.meanGrade != null && expl2.meanGrade == null) 1
            else (expl1.meanGrade!!.minus(expl2.meanGrade!!)).signum()
        } else if (expl1.score == null && expl2.score == null) 0
        else if (expl1.score == null && expl2.score != null) -1
        else if (expl1.score != null && expl2.score == null) 1
        else (expl2.score!!.minus(expl1.score!!)).signum()

}