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

package org.elaastic.questions.lti.oauth

import net.oauth.OAuthAccessor
import net.oauth.OAuthConsumer
import net.oauth.OAuthProblemException
import net.oauth.SimpleOAuthValidator
import org.elaastic.questions.lti.LtiConsumerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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
        OAuthConsumer(DEFAULT_CALLBACK_URL, ltiConsumer.key, ltiConsumer.secret, null).let {
            OAuthAccessor(it).let { oAuthAccessor ->
                try {
                    SimpleOAuthValidator().validateMessage(HttpRequestOAuthMessage(request), oAuthAccessor)
                }  catch (pe : OAuthProblemException) {
                    logger.severe(pe.message)
                    pe.parameters.keys.forEach { key ->
                        logger.severe(pe.parameters[key].toString())
                    }
                    throw pe;
                } catch(e: OAuthException) {
                    logger.severe(e.message)
                    throw e
                }
            }
        }
    }



}
