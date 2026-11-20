/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.assignment

/**
 * The revision mode of an assignment.
 *
 * This revision mode is for the **Konsolidation** app.
 *
 * @property NotAtAll The assignment is not meant to be revised.
 * @property AfterTeachings The assignment is meant to be revised after
 *    teachings.
 * @property Immediately The assignment is meant to be revised immediately
 *    after answering.
 */
enum class ReadyForConsolidation {
    /** The assignment is not meant to be revised. */
    NotAtAll,

    /** The assignment is meant to be revised after teachings. */
    AfterTeachings,

    /** The assignment is meant to be revised immediately after answering. */
    Immediately
}