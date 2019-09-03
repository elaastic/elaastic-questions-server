package org.elaastic.questions.terms

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class TermsService(
        @Autowired val termsRepository: TermsRepository,
        @Autowired val termsContentRepository: TermsContentRepository
) {

    /**
     * Save the given terms
     * @param terms the given terms
     * @return the saved terms
     */
    fun save(terms: Terms): Terms {
        with(terms) {
            termsRepository.save(this)
            termsContentsByLanguage.forEach { (_, tc) ->
                termsContentRepository.save(tc)
            }
            updateInactiveTermsList(this)
        }.let {
            return it
        }
    }

    /**
     * get active terms
     */
    fun getActive(): Terms? {
        return termsRepository.findByIsActive(true)
    }

    /**
     * Update inactive terms list when a new active terms has been added
     * @param activeTerms the new active terms
     * @return the active terms
     */
    fun updateInactiveTermsList(activeTerms: Terms): Terms {
        termsRepository.findAllByIdIsNot(activeTerms.id!!).forEach {
            with(it) {
                endDate = activeTerms.startDate
                isActive = false
                termsRepository.save(this)
            }
        }
        return activeTerms
    }

    /**
     * Get terms content by language
     * @param language the expected language
     * @return the terms content in expected language or terms content in english if language is not supported
     */
    fun getTermsContentByLanguage(language: String): String {
        return getActive()!!.termsContentsByLanguage[language]?.content
                ?: getActive()!!.termsContentsByLanguage["en"]!!.content
    }
}
