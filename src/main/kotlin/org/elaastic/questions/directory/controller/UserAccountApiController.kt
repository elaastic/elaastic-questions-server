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

package org.elaastic.questions.directory.controller

import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.directory.controller.command.UserData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.logging.Logger
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
class UserAccountApiController(

        @Autowired val roleService: RoleService,
        @Autowired val userService: UserService,

        @Value("\${elaastic.auth.check_user_email:true}")
        val checkEmail: Boolean,

        @Autowired val messageSource: MessageSource
) {

    val logger = Logger.getLogger(UserAccountApiController::class.java.name)

    @PostMapping("/api/users")
    fun ajaxDoSubscribe(
            @Valid userData: UserData,
            result: BindingResult,
            locale: Locale
    ): SubscriptionResponse {
        if (!result.hasErrors()) {
            val user = userData.populateNewUser(roleService)
            try {
                userService.addUser(
                        user = user,
                        language = userData.language,
                        checkEmailAccount = checkEmail
                )
            } catch (e: DataIntegrityViolationException) {
                userData.catchDataIntegrityViolationException(e, result)
            }
        }
        return if (result.hasErrors()) {
            result.allErrors.map {
                it.codes?.let { codes ->
                    messageSource.getMessage(codes[0], emptyArray(), locale)
                }
            }.let {
                SubscriptionResponse(
                        success = false,
                        errorList = it.map { it ?: "" }
                )
            }
        } else {
            SubscriptionResponse()
        }
    }


    data class SubscriptionResponse(
            val success: Boolean = true,
            val errorList: List<String> = listOf()
    )
}
