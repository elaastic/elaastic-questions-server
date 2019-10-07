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

package org.elaastic.questions.test.directive



inline fun <T, R> T.tExpect(that:String? = null, block: (T) -> R): R  {
    return this.let(block)
}

inline fun <T, R> T.tThen(that:String? = null, block: (T) -> R): R  {
    return this.let(block)
}

inline fun <T, R> T.tWhen(that:String? = null, block: (T) -> R): R  {
    return this.let(block)
}

fun Any.tNoProblem(that:String? = null) {
    // Just do nothing
}

inline fun <T> tWhen(that:String? = null,  block: () -> T) : T {
    return block()
}

inline fun <T> tGiven(that:String? = null, block: () -> T) : T {
    return block()
}
