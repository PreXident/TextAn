package cz.cuni.mff.ufal.textan.data.configs;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import cz.cuni.mff.ufal.textan.data.logging.LogInterceptor;
import cz.cuni.mff.ufal.textan.data.views.INameTagView;
import cz.cuni.mff.ufal.textan.data.views.NameTagView;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A Spring configuration for a connection to a database.
 * Contains beans with a connection to the database, hibernate and Spring configuration,
 * data access objects.
 *
 * @author Petr Fanta
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"cz.cuni.mff.ufal.textan.data.repositories.dao"})
public class DataConfig {

    /** Path to a default server property file (inside jar). */
    private static final String DEFAULT_DATA_PROPERTIES = "data-default.properties";
    /** Path to an user server property file. The file should be relative to working directory. */
    private static final String USER_DATA_PROPERTIES = "data.properties";

    /**
     * Loads properties from property files.
     *
     * @return a combination of the default and a user properties, if a user define some, otherwise default properties
     * @throws IOException thrown when loading fails
     */
    @Bean
    public Properties dataProperties() throws IOException{

        //load default properties from jar
        Properties defaults = new Properties();
        defaults.load(getClass().getClassLoader().getResourceAsStream(DEFAULT_DATA_PROPERTIES));

        //load user properties
        Properties properties = new Properties(defaults);
        File userPropertiesFile = new File(USER_DATA_PROPERTIES);
        if (userPropertiesFile.exists()) {
            try (InputStream stream = new FileInputStream(userPropertiesFile)) {
                properties.load(stream);
            }
        }

        return properties;
    }

    /**
     * Creates JDBC connection to the database.
     *
     * @return Connection to the database
     * @see com.mchange.v2.c3p0.ComboPooledDataSource
     */
    @SuppressWarnings("WeakerAccess")
    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws PropertyVetoException, IOException {

        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(dataProperties().getProperty("jdbc.driverClassName"));
        dataSource.setJdbcUrl(dataProperties().getProperty("jdbc.url"));
        dataSource.setUser(dataProperties().getProperty("jdbc.user"));
        dataSource.setPassword(dataProperties().getProperty("jdbc.pass"));

        dataSource.setMaxPoolSize(Integer.parseInt(dataProperties().getProperty("c3p0.maxPoolSize")));
        dataSource.setInitialPoolSize(Integer.parseInt(dataProperties().getProperty("c3p0.initialPoolSize")));
        dataSource.setMinPoolSize(Integer.parseInt(dataProperties().getProperty("c3p0.minPoolSize")));
        dataSource.setAcquireIncrement(Integer.parseInt(dataProperties().getProperty("c3p0.acquireIncrement")));
        dataSource.setMaxIdleTime(Integer.parseInt(dataProperties().getProperty("c3p0.maxIdleTime")));
        dataSource.setCheckoutTimeout(Integer.parseInt(dataProperties().getProperty("c3p0.checkoutTimeout")));

        dataSource.setMaxStatements(Integer.parseInt(dataProperties().getProperty("c3p0.maxStatements")));
        dataSource.setMaxStatementsPerConnection(Integer.parseInt(dataProperties().getProperty("c3p0.maxStatementsPerConnection")));
        dataSource.setIdleConnectionTestPeriod(Integer.parseInt(dataProperties().getProperty("c3p0.idleConnectionTestPeriod")));

        return dataSource;
    }

    @Bean
    public LogInterceptor logInterceptor() {
        return new LogInterceptor("username");
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

        //sessionFactory.getConfiguration().setInterceptor(logInterceptor());

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
        HibernateTransactionManager result = new HibernateTransactionManager(sessionFactory());
        result.setEntityInterceptor(logInterceptor());
        return result;
    }

    /**
     * Translates properties from the property file to properties for Hibernate
     *
     * @return the translated properties
     */
    @SuppressWarnings("serial")
    private Properties hibernateProperties() throws IOException {
        return new Properties() {
            {
//                setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
                setProperty("hibernate.dialect", dataProperties().getProperty("hibernate.dialect"));
                setProperty("show_sql", dataProperties().getProperty("hibernate.show_sql"));
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
    
    @Bean
    public INameTagView nameTagView() throws PropertyVetoException, IOException {
        return new NameTagView(sessionFactory());
    }


    @Bean
    public LogInterceptorHack logInterceptorHack() {
        return new LogInterceptorHack();
    }

    public class LogInterceptorHack {

        @Autowired
        private LogInterceptor interceptor;

        @Autowired
        private SessionFactory sessionFactory;

        @PostConstruct
        public void inject() {
            interceptor.setSessionFactory(sessionFactory);
        }
    }
}
