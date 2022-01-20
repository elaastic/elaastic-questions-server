package org.elaastic.questions.test

import org.elaastic.questions.directory.User
import org.elaastic.questions.test.interpreter.FunctionalTestInterpreter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
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
        val subject = functionalTestingService.generateSubject(user)



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