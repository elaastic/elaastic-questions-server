package org.elaastic.questions.terms

import org.elaastic.questions.persistence.AbstractJpaPersistable
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
final class TermsContent(
        @field:NotNull
        @field:NotBlank
        var content:String,

        @ManyToOne
        val terms:Terms,

        @field:NotNull
        var language:String = "fr"
): AbstractJpaPersistable<Long>()  {

    init {
        terms.addTermsContent(this)
    }

}
