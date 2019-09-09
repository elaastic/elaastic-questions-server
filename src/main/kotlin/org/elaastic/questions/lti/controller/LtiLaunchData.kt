package org.elaastic.questions.lti.controller

import org.elaastic.questions.directory.Role
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.lti.LtiActivity
import org.elaastic.questions.lti.LtiUser
import java.net.URLEncoder
import java.nio.charset.Charset

class LtiLaunchData(
        val oauth_consumer_key: String,
        val user_id: String,

        val roles: String,
        val lis_person_name_given: String,
        val lis_person_name_family: String,

        val lis_person_contact_email_primary: String,
        val context_id: String,
        val context_title: String,
        val resource_link_id: String,
        val resource_link_title: String,
        val custom_assignmentid: String? = null,
        val lis_person_sourcedid: String? = null,
        val lis_person_name_full: String? = null,
        val ext_user_username: String? = null,
        val launch_presentation_locale: String? = null,
        val context_label: String? = null,
        val resource_link_description: String? = null,

        val context_type: String? = null,
        val lis_course_section_sourcedid: String? = null,
        val lis_result_sourcedid: String? = null,
        val lis_outcome_service_url: String? = null,
        val ext_lms: String? = null,
        val tool_consumer_info_product_family_code: String? = null,
        val tool_consumer_info_version: String? = null,
        val lti_version: String? = null,
        val lti_message_type: String? = null,
        val tool_consumer_instance_guid: String? = null,
        val tool_consumer_instance_name: String? = null,
        val tool_consumer_instance_description: String? = null,
        val launch_presentation_document_target: String? = null,
        val launch_presentation_return_url: String? = null

) {
    private var roleList: List<String>
    var roleService: RoleService? = null

    init {
        roleList = roles.split(",").mapNotNull {
            if (it.trim().isNotEmpty()) {
                var role = it
                if (!it.startsWith("urn:")) {
                    role = "urn:lti:role:ims/lis/$it"
                }
                role
            } else {
                null
            }
        }
    }

    fun toLtiUser(): LtiUser {
        return LtiUser(
                lmsKey = oauth_consumer_key,
                lmsUserId = user_id,
                firstName = lis_person_name_given,
                lastName = lis_person_name_family,
                email = lis_person_contact_email_primary,
                role = getRole()
        )
    }

    fun toLtiActivity(): LtiActivity {
        return LtiActivity(
                lmsKey = oauth_consumer_key,
                lmsActivityId = resource_link_id,
                lmsCourseId = context_id,
                lmsCourseTitle = context_title,
                title = resource_link_title,
                globalId = custom_assignmentid
        )
    }

    private fun hasRole(role: String): Boolean {
        val fullRole = if (!role.startsWith("urn:")) {
            "urn:lti:role:ims/lis/$role"
        } else role
        return roleList.contains(fullRole)
    }

    private fun isAdmin(): Boolean {
        return this.hasRole("Administrator") || this.hasRole("urn:lti:sysrole:ims/lis/SysAdmin") ||
                this.hasRole("urn:lti:sysrole:ims/lis/Administrator") || this.hasRole("urn:lti:instrole:ims/lis/Administrator")

    }

    private fun isStaff(): Boolean {
        return this.hasRole("Instructor") || this.hasRole("ContentDeveloper") || this.hasRole("TeachingAssistant")

    }

    private fun isLearner(): Boolean {
        return this.hasRole("Learner") || this.hasRole("Student")

    }

    private fun  getRole(): Role =  when {
        isAdmin() -> roleService!!.roleTeacher()
        isStaff() -> roleService!!.roleTeacher()
        isLearner() -> roleService!!.roleStudent()
        else -> roleService!!.roleStudent()
    }

    fun getRedirectUrlWithErrorMessage(errorMessage: String):String {
        var url = launch_presentation_return_url!!
        if (url.indexOf("?") >= 0) {
            url += '&'
        } else {
            url += '?'
        }
        url += "lti_errormsg=" + URLEncoder.encode(errorMessage, Charset.defaultCharset())
        return url
    }

}
