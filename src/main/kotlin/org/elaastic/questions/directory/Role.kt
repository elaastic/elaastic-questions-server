package org.elaastic.questions.directory

import org.elaastic.questions.persistence.AbstractJpaPersistable
import javax.persistence.*


/**
 * @author John Tranier
 */
@Entity
class Role(
        @field:Column(name = "authority")
        var name: String
) : AbstractJpaPersistable<Long>() {

    enum class RoleId(val roleName: String) {
        STUDENT("STUDENT_ROLE"),
        TEACHER("TEACHER_ROLE"),
        ADMIN("ADMIN_ROLE")
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    
}