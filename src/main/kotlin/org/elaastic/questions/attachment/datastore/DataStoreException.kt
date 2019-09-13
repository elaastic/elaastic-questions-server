package org.elaastic.questions.attachment.datastore

/**
 * Exception thrown by the Data Store module.
 */
class DataStoreException : Exception {

    /**
     * Constructs a new instance of this class with the specified detail
     * message.
     *
     * @param message the detailed message.
     */
    constructor(message: String) : super(message) {}

    /**
     * Constructs a new instance of this class with the specified detail
     * message and root cause.
     *
     * @param message the detailed message.
     * @param cause   root failure cause
     */
    constructor(message: String, cause: Throwable) : super(message, cause) {}

    /**
     * Constructs a new instance of this class with the specified root cause.
     *
     * @param rootCause root failure cause
     */
    constructor(rootCause: Throwable) : super(rootCause) {}

}
