package config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatabaseConfigTest {

    @Test
    public void testConnection() {

        SessionFactory factory = DatabaseConfig.getSessionFactory();

        try {

            Assertions.assertNotNull(factory);
            Assertions.assertFalse(factory.isClosed());

            try (Session session = factory.openSession()) {

                Integer result = session
                        .createNativeQuery("SELECT 1", Integer.class)
                        .getSingleResult();

                Assertions.assertEquals(1, result);
            }

        } finally {

            DatabaseConfig.stop();
        }

        Assertions.assertTrue(factory.isClosed());
    }
}