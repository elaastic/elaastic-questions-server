package org.elaastic.user

/**
 * Defines an external reference to a User, which is the pair of the identifier of
 * the external source combined to the user identifier on this source
 * @author John Tranier
 */
data class ExternalUserRef(val sourceId: String, val userId: String)