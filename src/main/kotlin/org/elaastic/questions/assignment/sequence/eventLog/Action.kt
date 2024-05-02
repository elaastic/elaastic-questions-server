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

package org.elaastic.questions.assignment.sequence.eventLog

/**
 * An action that can be performed by a user.
 *
 * @property OPEN open action
 * @property CLOSE close action
 * @property START start action
 * @property STOP stop action
 * @property SKIP skip action
 * @property PUBLISH publish action
 * @property UNPUBLISH unpublish action
 * @property UPDATE update action
 * @property LOAD load action
 * @property CLICK click action
 * @property RESTART restart action
 * @property CONSULT consult action
 */
enum class Action(val propertyString: String) {
    OPEN("open"),
    CLOSE("close"),
    START("start"),
    STOP("stop"),
    SKIP("skip"),
    PUBLISH("publish"),
    UNPUBLISH("unpublish"),
    UPDATE("update"),
    LOAD("load"),
    CLICK("click"),
    RESTART("restart"),
    CONSULT("consult");

    companion object {
        fun from(findValue: String): Action = values().first { it.propertyString == findValue }
    }
}
