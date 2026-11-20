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

package org.elaastic.moderation

/**
 * Interface describing an object that may be candidate for report.
 *
 * Such a candidate (explanantion, human or AI feedback, can be hidden or removed by a teacher.
 */
interface ReportCandidate {

    /**
     * Flag that indicates if the candidate is hidden by the teacher.
     */
    var hiddenByTeacher: Boolean
    /**
     * Flag that indicates if the candidate is removed by the teacher.
     */
    var removedByTeacher: Boolean

    /**
     * The reason(s) for the report.
     */
    var reportReasons: String?
    /**
     * The comment of the reporter.
     */
    var reportComment: String?

    /**
     * The utility grade of the candidate given by the learner.
     */
    var utilityGrade: UtilityGrade?

    /**
     * Teacher's Utility Grade
     */
    var teacherUtilityGrade: UtilityGrade?
}