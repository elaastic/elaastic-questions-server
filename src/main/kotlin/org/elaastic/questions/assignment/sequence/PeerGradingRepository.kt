package org.elaastic.questions.assignment.sequence

import org.springframework.data.jpa.repository.JpaRepository


interface PeerGradingRepository : JpaRepository<PeerGrading, Long>