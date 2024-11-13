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
package org.elaastic.activity.response

/**
 * ConfidenceDegree is an enum class that represents the degree of
 * confidence that a student has in their response.
 *
 * @property NOT_CONFIDENT_AT_ALL The student is not confident at all in
 *     their response.
 * @property NOT_REALLY_CONFIDENT The student is not really confident in
 *     their response.
 * @property CONFIDENT The student is confident in their response.
 * @property TOTALLY_CONFIDENT The student is totally confident in their
 *     response.
 */
enum class ConfidenceDegree {
    /** The student is not confident at all in their response. */
    NOT_CONFIDENT_AT_ALL,

    /** The student is not really confident in their response. */
    NOT_REALLY_CONFIDENT,

    /** The student is confident in their response. */
    CONFIDENT,

    /** The student is totally confident in their response. */
    TOTALLY_CONFIDENT
}