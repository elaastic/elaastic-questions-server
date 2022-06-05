package org.elaastic.questions.features

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.util.ResourceUtils
import org.togglz.core.context.StaticFeatureManagerProvider
import org.togglz.core.manager.FeatureManager
import org.togglz.core.manager.FeatureManagerBuilder
import org.togglz.core.repository.StateRepository
import org.togglz.core.repository.file.FileBasedStateRepository
import org.togglz.core.spi.FeatureProvider
import org.togglz.core.user.UserProvider
import org.togglz.kotlin.EnumClassFeatureProvider
import org.togglz.spring.boot.actuate.autoconfigure.TogglzProperties

@Configuration
class ElaasticTogglzConfiguration {

    @Autowired
    private val properties: TogglzProperties? = null

    @Bean
    fun featureProvider() = EnumClassFeatureProvider(ElaasticFeatures::class.java)

    /**
     * Note JT : I define this bean only for converting a String to a Feature
     * I couldn't find out another way to use feature into @PreAuthorize annotations
     */
    @Bean
    fun featureResolver() = FeatureResolver()

    @Bean
    fun userProvider(): UserProvider {
        return ElaasticTogglzUserProvider(
            properties?.console?.featureAdminAuthority ?: ""
        )
    }

    @Bean
    fun getStateRepository(): StateRepository =
        FileBasedStateRepository(
            ResourceUtils.getFile("classpath:togglz.features-file.properties")
        )

    @Bean
    @Primary
    fun featureManager(
        stateRepository: StateRepository,
        userProvider: UserProvider,
        featureProvider: FeatureProvider
    ): FeatureManager {
        val featureManager = FeatureManagerBuilder()
            .featureProvider(featureProvider)
            .stateRepository(stateRepository)
            .userProvider(userProvider)
            .build()

        StaticFeatureManagerProvider.setFeatureManager(featureManager)

        return featureManager
    }
}