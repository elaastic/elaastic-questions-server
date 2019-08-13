package org.elaastic.questions.assignment

import org.springframework.data.jpa.repository.JpaRepository


interface LearnerAssignmentRepository : JpaRepository<LearnerAssignment, Long>