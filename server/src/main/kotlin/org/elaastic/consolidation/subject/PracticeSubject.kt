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

package org.elaastic.consolidation.subject

import com.fasterxml.jackson.annotation.JsonIgnore
import org.elaastic.consolidation.subject.question.PracticeQuestion
import org.elaastic.assignment.Assignment
import java.util.*

/**
 * A practice subject contains questions on which learners can practice.
 * Practice subjects are constructed from sequences played by learners during assignments.
 * Each question of a practice subject is attached to the 3 best ranked learners explanations
 * provided during the elaastic sequence execution. Those explanations will be used as feedbacks
 * for learners practicing the subject afterward.
 *
 *
 * @author John Tranier
 */
class PracticeSubject(
    id: UUID,
    title: String,

    @JsonIgnore
    val questions: List<PracticeQuestion>,

    @JsonIgnore
    val learners: Set<PracticeLearner>,

    @JsonIgnore
    val topic: PracticeTopic? = null,
) : SummaryPracticeSubject(id, title) {

    constructor(
        assignment: Assignment,
        questions: List<PracticeQuestion>,
        learners: Set<PracticeLearner>,
        topic: PracticeTopic? = null,
    ) : this(
        id = assignment.globalId,
        title = assignment.title,
        questions = questions,
        learners = learners,
        topic = topic,
    )
}