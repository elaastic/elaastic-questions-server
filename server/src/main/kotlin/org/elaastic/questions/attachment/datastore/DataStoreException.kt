/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
