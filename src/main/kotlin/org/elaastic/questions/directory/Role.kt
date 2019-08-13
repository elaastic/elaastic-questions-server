package org.elaastic.questions.directory

import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.core.GrantedAuthority
import java.io.Serializable
import javax.persistence.*



@Entity
@Cacheable("roles")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
class Role(
        @field:Column(name = "authority")
        var name: String
) : AbstractJpaPersistable<Long>(), Serializable, GrantedAuthority {

    enum class RoleId(val roleName: String) {
        STUDENT("STUDENT_ROLE"),
        TEACHER("TEACHER_ROLE"),
        ADMIN("ADMIN_ROLE")
    }

    override fun getAuthority(): String {
        return name
    }
}