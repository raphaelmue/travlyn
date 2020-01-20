package org.travlyn.server.db;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.travlyn.server.db.model.*;

import java.util.Properties;

public class HibernateUtil {

    private static final String DB_NAME_PLACEHOLDER = "DB_NAME_PLACEHOLDER";
    private static final String DB_HOST_PLACEHOLDER = "DB_HOST_PLACEHOLDER";
    private static final String DB_ENGINE_PLACEHOLDER = "DB_ENGINE_PLACEHOLDER";

    private static SessionFactory sessionFactory;
    private static Properties databaseProperties;

    private HibernateUtil() {

    }

    public static void setDatabaseProperties(Properties databaseProperties) {
        HibernateUtil.databaseProperties = databaseProperties;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure(HibernateUtil.class.getResource("hibernate.cfg.xml"))
                    .addAnnotatedClass(CategoryEntity.class)
                    .addAnnotatedClass(CityEntity.class)
                    .addAnnotatedClass(GeoTextEntity.class)
                    .addAnnotatedClass(RatingEntity.class)
                    .addAnnotatedClass(StopEntity.class)
                    .addAnnotatedClass(TokenEntity.class)
                    .addAnnotatedClass(TripEntity.class)
                    .addAnnotatedClass(UserEntity.class);

            // replace attributes with given properties
            String url = configuration.getProperty("hibernate.connection.url");
            configuration.setProperty("hibernate.connection.url", url
                    .replace(DB_ENGINE_PLACEHOLDER, databaseProperties.getProperty("travlyn.database.engine"))
                    .replace(DB_HOST_PLACEHOLDER, databaseProperties.getProperty("travlyn.database.host"))
                    .replace(DB_NAME_PLACEHOLDER, databaseProperties.getProperty("travlyn.database.name")));
            configuration.setProperty("hibernate.connection.username", databaseProperties.getProperty("travlyn.database.user"));
            configuration.setProperty("hibernate.connection.password", databaseProperties.getProperty("travlyn.database.password"));

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Returns an initialized SessionFactory.
     *
     * @return SessionFactory object
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }
}
