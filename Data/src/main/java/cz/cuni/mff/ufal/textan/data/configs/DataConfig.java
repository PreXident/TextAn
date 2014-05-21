package cz.cuni.mff.ufal.textan.data.configs;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import cz.cuni.mff.ufal.textan.data.logging.LogInterceptor;
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
import java.beans.PropertyVetoException;
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
@PropertySource("classpath:data-default.properties")
@PropertySource(value = "file:./data.properties", ignoreResourceNotFound = true)
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
     * @see com.mchange.v2.c3p0.ComboPooledDataSource
     */
    @SuppressWarnings("WeakerAccess")
    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws PropertyVetoException {

        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(env.getProperty("jdbc.driverClassName"));
        dataSource.setJdbcUrl(env.getProperty("jdbc.url"));
        dataSource.setUser(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.pass"));

        dataSource.setMaxPoolSize(env.getProperty("c3p0.maxPoolSize", int.class));
        dataSource.setInitialPoolSize(env.getProperty("c3p0.initialPoolSize", int.class));
        dataSource.setMinPoolSize(env.getProperty("c3p0.minPoolSize", int.class));
        dataSource.setAcquireIncrement(env.getProperty("c3p0.acquireIncrement", int.class));
        dataSource.setMaxIdleTime(env.getProperty("c3p0.maxIdleTime", int.class));
        dataSource.setCheckoutTimeout(env.getProperty("c3p0.checkoutTimeout", int.class));

        dataSource.setMaxStatements(env.getProperty("c3p0.maxStatements", int.class));
        dataSource.setMaxStatementsPerConnection(env.getProperty("c3p0.maxStatementsPerConnection", int.class));
        dataSource.setIdleConnectionTestPeriod(env.getProperty("c3p0.idleConnectionTestPeriod", int.class));

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
    public SessionFactory sessionFactory() throws PropertyVetoException, IOException {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setHibernateProperties(hibernateProperties());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] mappings = null;

        mappings = resolver.getResources("classpath:mappings/*.hbm.xml");
        sessionFactory.setMappingLocations(mappings);
        sessionFactory.afterPropertiesSet();
        
        //sessionFactory.getConfiguration().setInterceptor(new LogInterceptor("MyUserName"));
        
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
    public PlatformTransactionManager transactionManager() throws PropertyVetoException, IOException {
        return new HibernateTransactionManager(sessionFactory());
    }

    /**
     * Translates properties from the property file to properties for Hibernate
     *
     * @return the translated properties
     */
    @SuppressWarnings("serial")
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
    public GraphFactory graphFactory() throws PropertyVetoException, IOException {
        return new GraphFactory(sessionFactory());
    }
}
