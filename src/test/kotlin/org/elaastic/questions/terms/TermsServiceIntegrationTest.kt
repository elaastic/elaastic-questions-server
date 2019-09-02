package org.elaastic.questions.terms

import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest
@Transactional
internal class TermsServiceIntegrationTest(
        @Autowired val termsService: TermsService,
        @Autowired val entityManager: EntityManager
) {

    internal val logger = Logger.getLogger(TermsServiceIntegrationTest::class.java.name)

    @Test
    fun testTermsAndTermsContentCreation() {
        tGiven {
            // valid terms and terms contents
            Terms().let {
                TermsContent("Terms in english", it, "en")
                TermsContent("Terms en français", it)
                it
            }.tWhen {
                // saving the terms
                termsService.save(it)
            }.tThen {
                // Terms object is saved as expected
                entityManager.refresh(it)
                assertThat(it.id, notNullValue())
                assertThat(it.version, equalTo(0L))
                assertThat(it.startDate, notNullValue())
                assertThat(it.endDate, nullValue())
                assertTrue(it.isActive)
                it
            }.tExpect {
                // fetching terms contents by language as expected
                it.termsContentsByLanguage["fr"].let { tc ->
                    assertThat(tc!!.id, notNullValue())
                    assertThat(tc.content, equalTo("Terms en français"))
                }
                it.termsContentsByLanguage["en"].let { tc ->
                    assertThat(tc!!.id, notNullValue())
                    assertThat(tc.content, equalTo("Terms in english"))
                }
            }
        }
    }

    @Test
    fun testFindByActiveTerms() {
        tGiven {
            // a non active terms
            listOf<Terms>(
                    Terms(startDate = Date(), isActive = false).let {
                        TermsContent("Terms in english", it, "en")
                        TermsContent("Terms en français", it)
                        it
                    },
                    // and an active terms
                    Terms().let {
                        TermsContent("Active Terms in english", it, "en")
                        TermsContent("Terms en français", it)
                        it
                    }
            ).forEach {
                termsService.save(it)
            }.tExpect {
                termsService.getActive().let {
                    entityManager.refresh(it)
                    assertTrue(it.isActive)
                    logger.info(it.termsContentsByLanguage["en"]?.content)
                    assertTrue(it.termsContentsByLanguage["en"]?.content?.startsWith("Active") ?: false)
                }
            }

        }
    }

    @Test
    fun testUpdateOfInactiveTermsWhenANewOneIsComing() {
        tGiven {
            listOf(
                    // a terms
                    Terms().let {
                        TermsContent("Terms in english", it, "en")
                        TermsContent("Terms en français", it)
                        termsService.save(it)
                    },
                    // and a new one
                    Terms().let {
                        TermsContent("New Terms in english", it, "en")
                        TermsContent("Nouveaux Terms en français", it)
                        termsService.save(it)
                    }.tWhen {
                        // updating now inactive terms
                        termsService.updateInactiveTermsList(it)
                        it
                    })
        }.tThen {
            assertThat(it[0].endDate, equalTo(it[1].startDate))
            assertFalse(it[0].isActive)
            assertThat(it[1].endDate, nullValue())
            assertTrue(it[1].isActive)
        }
    }

}
