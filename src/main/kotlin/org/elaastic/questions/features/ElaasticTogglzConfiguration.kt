package org.elaastic.questions.features

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.ResourceUtils
import org.togglz.core.repository.StateRepository
import org.togglz.core.repository.file.FileBasedStateRepository
import org.togglz.core.user.UserProvider
import org.togglz.spring.boot.actuate.autoconfigure.TogglzProperties

@Configuration
class ElaasticTogglzConfiguration {

    @Autowired
    private val properties: TogglzProperties? = null

    @Bean
    fun userProvider(): UserProvider {
        return ElaasticTogglzUserProvider(
            properties?.getConsole()?.featureAdminAuthority ?: ""
        )
    }

    @Bean
    fun getStateRepository(): StateRepository =
        FileBasedStateRepository(
            ResourceUtils.getFile("classpath:togglz.features-file.properties")
        )

}