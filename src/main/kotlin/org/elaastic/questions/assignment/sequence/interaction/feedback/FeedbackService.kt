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
package org.elaastic.questions.assignment.sequence.interaction.feedback;

import org.elaastic.questions.assignment.LearnerAssignmentService
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class FeedbackService(
        @Autowired val learnerAssignmentService: LearnerAssignmentService,
        @Autowired val feedbackRepository: FeedbackRepository
) {
    fun getFeedback(learner: User, sequence: Sequence): Feedback? =
            feedbackRepository.findByLearnerAndSequence(learner, sequence)


    fun getSequenceFeedbacks(sequence: Sequence): List<Feedback>? =
            feedbackRepository.findAllBySequence(sequence)

    fun save(feedback: Feedback): Feedback {
        require(
                learnerAssignmentService.isRegistered(
                   feedback.learner, feedback.sequence.assignment!!
                )
        ) { "You must be registered on the assignment to submit a response" }

        feedbackRepository.save(feedback)
        return feedback
    }

    //TODO faire la moyenne des ranking
    fun getAverage(): Int?{

        return null
    }

}
