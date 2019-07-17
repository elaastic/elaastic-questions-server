package org.elaastic.questions.directory

import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.cache.annotation.Cacheable
import java.io.Serializable
import javax.persistence.*


/**
 * @author John Tranier
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
class Role(
        @field:Column(name = "authority")
        var name: String
) : AbstractJpaPersistable<Long>(), Serializable {

    enum class RoleId(val roleName: String) {
        STUDENT("STUDENT_ROLE"),
        TEACHER("TEACHER_ROLE"),
        ADMIN("ADMIN_ROLE")
    }

}