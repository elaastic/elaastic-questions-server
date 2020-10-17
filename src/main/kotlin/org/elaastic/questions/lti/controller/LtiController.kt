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

package org.elaastic.questions.lti.controller

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.lti.LmsService
import org.elaastic.questions.lti.LmsUser
import org.elaastic.questions.lti.LtiConsumerService
import org.elaastic.questions.lti.oauth.OauthService
import org.elaastic.questions.terms.TermsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession


@Controller
class LtiController(
        @Autowired val lmsService: LmsService,
        @Autowired val ltiConsumerService: LtiConsumerService,
        @Autowired val oauthService: OauthService,
        @Autowired val termsService: TermsService,
        @Autowired val roleService: RoleService,
        @Autowired val assignmentService: AssignmentService
) {

    internal var logger = Logger.getLogger(LtiController::class.java.name)

    @PostMapping("/launch")
    fun launch(ltiLaunchData: LtiLaunchData,
               request: HttpServletRequest,
               response: HttpServletResponse,
               model: Model,
               redirectAttributes: RedirectAttributes,
               locale: Locale): String {
        val session = startNewSession(request)
        return try {
            oauthService.validateOauthRequest(request)
            ltiConsumerService.touchLtiConsumer(
                    ltiLaunchData.oauth_consumer_key,
                    ltiLaunchData.tool_consumer_info_product_family_code,
                    ltiLaunchData.tool_consumer_info_version,
                    ltiLaunchData.tool_consumer_instance_guid,
                    ltiLaunchData.lti_version
            )
            ltiLaunchData.roleService = roleService
            val lmsUser = lmsService.findLmsUser(
                    ltiLmsKey = ltiLaunchData.oauth_consumer_key,
                    ltiUserId = ltiLaunchData.user_id)
            if (lmsUser != null) {
                authenticateLmsUser(session, lmsUser)
                redirectToAssignment(ltiLaunchData, lmsUser, redirectAttributes)
            } else {
                setLtiLaunchDataInSession(ltiLaunchData, session)
                model.addAttribute("termsContent", termsService.getTermsContentByLanguage(locale.language))
                model.addAttribute("firstName", ltiLaunchData.lis_person_name_given ?: "User")
                model.addAttribute("lastName", ltiLaunchData.lis_person_name_family ?: ltiLaunchData.user_id)
                "/terms/lti_terms_consent_form"
            }
        } catch (e: Exception) {
            e.stackTrace.iterator().forEach {
                logger.severe(it.toString())
            }
            "redirect:${ltiLaunchData.getRedirectUrlWithErrorMessage(e.message!!)}"
        }
    }

    @GetMapping("/launch/consent")
    fun collectConsent(request: HttpServletRequest,
                       @RequestParam("withConsent") withConsent: Boolean = false,
                        redirectAttributes: RedirectAttributes): String {
        val ltiLaunchData = getLtiLaunchDataFromSession(request.session)
        return try {
            if (withConsent) {
                val lmsUser = lmsService.getLmsUser(ltiLaunchData.toLtiUser())
                authenticateLmsUser(request.session, lmsUser)
                redirectToAssignment(ltiLaunchData, lmsUser, redirectAttributes)
            } else {
                logger.severe("Consent not given")
                "redirect:${ltiLaunchData.getRedirectUrlWithErrorMessage("no_consent_given_by_user")}"
            }
        } catch (e: Exception) {
            e.stackTrace.iterator().forEach {
                logger.severe(it.toString())
            }
            "redirect:${ltiLaunchData.getRedirectUrlWithErrorMessage(e.message!!)}"
        }

    }

    private fun authenticateLmsUser(session: HttpSession, lmsUser: LmsUser) {
        UsernamePasswordAuthenticationToken(lmsUser.user, null, lmsUser.user.authorities).let {
            val secContext = SecurityContextHolder.getContext()
            secContext.authentication = it
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, secContext)
        }
    }

    private fun redirectToAssignment(ltiLaunchData: LtiLaunchData, lmsUser: LmsUser, redirectAttributes: RedirectAttributes): String {
        val assignment = lmsService.getLmsAssignment(
                lmsUser = lmsUser,
                ltiActivity = ltiLaunchData.toLtiActivity()
        ).assignment
        val user = lmsUser.user
        assignmentService.registerUser(user, assignment)
        return when {
            assignment.sequences.isNotEmpty() -> "redirect:/player/assignment/${assignment.id}/play"
            user == assignment.owner -> {
                redirectAttributes.addAttribute("activeTab", "questions")
                "redirect:/subject/${assignment.subject!!.id}"
            }
            else -> {
                logger.severe("Student cannot access empty assignment")
                "redirect:${ltiLaunchData.getRedirectUrlWithErrorMessage("student_cannot_access_empty_assignment")}"
            }
        }
    }

    private fun startNewSession(request: HttpServletRequest): HttpSession {
        request.characterEncoding = "UTF-8"
        request.session.invalidate()
        return request.getSession(true)
    }

    private fun setLtiLaunchDataInSession(ltiLaunchData: LtiLaunchData, session: HttpSession) {
        session.setAttribute("ltiLaunchData", ltiLaunchData)
    }

    private fun getLtiLaunchDataFromSession(session: HttpSession): LtiLaunchData {
        return session.getAttribute("ltiLaunchData") as LtiLaunchData
    }

}
