package config;

// import model.Lead;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import model.Exam;
import model.Grade;
import model.Lead;

public class DatabaseConfig {

    private SessionFactory factory;

    public void start() {

        Configuration config = new Configuration();

        config.configure("hibernate.cfg.xml");

        config.addAnnotatedClass(Lead.class);
        config.addAnnotatedClass(Exam.class);
        config.addAnnotatedClass(Grade.class);
        // config.addAnnotatedClass(Lead.class);

        factory = config.buildSessionFactory();
    }

    public SessionFactory getSessionFactory() {
        return factory;
    }

    public void stop() {
        if (factory != null) {
            factory.close();
        }
    }
}