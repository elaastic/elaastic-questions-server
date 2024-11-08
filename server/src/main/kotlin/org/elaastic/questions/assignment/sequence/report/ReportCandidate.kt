package org.elaastic.moderation

/**
 * Interface describing an object that may be candidate for report.
 *
 * Such a candidate (explanantion, human or AI feedback, can be hidden or removed by a teacher.
 */
interface ReportCandidate {

    /**
     * Flag that indicates if the candidate is hidden by the teacher.
     */
    var hiddenByTeacher: Boolean
    /**
     * Flag that indicates if the candidate is removed by the teacher.
     */
    var removedByTeacher: Boolean

    /**
     * The reason(s) for the report.
     */
    var reportReasons: String?
    /**
     * The comment of the reporter.
     */
    var reportComment: String?

    /**
     * The utility grade of the candidate given by the learner.
     */
    var utilityGrade: UtilityGrade?

    /**
     * Teacher's Utility Grade
     */
    var teacherUtilityGrade: UtilityGrade?
}