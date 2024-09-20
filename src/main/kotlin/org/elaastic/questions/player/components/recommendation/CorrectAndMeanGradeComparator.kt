package org.elaastic.questions.player.components.recommendation

import org.elaastic.questions.player.components.explanationViewer.ExplanationData

class CorrectAndMeanGradeComparator : Comparator<ExplanationData> {
    override fun compare(expl1: ExplanationData?, expl2: ExplanationData?): Int =
        when {
            expl1 == null && expl2 == null -> 0
            expl1 == null -> -1
            expl2 == null -> 1
            expl1.score == expl2.score -> when {
                expl1.meanGrade == null && expl2.meanGrade == null -> 0
                expl1.meanGrade == null -> -1
                expl2.meanGrade == null -> 1
                else -> (expl1.meanGrade - expl2.meanGrade).signum()
            }

            expl1.score == null && expl2.score == null -> 0
            expl1.score == null -> -1
            expl2.score == null -> 1
            else -> (expl1.score - expl2.score).signum()
        }

}