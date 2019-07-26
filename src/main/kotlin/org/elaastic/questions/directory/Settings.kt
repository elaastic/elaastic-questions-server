package org.elaastic.questions.directory

import org.elaastic.questions.persistence.AbstractJpaPersistable
import javax.persistence.Entity
import javax.persistence.OneToOne
import javax.persistence.Version
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Settings(
        @field:NotNull @field:OneToOne var user: User,
        @field:NotBlank var language: String
) : AbstractJpaPersistable<Long>() {
    @Version
    var version: Long? = null
}