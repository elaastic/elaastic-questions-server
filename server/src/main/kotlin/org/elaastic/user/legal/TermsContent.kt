package org.elaastic.user.legal

import org.elaastic.common.persistence.AbstractJpaPersistable
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
final class TermsContent(
    @field:NotNull
        @field:NotBlank
        var content:String,

    @ManyToOne
        val terms: Terms,

    @field:NotNull
        var language:String = "fr"
): AbstractJpaPersistable<Long>()  {

    init {
        terms.addTermsContent(this)
    }

}