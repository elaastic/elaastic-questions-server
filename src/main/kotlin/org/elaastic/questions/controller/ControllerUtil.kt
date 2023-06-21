package org.elaastic.questions.controller

import javax.servlet.http.HttpServletRequest

object ControllerUtil {

    fun getServerBaseUrl(request: HttpServletRequest) =
        "${request.scheme}://${request.serverName}:${request.serverPort}"
}