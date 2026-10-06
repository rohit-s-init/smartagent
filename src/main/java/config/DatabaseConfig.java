package config;

// import model.Lead;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import model.Chat;
import model.Exam;
import model.Grade;
import model.Lead;

public class DatabaseConfig {

    private static SessionFactory factory;

    public static SessionFactory getSessionFactory() {
        if (factory == null || factory.isClosed()) {
            Configuration config = new Configuration();

            config.configure("hibernate.cfg.xml");

            config.addAnnotatedClass(Lead.class);
            config.addAnnotatedClass(Exam.class);
            config.addAnnotatedClass(Grade.class);
            config.addAnnotatedClass(Chat.class);
            // config.addAnnotatedClass(Lead.class);

            factory = config.buildSessionFactory();
        }
        return factory;
    }

    public static void stop() {
        if (factory != null) {
            factory.close();
        }
    }
}