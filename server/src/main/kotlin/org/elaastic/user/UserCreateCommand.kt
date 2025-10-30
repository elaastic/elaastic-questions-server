package org.elaastic.user

/**
 * Command to create a new user.
 * @author John Tranier
 */
data class UserCreateCommand(
    val firstName: String,
    val lastName: String,
    val email: String?,
    val roleId: Role.RoleId,
    val userSource: UserSource,
    val language: String
)