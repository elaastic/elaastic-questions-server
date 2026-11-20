package org.elaastic.material.instructional.subject

import org.elaastic.assignment.AssignmentController
import org.elaastic.assignment.AssignmentService
import org.elaastic.common.persistence.pagination.PaginationUtil
import org.elaastic.common.web.ControllerUtil
import org.elaastic.common.web.MessageBuilder
import org.elaastic.material.instructional.course.Course
import org.elaastic.material.instructional.course.CourseService
import org.elaastic.material.instructional.question.attachment.AttachmentService
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.material.instructional.statement.StatementController
import org.elaastic.material.instructional.statement.StatementService
import org.elaastic.user.PrincipalUserResolver
import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Controller
@RequestMapping("/subject", "/elaastic-questions/subject")
@Transactional
class SubjectController(
    @Autowired val subjectService: SubjectService,
    @Autowired val courseService: CourseService,
    @Autowired val statementService: StatementService,
    @Autowired val attachmentService: AttachmentService,
    @Autowired val messageBuilder: MessageBuilder,
    @Autowired val assignmentService: AssignmentService,
    @Autowired val sharedSubjectService: SharedSubjectService,
    @Autowired val subjectExporter: SubjectExporter,
    @Autowired val messageSource: MessageSource,
) {

    @GetMapping(value = ["", "/", "/index"])
    fun index(
        authentication: Authentication,
        model: Model,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        subjectService.findAllByOwner(
            user,
            PageRequest.of((page ?: 1) - 1, size ?: 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
        ).let {
            model["user"] = user
            model["subjectPage"] = it
            model["pagination"] = PaginationUtil.buildInfo(
                it.totalPages,
                page,
                size
            )
        }

        return "subject/index"
    }

    @GetMapping("/{subjectId}/download-json")
    @PreAuthorize("@featureManager.isActive(@featureResolver.getFeature('IMPORT_EXPORT'))")
    fun downloadAsJson(
        authentication: Authentication,
        @PathVariable subjectId: Long,
    ): ResponseEntity<ByteArray> {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        return subjectService.get(user, subjectId).let { subject ->
            val bytes = subjectExporter.exportToJson(subject).toByteArray()
            val filename = subject.title
                .replace("\\s".toRegex(), "_")
                .replace("\\W+".toRegex(), "")
                .take(24) +
                    ".json"
            ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment;filename=${filename}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(bytes.size.toLong())
                .body(bytes)
        }
    }

    @GetMapping("/{subjectId}/download-zip")
    @PreAuthorize("@featureManager.isActive(@featureResolver.getFeature('IMPORT_EXPORT'))")
    fun downloadAsZip(
        authentication: Authentication,
        @PathVariable subjectId: Long,
        response: HttpServletResponse,
    ) {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        return subjectService.get(user, subjectId).let { subject ->
            val filename = subject.title
                .replace("\\s".toRegex(), "_")
                .replace("\\W+".toRegex(), "")
                .take(24)


            response.contentType = "application/octet-stream"
            response.setHeader("Content-Disposition", "attachment;filename=${filename}.elaastic.zip")
            response.status = HttpServletResponse.SC_OK
            subjectExporter.exportToZip(
                subject,
                "${filename}.elaastic.json",
                response.outputStream,
            )
        }
    }

    /** Show upload form to import a Subject from a JSON file */
    @GetMapping("/upload-form")
    @PreAuthorize("@featureManager.isActive(@featureResolver.getFeature('IMPORT_EXPORT'))")
    fun showUploadForm(
        authentication: Authentication,
        model: Model,
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        model["user"] = user

        return "subject/upload-form-zip"
    }

    @PostMapping("/do-upload-zip")
    @PreAuthorize("@featureManager.isActive(@featureResolver.getFeature('IMPORT_EXPORT'))")
    fun doUploadZip(
        authentication: Authentication,
        model: Model,
        locale: Locale,
        @RequestParam("zipFile") zipFile: MultipartFile,
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        return if (zipFile.isEmpty) {
            model["user"] = user
            model["messageContent"] = messageSource.getMessage("subject.file.mandatory", emptyArray(), locale)
            model["messageType"] = "error"

            "/subject/upload-form-zip"
        } else {
            val subject = subjectExporter.importFromZip(user, zipFile.inputStream)

            "redirect:/subject/${subject.id}"
        }

    }

    @GetMapping(value = ["/{subjectId}", "{subjectId}/show"])
    fun show(
        authentication: Authentication,
        model: Model,
        @PathVariable subjectId: Long,
        httpServletRequest: HttpServletRequest,
        @RequestParam("activeTab", defaultValue = "questions") activeTab: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        model["user"] = user

        val subject: Subject = subjectService.get(user, subjectId, fetchStatementsAndAssignments = true)
        model["subject"] = subject

        val statements: MutableList<Statement> = ArrayList()
        for (statement: Statement in subject.statements) {
            if (!statements.contains(statement)) statements.add(statement)
        }
        model["statements"] = statements
        model["listCourse"] = courseService.findAllByOwner(user)
        model["alreadyImported"] = subjectService.isUsedAsParentSubject(user, subject)
        model["subjectData"] = SubjectData(owner = user)
        model["activeTab"] = activeTab
        model["serverBaseUrl"] = ControllerUtil.getServerBaseUrl(httpServletRequest)

        subjectService.findAllByOwner(
            user,
            PageRequest.of((page ?: 1) - 1, size ?: 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
        ).let {
            model["subjects"] = it.content
            var firstSubject = Subject("NoSubject", user)
            if (it.content.isNotEmpty())
                firstSubject = it.content[0]

            model["firstSubject"] = firstSubject
        }

        return "subject/show"
    }

    @GetMapping("create")
    fun create(authentication: Authentication, model: Model): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        if (!model.containsAttribute("subjectData")) {
            model["subjectData"] = SubjectData(owner = user)
        }
        model["user"] = user
        model["listCourse"] = courseService.findAllByOwner(user)

        return "subject/create"
    }

    @PostMapping("save")
    fun save(
        authentication: Authentication,
        @Valid @ModelAttribute subjectData: SubjectData,
        result: BindingResult,
        model: Model,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model["user"] = user
            model["subject"] = subjectData

            "/subject/create"
        } else {
            val subject = subjectData.toEntity()
            subjectService.save(subject)
            redirectAttributes.addAttribute("activeTab", "questions")

            "redirect:/subject/${subject.id}"
        }
    }

    @PostMapping("{subjectId}/addStatement")
    fun addStatement(
        authentication: Authentication,
        @RequestParam("fileToAttached") fileToAttached: MultipartFile,
        @Valid @ModelAttribute statementData: StatementController.StatementData,
        result: BindingResult,
        model: Model,
        @PathVariable subjectId: Long,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        val subject = subjectService.get(user, subjectId, fetchStatementsAndAssignments = true)

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()

            model["user"] = user
            model["subject"] = subject
            model["statement"] = statementData
            model["nbStatement"] = subject.statements.size

            "subject/statement/create"
        } else {
            val statementSaved = subjectService.addStatement(subject, statementData.toEntity(user))
            statementService.updateFakeExplanationList(
                statementSaved,
                statementData.fakeExplanations
            )
            attachedFileIfAny(fileToAttached, statementSaved)

            redirectAttributes["activeTab"] = "questions"

            "redirect:/subject/${subject.id}"
        }
    }

    private fun attachedFileIfAny(fileToAttached: MultipartFile, it: Statement) {
        if (!fileToAttached.isEmpty) {
            attachmentService.saveStatementAttachment(
                it,
                StatementController.createAttachment(fileToAttached),
                fileToAttached.inputStream
            )
        }
    }

    @GetMapping("{subjectId}/addStatement")
    fun addStatement(
        authentication: Authentication,
        model: Model,
        @PathVariable subjectId: Long
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        val subject = subjectService.get(user, subjectId)

        model["user"] = user
        model["subject"] = subject
        model["nbStatement"] = subject.statements.size
        model["statementData"] = StatementController.StatementData(Statement.createDefaultStatement(user))

        return "subject/statement/create"
    }

    @PostMapping("{subjectId}/addAssignment")
    fun addAssignment(
        authentication: Authentication,
        @Valid @ModelAttribute assignmentData: AssignmentController.AssignmentData,
        result: BindingResult,
        model: Model,
        response: HttpServletResponse,
        @PathVariable subjectId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        val subject = subjectService.get(user, subjectId)

        model["user"] = user
        model["subject"] = subject

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model["user"] = user
            model["assignment"] = assignmentData

            "redirect:/subject/${subject.id}/addAssignment"
        } else {
            val assignment = assignmentData.toEntity()
            if (assignment.audience.isBlank())
                assignment.audience = "na"
            assignmentService.save(assignment)
            subjectService.addAssignment(subject, assignment)

            redirectAttributes.addAttribute("activeTab", "assignments")

            "redirect:/subject/${subject.id}"
        }

    }

    @GetMapping("{subjectId}/addAssignment")
    fun addAssignment(
        authentication: Authentication,
        model: Model,
        @PathVariable subjectId: Long
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        val subject = subjectService.get(user, subjectId)

        model["user"] = user
        model["nbAssignments"] = subject.assignments.size
        if (!model.containsAttribute("assignment")) {
            model["assignment"] = AssignmentController.AssignmentData(
                owner = user,
                subject = subject,
                title = subject.title
            ).toEntity()
        }

        return "assignment/create"
    }

    @PostMapping("{subjectId}/update")
    fun update(
        authentication: Authentication,
        @Valid @ModelAttribute subjectData: SubjectData,
        result: BindingResult,
        model: Model,
        @PathVariable subjectId: Long,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        model["user"] = user

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model["subject"] = subjectData
            redirectAttributes.addAttribute("activeTab", "questions")

            "redirect:/subject/$subjectId"
        } else {
            subjectService.get(user, subjectId).let {
                it.updateFrom(subjectData.toEntity())
                subjectService.save(it)

                with(messageBuilder) {
                    success(
                        redirectAttributes,
                        message(
                            "subject.updated.message",
                            message("subject.label"),
                            it.title
                        )
                    )
                }
                model["subject"] = it
                redirectAttributes.addAttribute("activeTab", "questions")

                "redirect:/subject/$subjectId"
            }
        }
    }

    @GetMapping("{subjectId}/delete")
    fun delete(
        authentication: Authentication,
        @PathVariable subjectId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        val subject = subjectService.get(user, subjectId)
        courseService.removeSubject(user, subject)

        with(messageBuilder) {
            success(
                redirectAttributes,
                message(
                    "subject.deleted.message",
                    message("subject.label"),
                    subject.title
                )
            )
        }

        return "redirect:/subject"
    }

    @GetMapping("/shared")
    fun shared(
        authentication: Authentication,
        model: Model,
        @RequestParam("globalId") globalId: String?,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        require(globalId.isNullOrBlank().not()) {
            messageBuilder.message("subject.share.empty.globalId")
        }

        subjectService.findByGlobalId(UUID.fromString(globalId)).let {
            if (it == null) {
                throw EntityNotFoundException(
                    messageBuilder.message("subject.globalId.does.not.exist")
                )
            }

            subjectService.sharedToTeacher(user, it)
            redirectAttributes.addAttribute("activeTab", "questions")

            return "redirect:/subject/${it.id}/show"
        }
    }


    @GetMapping(value = ["/shared_index"])
    fun sharedIndex(
        authentication: Authentication,
        model: Model,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        var sharedSubjectPage: Page<Subject>?
        subjectService.findAllSharedSubjects(
            user,
            PageRequest.of((page ?: 1) - 1, size ?: 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
        ).let {
            model["user"] = user
            sharedSubjectPage = it
            model.addAttribute("sharedSubjectPage", sharedSubjectPage)
            model["pagination"] = PaginationUtil.buildInfo(
                it.totalPages,
                page,
                size
            )
        }
        val sharedInfos: MutableList<SharedSubject> = ArrayList()
        for (subject: Subject in sharedSubjectPage!!.content) {
            sharedInfos.add(sharedSubjectService.getSharedSubject(user, subject)!!)
        }
        model["sharedInfos"] = sharedInfos

        return "subject/shared_index"
    }

    @GetMapping(value = ["{subjectId}/importSubject"])
    fun importSubject(
        authentication: Authentication,
        model: Model,
        @PathVariable subjectId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        val sharedSubject = subjectService.get(user, subjectId)
        val importedSubject = subjectService.import(user, sharedSubject)
        with(messageBuilder) {
            success(
                redirectAttributes,
                message(
                    "subject.imported.message",
                    message("subject.label"),
                    importedSubject.title
                )
            )
        }
        redirectAttributes.addAttribute("activeTab", "questions")

        return "redirect:/subject/${importedSubject.id}/show"
    }

    @GetMapping(value = ["{id}/duplicateSubject"])
    fun duplicateSubject(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        val originalSubject = subjectService.get(user, id)
        val duplicatedSubject = subjectService.duplicate(user, originalSubject)
        with(messageBuilder) {
            success(
                redirectAttributes,
                message(
                    "subject.duplicated.message",
                    message("subject.label"),
                    duplicatedSubject.title
                )
            )
        }
        redirectAttributes.addAttribute("activeTab", "questions")

        return "redirect:/subject/${duplicatedSubject.id}/show"
    }


    data class SubjectData(
        var id: Long? = null,
        var version: Long? = null,
        @field:NotBlank var title: String? = null,
        @field:NotNull var owner: User? = null,
        var course: Course? = null
    ) {
        fun toEntity(): Subject {
            return Subject(title!!, owner!!, course = course).also {
                it.id = id
                it.version = version
            }
        }
    }

}