package org.elaastic.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
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
}