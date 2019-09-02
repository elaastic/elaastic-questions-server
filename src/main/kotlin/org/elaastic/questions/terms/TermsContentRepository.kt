package org.elaastic.questions.terms

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TermsContentRepository : JpaRepository<TermsContent, Long> {

}
