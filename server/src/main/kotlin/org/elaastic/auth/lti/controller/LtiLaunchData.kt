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

package org.elaastic.auth.lti.controller

import org.elaastic.user.Role
import org.elaastic.user.RoleService
import org.elaastic.auth.lti.LtiActivity
import org.elaastic.auth.lti.LtiUser
import java.net.URLEncoder
import java.nio.charset.Charset

class LtiLaunchData(
        val oauth_consumer_key: String,
        val user_id: String,

        val roles: String,
        val lis_person_name_given: String? = null,
        val lis_person_name_family: String? = null,

        val lis_person_contact_email_primary: String? = null,
        val context_id: String,
        val context_title: String,
        val resource_link_id: String,
        val resource_link_title: String = "Elaastic questions",
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
                firstName = lis_person_name_given ?: "User",
                lastName = lis_person_name_family ?: user_id,
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
        url += "lti_errormsg=" + URLEncoder.encode(errorMessage, Charset.defaultCharset().toString())
        return url
    }

}
