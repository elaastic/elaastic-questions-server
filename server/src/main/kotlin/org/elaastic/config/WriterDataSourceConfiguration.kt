package org.elaastic.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = [
        "org.elaastic.material",
    ],
    entityManagerFactoryRef = "writerEntityManagerFactory",
    transactionManagerRef = "writerTransactionManager"
)
class WriterDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.writer")
    fun writerDataSourceProperties() = DataSourceProperties()

    @Bean
    fun writerDataSource() =
        writerDataSourceProperties()
            .initializeDataSourceBuilder()
            .build()

    @Bean
    fun writerJdbcTemplate(@Qualifier("writerDataSource") dataSource: DataSource) =
        JdbcTemplate(dataSource)

    @Bean
    fun writerEntityManagerFactory(
        @Qualifier("writerDataSource") dataSource: DataSource,
        jpaProperties: JpaProperties,
        builder: EntityManagerFactoryBuilder
    ): LocalContainerEntityManagerFactoryBean {
        return builder.dataSource(dataSource)
            .packages("org.elaastic.material")
            .persistenceUnit("writer")
            .properties(jpaProperties.properties)
            .build()
    }

    @Bean
    fun writerTransactionManager(
        @Qualifier("writerEntityManagerFactory") writerEntityManagerFactory: LocalContainerEntityManagerFactoryBean
    ) = JpaTransactionManager(
        writerEntityManagerFactory.`object` ?: throw IllegalArgumentException("Factory must not be null")
    )
}