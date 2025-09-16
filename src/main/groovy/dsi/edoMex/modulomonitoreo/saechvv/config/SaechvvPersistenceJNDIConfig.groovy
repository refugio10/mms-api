package dsi.edoMex.modulomonitoreo.saechvv.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource

/**
 * Contiene las funciones para definir el manejador de entidades de la base de SAECHVV
 * con perfil productivo y principal
 * @author lorenav
 * @version 06/02/2024
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "saechvvEntityManagerFactory",
        transactionManagerRef = "saechvvTransactionManager",
        basePackages = ["dsi.edoMex.modulomonitoreo.saechvv.repository"])
@EntityScan("dsi.edoMex.modulomonitoreo.saechvv.entity")
@Profile("prod")
class SaechvvPersistenceJNDIConfig {

    /**
     * Inicializa el data source con las propiedades contenidas en el jdbc del tomcat
     * @param properties Clase base de configuración de un data source
     * @return Clase alternativa para facilitar al DriverManager la conexión a la base
     */
    @Primary
    @Bean(name = "saechvvDatasource")
    DataSource dataSource(@Value('${spring.datasource.saechvv.jndiName}') String jndiName) throws Exception {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup()
        return dataSourceLookup.getDataSource("java:comp/env/${jndiName}")
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
