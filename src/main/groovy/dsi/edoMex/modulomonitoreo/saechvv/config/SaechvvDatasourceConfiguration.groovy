package dsi.edoMex.modulomonitoreo.saechvv.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.sql.DataSource


/**
 * Contiene las funciones para definir el manejador de entidades de la base de SAECHVV
 * para el perfil de desarrollo local
 * @author lorenav
 * @version 12/11/2024
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "saechvvEntityManagerFactory",
        transactionManagerRef = "saechvvTransactionManager",
        basePackages = ["dsi.edoMex.modulomonitoreo.saechvv.repository"])
@EntityScan("dsi.edoMex.modulomonitoreo.saechvv.entity")
@Profile("local")
class SaechvvDatasourceConfiguration {

    /**
     * Definición de propiedades para la configuración del data source
     * @return Clase base de configuración de un data source
     */
    @Primary
    @Bean(name = "saechvvProperties")
    @ConfigurationProperties("spring.datasource.saechvv")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties()
    }

    /**
     * Inicializa el data source con la configuración de propiedades
     * @param properties Clase base de configuración de un data source
     * @return Clase alternativa para facilitar al DriverManager la conexión a la base
     */
    @Primary
    @Bean(name = "saechvvDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.saechvv")
    DataSource datasource(@Qualifier("saechvvProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build()
    }

    /**
     * Crea el EntityManager compatible al estándar de JPA
     * @param builder Constructor conveniente para instancias JPA EntityManagerFactory
     * @param dataSource Clase alternativa para facilitar al DriverManager la conexión a la base
     * @return Clase que crea EntityManagerFactory de acuerdo al estándar y configuración de JPA
     */
    @Primary
    @Bean(name = "saechvvEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder, @Qualifier("saechvvDatasource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("dsi.edoMex.modulomonitoreo.saechvv.entity")
                .persistenceUnit("saechvv").build()
    }

    /**
     * Creación del la interfaz central imperativo en la infraestructura de Spring
     * @param entityManagerFactory Interface usado para interactuar con el EntityManagerFactory para la persistencia unitaria
     * @return Interface central imperativa en la infraestructura de Spring
     */
    @Primary
    @Bean(name = "saechvvTransactionManager")
    @ConfigurationProperties("spring.jpa")
    PlatformTransactionManager transactionManager(@Qualifier("saechvvEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory)
    }
}