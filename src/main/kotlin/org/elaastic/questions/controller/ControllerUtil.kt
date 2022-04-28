package org.elaastic.questions.controller

import org.elaastic.questions.directory.User
import javax.servlet.http.HttpServletRequest

object ControllerUtil {

    /**
     * Construct the server URL from the http request and taking into account if the current session has been
     * authenticated on a CAS server.
     * For session authenticated on a CAS server, it adds "/cas/<casKey>" so that the provided URL will be associated
     * to the same CAS server
     */
    fun getServerBaseUrl(httpServletRequest: HttpServletRequest, user: User) =
        "${httpServletRequest.scheme}://${httpServletRequest.serverName}:${httpServletRequest.serverPort}" +
                if(user.casKey != null) "/cas/${user.casKey}" else ""
}