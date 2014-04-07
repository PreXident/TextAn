package cz.cuni.mff.ufal.textan.data.configs;

import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * A Spring configuration for a connection to a database.
 * Contains beans with a connection to the database, hibernate and Spring configuration,
 * data access objects.
 *
 * @author Petr Fanta
 */
@Configuration
@PropertySource("classpath:data.properties")
@EnableTransactionManagement
@ComponentScan(basePackages = {"cz.cuni.mff.ufal.textan.data.repositories.dao"})
public class DataConfig {

    @SuppressWarnings("unused")
    @Autowired
    private Environment env;

    /**
     * Creates JDBC connection to the database.
     *
     * @return Connection to the database
     * @see org.apache.commons.dbcp.BasicDataSource
     */
    @SuppressWarnings("WeakerAccess")
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
//        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//        driverManagerDataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
//        driverManagerDataSource.setUrl(env.getProperty("jdbc.url"));
//        driverManagerDataSource.setUsername(env.getProperty("jdbc.user"));
//        driverManagerDataSource.setPassword(env.getProperty("jdbc.pass"));


        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.pass"));

//        return driverManagerDataSource;
        return dataSource;
    }

    /**
     * Creates Hibernate's {@link org.hibernate.SessionFactory} with a connection to the database.
     *
     * @return SessionFactory to handle with transactions and access to database
     * @see DataConfig#dataSource()
     */
    @SuppressWarnings("WeakerAccess")
    @Bean
    public SessionFactory sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setHibernateProperties(hibernateProperties());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] mappings = null;

        try {
            mappings = resolver.getResources("classpath:mappings/*.hbm.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        sessionFactory.setMappingLocations(mappings);

        try {
            sessionFactory.afterPropertiesSet();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sessionFactory.getObject();
    }

    /**
     * Creates a postprocessor with an exception translation for Hibernate.
     *
     * @return the persistence exception translation post processor
     */
    @SuppressWarnings("unused")
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * Creates a Spring's transaction manager which cover and hides hibernate transactions.
     *
     * @return the {@link org.springframework.orm.hibernate4.HibernateTransactionManager}
     */
    @SuppressWarnings("unused")
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new HibernateTransactionManager(sessionFactory());
    }

    /**
     * Translates properties from the property file to properties for Hibernate
     *
     * @return the translated properties
     */
    private Properties hibernateProperties() {
        return new Properties() {
            {
//                setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
                setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
                setProperty("show_sql", env.getProperty("hibernate.show_sql"));
                setProperty("hibernate.globally_quoted_identifiers", "true");
            }
        };
    }

    /**
     * Creates a graph factory.
     *
     * @return the graph factory
     */
    @Bean
    public GraphFactory graphFactory() {
        return new GraphFactory(sessionFactory());
    }

}
