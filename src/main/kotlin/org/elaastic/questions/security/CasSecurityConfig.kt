package org.elaastic.questions.security

import org.jasig.cas.client.validation.Cas30ServiceTicketValidator
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.cas.web.CasAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


private const val SERVICE_URL_PREFIX = "login/cas"

private const val SERVICE_PROPERTIES_BEAN_PREFIX = "serviceProperties_"
private const val CAS_AUTHENTICATION_FILTER_BEAN_PREFIX = "casAuthenticationFilter_"
private const val TICKET_VALIDATOR_BEAN_PREFIX = "ticketValidator_"
private const val CAS_AUTHENTICATION_PROVIDER_BEAN_PREFIX = "casAuthenticationProvider_"
private const val CAS_AUTHENTICATION_ENTRY_POINT = "casAuthenticationEntryPoint_"

/**
 * This configuration class is responsible for creating all the spring-security artifacts required to interconnect
 * elaastic with all the configured CAS server (@see application.properties #CAS section)
 *
 * Authentication in elaastic works as follows :
 * - URL of the form /cas/{casKey}/... will use the CAS identified by {casKey} to authenticate the user
 * - all other secured URLs will be handled by the native form login (/login)
 * - the service URL (i.e. URL on which elaastic will authenticate a CAS user with its CAS ticket) for a specific
 * CAS server is /login/cas/{casKey}
 * - The CAS config for elaastic is defined in application.properties ; it comprises the list of cas identifiers (cas.keyList)
 * and the server.url for each CAS.
 *
 * When a CAS user is authenticated, the CasUser entity is looked up based on <casKey> and <principal.name>, and the
 * User bound the CasUser is loaded. User and CasUser are created for each first CAS authentication.
 *
 * @author John Tranier
 */
@Configuration
class CasSecurityConfig {

    companion object {
        private fun getServiceUrl(casKey: String) = "$SERVICE_URL_PREFIX/$casKey"
    }

    @Bean
    fun casSecurityConfigurer(
        context: ApplicationContext,
        environment: ConfigurableEnvironment
    ): CasSecurityConfigurer {
        return CasSecurityConfigurer(context, environment)
    }

