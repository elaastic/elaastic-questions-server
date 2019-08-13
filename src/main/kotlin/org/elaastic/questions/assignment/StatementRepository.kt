package org.elaastic.questions.assignment

import org.springframework.data.jpa.repository.JpaRepository



interface StatementRepository : JpaRepository<Statement, Long>