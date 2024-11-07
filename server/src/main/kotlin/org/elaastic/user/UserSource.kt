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
package org.elaastic.user

/**
 * Describe the source of the user.
 *
 * @property ELAASTIC The user is created by Elaastic website
 * @property ANONYMOUS The user is anonymous (not connected)
 * @property LMS The user is created by an LMS (Learning Management System)
 *     like Moodle
 * @property CAS The user is created by a CAS (Central Authentication Service)
 * @see User
 */
enum class UserSource {
    /** The user is created by Elaastic website. Through the form on the website */
    ELAASTIC,

    /**
     * The user is anonymous (not connected). It'd append when a teacher allows
     * anonymous user to access an assignment
     */
    ANONYMOUS,

    /** The user is created by an LMS (Learning Management System) like Moodle. */
    LMS,

    /**
     * The user is created by a CAS.
     * Student are able to connect to Elaastic with their ENT (Espace Numérique de Travail) account, for example.
     * @see <a href="https://en.wikipedia.org/wiki/Central_Authentication_Service">CAS</a>
     */
    CAS
}