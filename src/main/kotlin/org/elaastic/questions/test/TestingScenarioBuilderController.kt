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

package org.elaastic.questions.test

import org.elaastic.questions.directory.User
import org.elaastic.questions.test.interpreter.FunctionalTestInterpreter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("test")
@PreAuthorize("@featureManager.isActive(@featureResolver.getFeature('FUNCTIONAL_TESTING'))")
@Transactional
class TestingScenarioBuilderController(
    @Autowired val functionalTestingService: FunctionalTestingService,
) {

    /**
     * Generate a subject for the current user with :
     * - 3 questions (one of each type)
     * - 3 assignments (for easily testing each context)
     */
    @GetMapping("generate-subject")
    fun generateSubject(
        authentication: Authentication,
    ): String {

        val user: User = authentication.principal as User
        val subject = functionalTestingService.generateSubjectWithQuestionsAndAssignments(user)



        return "redirect:/subject/${subject.id}"
    }

    @PostMapping("execute-script")
    fun executeScript(
        @RequestParam sequenceId: Long,
        @RequestParam assignmentId: Long,
        @ModelAttribute("script") script: String,
    ) : String {

        functionalTestingService.executeScript(
            sequenceId,
            FunctionalTestInterpreter().parse(script)
        )

        return "redirect:/player/assignment/${assignmentId}/play/sequence/${sequenceId}"
    }
}