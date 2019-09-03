package org.elaastic.questions.directory

import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.elaastic.questions.terms.Terms
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
final class UserConsent(

        @NotNull
        @NotBlank
        val username:String,

        @ManyToOne
        @NotNull
        val terms: Terms,

        @field:NotNull
        var collectDate: Date = Date()
): AbstractJpaPersistable<Long>()  {

}
