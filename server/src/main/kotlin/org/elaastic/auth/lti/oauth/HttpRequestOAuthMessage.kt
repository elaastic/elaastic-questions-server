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

package org.elaastic.auth.lti.oauth

import net.oauth.OAuth
import net.oauth.OAuthMessage
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import javax.servlet.http.HttpServletRequest

class HttpRequestOAuthMessage(
        val request: HttpServletRequest
) : OAuthMessage(request.method, request.requestURL.toString(), getParameters(request)) {

    init {
        copyHeaders(request, headers)
    }

    @Throws(IOException::class)
    override fun getBodyAsStream(): InputStream {
        return request.inputStream
    }

    override fun getBodyEncoding(): String {
        return request.characterEncoding
    }


    companion object {

        fun copyHeaders(request: HttpServletRequest, into: MutableCollection<Map.Entry<String, String>>) {
            request.headerNames.iterator().forEach { name ->
                request.getHeaders(name).iterator().forEach { value ->
                    into.add(OAuth.Parameter(name, value))
                }
            }
        }

        fun getParameters(request: HttpServletRequest): List<OAuth.Parameter> {
            val list = ArrayList<OAuth.Parameter>()
            request.getHeaders("Authorization").iterator().forEach {
                for (parameter in OAuthMessage.decodeAuthorization(it)) {
                    if (!"realm".equals(parameter.key, ignoreCase = true)) {
                        list.add(parameter)
                    }
                }
            }
            for (entry in request.parameterMap.entries) {
                for (value in entry.value) {
                    list.add(OAuth.Parameter(entry.key, value))
                }
            }
            return list
        }
    }

}
