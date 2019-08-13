package org.elaastic.questions.assignment.sequence

import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author John Tranier
 */
interface LearnerSequenceRepository : JpaRepository<LearnerSequence, Long>