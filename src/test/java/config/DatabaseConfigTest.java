package config;

import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class DatabaseConfigTest {

    @Test
    public void testConnection() {

        DatabaseConfig db = new DatabaseConfig();

        try {
            db.start();

            Assertions.assertNotNull(db.getSessionFactory());

            try (Session session = db.getSessionFactory().openSession()) {

                Integer result = session
                    .createNativeQuery("SELECT 1", Integer.class)
                    .getSingleResult();

                Assertions.assertEquals(1, result);
            }

        } finally {
            db.stop();
        }

        Assertions.assertTrue(db.getSessionFactory().isClosed());
    }
}