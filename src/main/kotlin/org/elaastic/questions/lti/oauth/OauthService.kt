package org.elaastic.questions.lti.oauth

import net.oauth.OAuthAccessor
import net.oauth.OAuthConsumer
import net.oauth.SimpleOAuthValidator
import org.elaastic.questions.lti.LtiConsumerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.tsaap.lti.tp.net.oauth.server.OAuthServlet
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

@Service
class OauthService(
        @Autowired val ltiConsumerRepository: LtiConsumerRepository
) {

    private val logger = Logger.getLogger(OauthService::class.java.name)

    private val NO_CONSUMER_KEY: String = "No consumer key provided in request paramameters"
    private val NO_CONSUMER: String = "No consumer corresponding to the provided key provided in request paramameters"
    private val VALIDATION_FAILS: String = "Validation fails"

    private val PARAM_OAUTH_CONSUMER_KEY = "oauth_consumer_key"
    private val DEFAULT_CALLBACK_URL = "about:blank"


    /**
     * Validate oauth http request
     * @param request the http request
     */
    fun validateOauthRequest(request: HttpServletRequest) {
        val key = request.getParameter(PARAM_OAUTH_CONSUMER_KEY) ?: throw IllegalArgumentException(NO_CONSUMER_KEY)
        val ltiConsumer = ltiConsumerRepository.findByKey(key) ?: throw IllegalArgumentException(NO_CONSUMER)
        val oAuthConsumer = OAuthConsumer(DEFAULT_CALLBACK_URL, ltiConsumer.key, ltiConsumer.secret, null)
        val oAuthAccessor = OAuthAccessor(oAuthConsumer)
        val oAuthValidator = SimpleOAuthValidator()
        val oAuthMessage = OAuthServlet.getMessage(request, null)
        try {
            oAuthValidator.validateMessage(oAuthMessage, oAuthAccessor)
        } catch(e: Exception) {
            logger.severe(e.message)
            throw OAuthProblemException(VALIDATION_FAILS)
        }

    }

}
