package org.elaastic.questions.controller

import org.elaastic.questions.directory.User
import javax.servlet.http.HttpServletRequest

object ControllerUtil {

    fun getServerBaseUrl(request: HttpServletRequest) =
        "${request.scheme}://${request.serverName}:${request.serverPort}"
}