package org.elaastic.questions.onboarding

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/properties")
class PropertiesController(
    @Autowired
    val messageSource: MessageSource
) {

    @GetMapping("onboarding")
    fun getI18nMessages(locale: Locale): Map<String, String> {
        return OnboardingChapter.values()
            .map { chapter -> chapter.i18nMessages }
            .flatten()
            .associateWith { i18nKey -> messageSource.getMessage(i18nKey, null, locale) }
    }

    @GetMapping("onboarding/chapter/{chapter}")
    fun getI18nMessages(locale: Locale, @PathVariable chapter: String): Map<String, String> {
        return OnboardingChapter.valueOf(chapter).i18nMessages.associateWith { i18nKey -> messageSource.getMessage(i18nKey, null, locale) }
    }
}