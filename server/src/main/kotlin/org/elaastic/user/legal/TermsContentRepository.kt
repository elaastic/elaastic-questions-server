package org.elaastic.user.legal

import org.springframework.data.jpa.repository.JpaRepository

interface TermsContentRepository : JpaRepository<TermsContent, Long> {

}