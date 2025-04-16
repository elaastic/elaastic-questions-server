package org.elaastic.auth.oauth

/**
 * Exception throws during problems related role management.
 */
class RoleException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}