package org.elaastic.questions.assignment.sequence.moderation

/**
 * Marker interface for moderation candidate repository
 */
interface ModerationCandidateRepository {

    /**
     * Save the candidate.
     *
     * @param moderationCandidate the candidate to save.
     */
    fun save(moderationCandidate: ModerationCandidate)

}