    class CasSecurityConfigurer(
        val context: ApplicationContext,
        val environment: ConfigurableEnvironment
    ) : BeanDefinitionRegistryPostProcessor {

        private val elaasticServerUrl = readProperty("elaastic.questions.url", environment)
        val casInfoList = readCasConfiguration(environment)
        val casKeyToServerUrl = casInfoList.map { it.casKey }.associateWith { readCasProperty("server.url", it, environment) }

        /**
         * Each configured CAS server will require a CasAuthenticationProvider
         * This method returns the list of created CasAuthenticationProviders so that they can be used within WebSecurityConfig
         * @return the list of CasAuthenticationProviders created
         */
        fun getCasAuthenticationProviderBeanList(): List<CasAuthenticationProvider> =
            casInfoList.map { casInfo ->
                context.getBean(
                    "$CAS_AUTHENTICATION_PROVIDER_BEAN_PREFIX${casInfo.casKey}",
                    CasAuthenticationProvider::class.java
                )
            }

        /**
         * Each configured CAS server will require a CasAuthenticationEntryPoint that is responsible to commence
         * authentication for some URLs
         *
         * This method returns the mapping RequestMatcher => CasAuthenticationEntryPoint to define which URLs must
         * be authenticated by a specific CAS server
         *
         * The provided config here is that the CAS server identified by {casKey} will operate for URL of the form
         * /cas/{casKey}/...
         *
         */
        fun getCasAuthenticationEntryPoints(): Array<Pair<AntPathRequestMatcher, CasAuthenticationEntryPoint>> =
            casInfoList.map { casInfo ->
                AntPathRequestMatcher("/cas/${casInfo.casKey}/**") to
                        context.getBean(
                            "$CAS_AUTHENTICATION_ENTRY_POINT${casInfo.casKey}",
                            CasAuthenticationEntryPoint::class.java
                        )
            }.toTypedArray()

        override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {}

        /**
         * Modify the application context's internal bean definition registry after its standard initialization.
         * All regular bean definitions will have been loaded, but no beans will have been instantiated yet.
         * This allows for adding further bean definitions before the next post-processing phase kicks in.
         *
         * We use this method to define programmatically the beans we need to handle CAS servers
         */
        override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {

            // Iterate on the list of configured CAS in application.properties
            casInfoList.forEach { registerCasBeans(registry, it.casKey) }
        }

        /**
         * Register all the beans needed for a CAS server
         */
        private fun registerCasBeans(registry: BeanDefinitionRegistry, casKey: String) {

            registerCasAuthenticationFilter(registry, casKey)
            registerServiceProperties(registry, casKey)
            registerCasAuthenticationProvider(registry, casKey)
            registerTicketValidator(registry, casKey)
            registerCasAuthenticationEntryPoint(registry, casKey)
        }

        private fun registerCasAuthenticationFilter(registry: BeanDefinitionRegistry, casKey: String) {
            BeanDefinitionBuilder.genericBeanDefinition(CasAuthenticationFilter::class.java).setLazyInit(true)
                .let { builder ->
                    builder.addAutowiredProperty("authenticationManager")
                    builder.addPropertyReference("serviceProperties", "$SERVICE_PROPERTIES_BEAN_PREFIX${casKey}")
                    builder.addPropertyValue("filterProcessesUrl", "/${getServiceUrl(casKey)}")
                    registry.registerBeanDefinition(
                        "$CAS_AUTHENTICATION_FILTER_BEAN_PREFIX${casKey}",
                        builder.beanDefinition
                    )
                }
        }

        private fun registerServiceProperties(registry: BeanDefinitionRegistry, casKey: String) {
            BeanDefinitionBuilder.genericBeanDefinition(ServiceProperties::class.java)
                .setLazyInit(true)
                .let { builder ->
                    val serviceUrl = "$elaasticServerUrl${getServiceUrl(casKey)}"
                    builder.addPropertyValue("service", serviceUrl)
                    builder.addPropertyValue("sendRenew", false)
                    registry.registerBeanDefinition("$SERVICE_PROPERTIES_BEAN_PREFIX${casKey}", builder.beanDefinition)
                }
        }

        private fun registerCasAuthenticationProvider(registry: BeanDefinitionRegistry, casKey: String) {
            BeanDefinitionBuilder.genericBeanDefinition(ElaasticCasAuthenticationProvider::class.java).setLazyInit(true)
                .let { builder ->
                    builder.addConstructorArgValue(casKey)
                    builder.addConstructorArgReference("casUserDetailService")
                    builder.addConstructorArgReference("$SERVICE_PROPERTIES_BEAN_PREFIX${casKey}")
                    builder.addConstructorArgReference("$TICKET_VALIDATOR_BEAN_PREFIX${casKey}")
                    registry.registerBeanDefinition(
                        "$CAS_AUTHENTICATION_PROVIDER_BEAN_PREFIX${casKey}",
                        builder.beanDefinition
                    )
                }
        }

        private fun registerTicketValidator(registry: BeanDefinitionRegistry, casKey: String) {
            BeanDefinitionBuilder.genericBeanDefinition(Cas30ServiceTicketValidator::class.java).setLazyInit(true)
                .let { builder ->
                    builder.addConstructorArgValue(readCasProperty("server.url", casKey, environment))
                    registry.registerBeanDefinition("$TICKET_VALIDATOR_BEAN_PREFIX${casKey}", builder.beanDefinition)
                }
        }

        private fun registerCasAuthenticationEntryPoint(registry: BeanDefinitionRegistry, casKey: String) {
            BeanDefinitionBuilder.genericBeanDefinition(CasAuthenticationEntryPoint::class.java).setLazyInit(true)
                .let { builder ->
                    builder.addPropertyValue("loginUrl", "${readCasProperty("server.url", casKey, environment)}/login")
                    builder.addPropertyReference("serviceProperties", "$SERVICE_PROPERTIES_BEAN_PREFIX${casKey}")
                    registry.registerBeanDefinition("$CAS_AUTHENTICATION_ENTRY_POINT${casKey}", builder.beanDefinition)
                }
        }

        private fun readCasConfiguration(environment: ConfigurableEnvironment): List<CasInfo> =
            readOptionalProperty("cas.keyList", environment)?.split(',')?.map { casKey ->
                CasInfo(
                    casKey,
                    label = readCasProperty("label", casKey, environment),
                    logoSrc = readCasProperty("logo", casKey, environment),
                )
            } ?: listOf()

        /*
         * Iterates over all configuration sources, looking for the property value.
         * As Spring orders the property sources by relevance, the value of the first
         * encountered property with the correct name is read and returned.
         */
        private fun readOptionalProperty(propertyName: String, environment: ConfigurableEnvironment): String? =
            environment.propertySources.filterIsInstance<EnumerablePropertySource<Any>>()
                .find { source: EnumerablePropertySource<Any> ->
                    source.propertyNames.find { it.equals(propertyName) } != null
                }?.getProperty(propertyName) as String?

        private fun readProperty(propertyName: String, environment: ConfigurableEnvironment): String =
            readOptionalProperty(propertyName, environment)
                ?: throw IllegalStateException("Unable to determine value of property $propertyName")

        private fun readCasProperty(casPropertyName: String, casKey: String, environment: ConfigurableEnvironment) =
            readProperty("cas.${casKey}.$casPropertyName", environment)
    }
}