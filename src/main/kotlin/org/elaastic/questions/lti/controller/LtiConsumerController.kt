package org.elaastic.questions.lti.controller

import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.lti.LtiConsumer
import org.elaastic.questions.lti.LtiConsumerRepository
import org.elaastic.questions.persistence.pagination.PaginationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


@Controller
@RequestMapping("/ltiConsumer")
@Transactional
class LtiConsumerController(
        @Autowired val ltiConsumerRepository: LtiConsumerRepository,
        @Autowired val messageBuilder: MessageBuilder
) {
    @GetMapping(value = ["", "/", "/index"])
    fun index(authentication: Authentication,
              model: Model,
              @RequestParam("page") page: Int?,
              @RequestParam("size") size: Int?): String {
        val user: User = authentication.principal as User

        ltiConsumerRepository.findAll(
                PageRequest.of((page ?: 1) - 1, size ?: 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
        ).let {
            model.addAttribute("user", user)
            model.addAttribute("ltiConsumerPage", it)
            model.addAttribute(
                    "pagination",
                    PaginationUtil.buildInfo(
                            it.totalPages,
                            page,
                            size
                    )
            )
        }

        return "/ltiConsumer/index"
    }

    @GetMapping(value = ["/{id}", "{id}/show"])
    fun show(authentication: Authentication, model: Model, @PathVariable id: String): String {
        val user: User = authentication.principal as User

        ltiConsumerRepository.findById(id).get().let {
            model.addAttribute("user", user)
            model.addAttribute("ltiConsumer", it)
        }

        return "/ltiConsumer/show"
    }

    @GetMapping("create")
    fun create(authentication: Authentication, model: Model): String {
        val user: User = authentication.principal as User

        if (!model.containsAttribute("ltiConsumer")) {
            model.addAttribute("ltiConsumer", LtiConsumerData())
        }
        model.addAttribute("user", user)

        return "/ltiConsumer/create"
    }

    @PostMapping("save")
    fun save(authentication: Authentication,
             @Valid @ModelAttribute ltiConsumerData: LtiConsumerData,
             result: BindingResult,
             model: Model,
             response: HttpServletResponse,
             redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", user)
            model.addAttribute("ltiConsumer", ltiConsumerData)
            "/assignment/create"
        } else {
            val ltiConsumer = ltiConsumerData.toEntity()
            ltiConsumerRepository.save(ltiConsumer).let {
                with(messageBuilder) {
                    success(
                            redirectAttributes,
                            message(
                                    "default.updated.message",
                                    "LTI Consumer",
                                    it.key
                            )
                    )
                }
            }
            "redirect:/ltiConsumer/${ltiConsumer.key}"
        }
    }

    @GetMapping("{id}/edit")
    fun edit(authentication: Authentication,
             model: Model,
             @PathVariable id: String): String {
        val user: User = authentication.principal as User

        ltiConsumerRepository.findById(id).get().let {
            model.addAttribute("user", user)
            model.addAttribute("ltiConsumer", it)
        }

        return "/ltiConsumer/edit"
    }

    @PostMapping("{id}/update")
    fun update(authentication: Authentication,
               @Valid @ModelAttribute ltiConsumerData: LtiConsumerData,
               result: BindingResult,
               model: Model,
               @PathVariable id: String,
               response: HttpServletResponse,
               redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", user)
            model.addAttribute("ltiConsumer", ltiConsumerData)
            "/ltiConsumer/edit"
        } else {
            ltiConsumerRepository.findById(id).get().let {
                ltiConsumerData.populate(it)
                ltiConsumerRepository.save(it)
                with(messageBuilder) {
                    success(
                            redirectAttributes,
                            message(
                                    "default.updated.message",
                                    "LTI Consumer",
                                    it.key
                            )
                    )
                }
                "redirect:/ltiConsumer/$id"
            }
        }
    }

    @GetMapping("{id}/delete")
    fun delete(authentication: Authentication,
               @PathVariable id: String,
               redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        ltiConsumerRepository.deleteById(id)

        with(messageBuilder) {
            success(
                    redirectAttributes,
                    message(
                            "default.deleted.message",
                            "LTI Consumer",
                            id
                    )
            )
        }
        return "redirect:/ltiConsumer"
    }


    data class LtiConsumerData(

            @field:NotBlank
            @field:Size(max = 45)
            val consumerName: String? = null,

            @field:NotBlank
            @field:Size(max = 32)
            val secret: String? = null,

            @field:NotBlank
            @field:Size(max = 255)
            val key: String? = null

    ) {
        fun toEntity(): LtiConsumer {
            return LtiConsumer(
                    consumerName!!,
                    secret!!,
                    key!!
            )
        }

        fun populate(ltiConsumer: LtiConsumer) {
            ltiConsumer.consumerName = this.consumerName!!
            ltiConsumer.secret = this.secret!!
        }
    }
}
