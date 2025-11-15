package org.elaastic.common.onboarding

import org.elaastic.user.User
import org.elaastic.user.UserService
import org.springframework.context.MessageSource
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@PreAuthorize("@featureManager.isActive(@featureResolver.getFeature('ONBOARDING'))")
@RestController
class OnboardingController(
    private val messageSource: MessageSource,
    private val userService: UserService
) {

    @GetMapping("/api/properties/onboarding")
    fun getI18nMessages(locale: Locale): Map<String, String> {
        return OnboardingChapter.values()
            .map { chapter -> chapter.i18nMessages }
            .flatten()
            .associateWith { i18nKey -> messageSource.getMessage(i18nKey, null, locale) }
    }

    @GetMapping("/api/properties/onboarding/chapter/{chapter}")
    fun getI18nMessages(locale: Locale, @PathVariable chapter: String): Map<String, String> {
        return OnboardingChapter.valueOf(chapter).i18nMessages.associateWith { i18nKey ->
            messageSource.getMessage(
                i18nKey,
                null,
                locale
            )
        }
    }

    @GetMapping("/userAccount/updateOnboardingChapter/{chapterToUpdate}")
    fun updateOnboardingChapter(authentication: Authentication, @PathVariable chapterToUpdate: String) {
        val user: User = authentication.principal as User
        userService.updateOnboardingChapter(OnboardingChapter.from(chapterToUpdate), user)
    }

    @GetMapping("/userAccount/getOnboardingChapter")
    fun getOnboardingChapter(authentication: Authentication): String? {
        val user: User = authentication.principal as User
        return userService.getOnboardingState(user.id).toString()
    }

}