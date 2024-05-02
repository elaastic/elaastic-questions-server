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

package org.elaastic.questions.assignment

/**
 * Enumeration of the different execution contexts for an assignment.
 *
 * @property FaceToFace The assignment is done face to face.
 * @property Distance The assignment is done at a distance context.
 * @property Blended The assignment is done in a blended context.
 */
enum class ExecutionContext {
    /**
     * The assignment is done face to face.
     * That means the student must wait until the teacher opens the next phase.
     * The teacher controls every step of the assignment.
     */
    FaceToFace,

    /**
     * The assignment is done at a distance context.
     * That means each student can complete the sequence at their own pace and directly see the result.
     * The teacher can only control the opening and closing of the sequence.
     */
    Distance,

    /**
     * The assignment is done in a blended context.
     * That means the student can complete the two first phases at their own pace.
     * But the reveal of the result is controlled by the teacher.
     * The teacher can control the opening, closing of the sequence and the publication of the result.
     */
    Blended
}
