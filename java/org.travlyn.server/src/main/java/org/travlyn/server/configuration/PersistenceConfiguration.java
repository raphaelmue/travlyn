package org.travlyn.server.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceConfiguration.class);

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("org.travlyn.server.service", "org.travlyn.shared.model.db");
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/hibernate.properties"));
            sessionFactory.setHibernateProperties(properties);
        } catch (IOException e) {
            logger.error("Failed to load hibernate properties for instantiating data source.", e);
        }
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/application.properties"));
            dataSource.setDriverClassName(properties.getProperty("travlyn.database.driver"));
            dataSource.setUrl("jdbc:" +
                    properties.getProperty("travlyn.database.engine") + "://" +
                    properties.getProperty("travlyn.database.host") + ":3306/" +
                    properties.getProperty("travlyn.database.name"));
            dataSource.setUsername(properties.getProperty("travlyn.database.user"));
            dataSource.setPassword(properties.getProperty("travlyn.database.password"));
        } catch (IOException e) {
            logger.error("Failed to load database properties for instantiating data source.", e);
        }

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setDataSource(dataSource());
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }
}
