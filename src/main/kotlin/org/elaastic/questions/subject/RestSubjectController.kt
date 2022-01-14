package org.elaastic.questions.subject

import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@Transactional
@RequestMapping("/api/subject")
class RestSubjectController(
    @Autowired val subjectService: SubjectService,
    @Autowired val subjectJsonExporter: SubjectExporter,
) {

    @GetMapping("{id}/export-json")
    fun exportToJson(
        authentication: Authentication,
        @PathVariable id: Long,
    ) : ExportSubjectData {
        val user: User = authentication.principal as User

        return subjectService.get(user, id).let { subject ->
            subjectJsonExporter.exportToPojo(subject)
        }
    }
}