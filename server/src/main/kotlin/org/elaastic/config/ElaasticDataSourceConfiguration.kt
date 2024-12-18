package org.elaastic.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["org.elaastic"], // TODO add an exclude filter
    entityManagerFactoryRef = "elaasticEntityManagerFactory",
    transactionManagerRef = "elaasticTransactionManager"
)
class ElaasticDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.elaastic")
    fun elaasticDataSourceProperties() = DataSourceProperties()

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.elaastic.hikari")
    fun elaasticDataSource() =
        elaasticDataSourceProperties()
            .initializeDataSourceBuilder()
            .build()

    @Bean
    fun elaasticJdbcTemplate(@Qualifier("elaasticDataSource") dataSource: DataSource) =
        JdbcTemplate(dataSource)

    @Bean
    fun elaasticEntityManagerFactory(
        @Qualifier("elaasticDataSource") dataSource: DataSource,
        builder: EntityManagerFactoryBuilder
    ) =
        builder.dataSource(dataSource)
            .packages("org.elaastic")
            .build()

    @Bean
    fun elaasticTransactionManager(
        @Qualifier("elaasticEntityManagerFactory") elaasticEntityManagerFactory: LocalContainerEntityManagerFactoryBean
    ) = JpaTransactionManager(
        elaasticEntityManagerFactory.`object` ?: throw IllegalArgumentException("Factory must not be null")
    )

    @Bean(initMethod = "migrate")
    @Value("\${spring.flyway.elaastic.locations}")
    fun elaasticFlyway(locations: String): Flyway {
        return Flyway.configure()
            .dataSource(elaasticDataSource())
            .locations(*locations.split(",").toTypedArray())
            .load()
    }

}
