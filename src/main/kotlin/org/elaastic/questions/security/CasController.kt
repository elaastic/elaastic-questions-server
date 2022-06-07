package org.elaastic.questions.security

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.servlet.http.HttpServletRequest

@Controller
class CasController {

    /**
     * URL of the form /cas/{casKey}/{remaining-path} are secured by the CAS server identified by {casKey}
     * When the user is properly authenticated, this action will perform a redirect to {remaining-path} (queryString if
     * any is preserved)
     * When there is no authentication, the security config will handle to redirect the user on the CAS server login page.
     */
    @GetMapping("/cas/{casKey}/**")
    fun casRedirect(request: HttpServletRequest, @PathVariable casKey: String) =
        "redirect:${request.requestURL.replace(Regex("/cas/$casKey"), "")}"  +
                (if (request.queryString != null) "?${request.queryString}" else "")

}