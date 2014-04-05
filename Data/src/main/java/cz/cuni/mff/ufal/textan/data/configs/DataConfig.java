package cz.cuni.mff.ufal.textan.data.configs;

import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import cz.cuni.mff.ufal.textan.data.repositories.Data;
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
 * Created by Petr Fanta on 14.3.14.
 */

@Configuration
@PropertySource("classpath:data.properties")
@EnableTransactionManagement
//TODO: it is posible to to create bean manualy, but comment this line and @Autowired in AbstractHibernateDAO
@ComponentScan(basePackages = {"cz.cuni.mff.ufal.textan.data.repositories.dao"})
public class DataConfig {

    @Autowired
    private Environment env;

    /**
     *
     * @return Connection to the database
     */
    @Bean (destroyMethod = "close")
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
     *
     * @return SessionFactory to handle with transactions and access to database
     */
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

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        //HibernateTransactionManager
        return new HibernateTransactionManager(sessionFactory());
    }

    Properties hibernateProperties() {
        return new Properties() {
            {
//                setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
                setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
                setProperty("show_sql", env.getProperty("hibernate.show_sql"));
                setProperty("hibernate.globally_quoted_identifiers", "true");
            }
        };
    }

    //TODO: Add transaction management
    // TODO: to uz mame ne?
        
    @Bean
    public Data data() {
        return new Data(sessionFactory());
    }

    @Bean
    public GraphFactory graphFactory() {
        return new GraphFactory(sessionFactory());
    }
    
//    @Bean
//    public ObjectTableDAO objectTableDAO() {
//        ObjectTableDAO objectTableDAO = new ObjectTableDAO();
//        objectTableDAO.setSessionFactory(sessionFactory());
//        return objectTableDAO;
//    }
    
}
