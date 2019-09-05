package org.elaastic.questions.lti.oauth

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
