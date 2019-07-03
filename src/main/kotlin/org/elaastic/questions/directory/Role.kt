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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // TODO Check this
    var id: Long? = null
    
}