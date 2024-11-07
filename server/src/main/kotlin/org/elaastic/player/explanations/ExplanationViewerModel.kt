package org.elaastic.player.explanations

interface ExplanationViewerModel {
    val hasChoice: Boolean
    val explanationsExcerpt: List<ExplanationData>
    val hasMoreThanExcerpt: Boolean
    val hasHiddenByTeacherExplanations: Boolean?
    val nbExplanations: Int
    val studentsIdentitiesAreDisplayable: Boolean
    val teacherExplanation: TeacherExplanationData?
    val nbRecommendedExplanations: Int
}