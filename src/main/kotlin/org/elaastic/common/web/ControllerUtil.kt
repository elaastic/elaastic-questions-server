package org.elaastic.common.web

import javax.servlet.http.HttpServletRequest

object ControllerUtil {

    fun getServerBaseUrl(request: HttpServletRequest) =
        "${request.scheme}://${request.serverName}:${request.serverPort}"
}