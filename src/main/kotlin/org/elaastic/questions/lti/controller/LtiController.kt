package org.elaastic.questions.lti.controller

import org.elaastic.questions.lti.LmsAssignment
import org.elaastic.questions.lti.LmsService
import org.elaastic.questions.lti.LmsUser
import org.elaastic.questions.lti.oauth.OauthService
import org.elaastic.questions.terms.TermsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.tsaap.lti.tp.Callback
import org.tsaap.lti.tp.DataConnector
import org.tsaap.lti.tp.ToolProvider
import org.tsaap.lti.tp.dataconnector.JDBC
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.sql.DataSource


@Controller
class LtiController(
        @Autowired val dataSource: DataSource,
        @Autowired val lmsService: LmsService,
        @Autowired val authenticationManager: AuthenticationManager,
        @Autowired val oauthService: OauthService,
        @Autowired val termsService: TermsService
) : Callback {

    internal var logger = Logger.getLogger(LtiController::class.java.name)

    @PostMapping("/launch")
    fun launch(ltiLaunchData: LtiLaunchData, request: HttpServletRequest, response: HttpServletResponse): String {
        startNewSession(request)
        oauthService.validateOauthRequest(request)
        // TODO IN CASE OF CREATION OF LMS USER
        // TODO Authenticate oauth user with OAUTH role
        // TODO Give access to "/ltiConsent" only to role OAUTH
        // TODO No more needs of LTI_USER table
        request.session.setAttribute("ltiLaunchData", ltiLaunchData)
        return "redirect:/ltiConsent"
    }

    @GetMapping("/ltiConsent")
    fun ltiConsent(session: HttpSession, model: Model, locale: Locale):String {
        val launchData: LtiLaunchData = session.getAttribute("ltiLaunchData") as LtiLaunchData
        model.addAttribute("termsContent",termsService.getTermsContentByLanguage(locale.language))
        model.addAttribute("firstName", launchData.lis_person_name_given)
        model.addAttribute("lastName", launchData.lis_person_name_family)
        return "/terms/lti_terms_consent_form"
    }

    /**
     * Execute the launch of the tool provider activity
     *
     * @param toolProvider the tool provider
     * @return true if the launch is OK
     */
    override fun execute(toolProvider: ToolProvider): Boolean {
        initializeUserSession(toolProvider)
        val lmsUser = lmsService.getLmsUser(toolProvider)
        val lmsAssignment = lmsService.getLmsAssignment(toolProvider, lmsUser)
        updateServerUrl(toolProvider, lmsUser, lmsAssignment)
        return true

    }

    private fun updateServerUrl(toolProvider: ToolProvider, lmsUser: LmsUser, lmsAssignment: LmsAssignment) {
        val serverUrlFromTP = toolProvider.request.requestURL.toString()
        val serverUrlRoot = serverUrlFromTP.substring(0, serverUrlFromTP.lastIndexOf("/"))
        val result: String
        result = if (lmsUser.user.enabled) {
            authenticateLmsUser(toolProvider.request, lmsUser) // not good : have to separate consent from enabling
            "${serverUrlRoot}/player/ltiLaunch/${lmsAssignment.assignment.id}"
        } else {
            "${serverUrlRoot}/terms?username=${lmsUser.user.username}&assignment_id=${lmsAssignment.assignment.id}"
        }
        toolProvider.redirectUrl = result
    }

    private fun authenticateLmsUser(request: HttpServletRequest, lmsUser: LmsUser) {
        val user = lmsUser.user
        val authReq = UsernamePasswordAuthenticationToken(user.username, user.password)
        val auth = authenticationManager.authenticate(authReq)
        val secContext = SecurityContextHolder.getContext()
        secContext.authentication = auth
        val session = request.getSession(true)
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, secContext)
    }

    private fun initializeUserSession(toolProvider: ToolProvider) {
        with(toolProvider.request.session) {
            setAttribute("consumer_key", toolProvider.consumer.key)
            setAttribute("resource_id", toolProvider.resourceLink.id)
            setAttribute("user_consumer_key", toolProvider.user.resourceLink.consumer.key)
            setAttribute("user_id", toolProvider.user.idForDefaultScope)
            setAttribute("isStudent", toolProvider.user.isLearner)
            setAttribute("lti_context_id", toolProvider.resourceLink.ltiContextId)
        }

    }

    @Throws(UnsupportedEncodingException::class)
    private fun startNewSession(request: HttpServletRequest): HttpServletRequest {
        request.session.invalidate()
        request.getSession(true)
        request.characterEncoding = "UTF-8"
        return request
    }

    private fun getDataConnector(): DataConnector {
        return JDBC("", dataSource.connection)
    }

    private fun getToolProvider(request: HttpServletRequest, response: HttpServletResponse, dc: DataConnector): ToolProvider {
        val tp = ToolProvider(request, response, this, dc)
        with(tp) {
            setParameterConstraint("oauth_consumer_key", true, 50)
            setParameterConstraint("resource_link_id", true, 50)
            setParameterConstraint("user_id", true, 50)
            setParameterConstraint("roles", true, null)
        }
        return tp
    }

}
