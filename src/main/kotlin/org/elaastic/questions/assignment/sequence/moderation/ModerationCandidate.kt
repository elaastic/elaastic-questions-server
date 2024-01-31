package org.elaastic.questions.assignment.sequence.moderation

/**
 * Interface describing an object that may be candidate for moderation.
 *
 * Such a candidate (explanantion, human or AI feedback, can be hidden by a teacher.
 */
interface ModerationCandidate {

    /**
     * Flag that indicates if the candidate is hidden by the teacher.
     */
    var hiddenByTeacher: Boolean

    /**
     * Flag that indicates if the candidate is recommended by the system (ONLY FACETOFACE).
     */
    var recommendedBySystem: Boolean

    /**
     * Flag that indicates if the candidate is recommended by the teacher.
     */
    var recommendedByTeacher: Boolean
}