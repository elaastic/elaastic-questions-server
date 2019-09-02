package org.elaastic.questions.terms

import org.elaastic.questions.persistence.AbstractJpaPersistable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Terms(

        @field:NotNull
        var startDate:Date = Date(),

        @field:NotNull
        var isActive:Boolean = true

): AbstractJpaPersistable<Long>()  {

    @Version
    var version:Long? = null

    var endDate:Date? = null

    @OneToMany(mappedBy = "terms", fetch = FetchType.EAGER)
    @MapKey(name = "language")
    @NotNull
    var termsContentsByLanguage:MutableMap<String, TermsContent> = mutableMapOf<String, TermsContent>()

    /**
     * Add terms content to terms
     */
    fun addTermsContent(termsContent: TermsContent) {
        termsContentsByLanguage[termsContent.language] = termsContent
    }

}
