package org.elaastic.auth

import org.elaastic.common.persistence.AbstractJpaPersistable
import org.elaastic.user.User
import javax.persistence.*

/**
 * This class is used to link an external user to an Elaastic user
 */
@Entity
class UserLink(
    /**
     * The Elaastic user linked to the external user
     */
    @OneToOne
    val user: User,

    /**
     * The provider id (e.g. "cas", "oidc", ...)
     */
    val providerId: String,

    /**
     * The external user id (e.g. "cas:123456", "oidc:123456", ...)
     */
    val linkedUserId: String
) : AbstractJpaPersistable<Long>() {
}