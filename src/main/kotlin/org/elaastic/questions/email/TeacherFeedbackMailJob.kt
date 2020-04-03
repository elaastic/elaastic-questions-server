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

package org.elaastic.questions.email

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class TeacherFeedbackMailJob(
        @Autowired val teacherFeedbackMailService: TeacherFeedbackMailService
) {

    val logger = Logger.getLogger(TeacherFeedbackMailJob::class.java.name)

    @Scheduled(cron = "0 * * * * ?") // every minute
    fun execute() {
        logger.info("Start teacher email job...")
        teacherFeedbackMailService.sendFeedbackReminderEmails()
        logger.info("End teacher email job.")
    }

}
