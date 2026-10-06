package repository;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import config.DatabaseConfig;
import model.Lead;

public class LeadRepositoryTest {

    private static SessionFactory sessionFactory;
    private static LeadRepository leadRepository;

    @BeforeAll
    public static void setup() {

        sessionFactory = DatabaseConfig.getSessionFactory();

        leadRepository = new LeadRepository();
    }

    @AfterAll
    public static void cleanup() {

        sessionFactory.close();
    }

    @Test
    public void testSaveLead() {

        Lead lead = new Lead();

        lead.setName("Test Student");
        lead.setPhoneNumber("9999999991");
        lead.setLocation("Pune");
        lead.setSchooling("12th Science");

        leadRepository.save(lead);

        assertNotNull(lead.getId());

        Lead savedLead = leadRepository.getLeadById(lead.getId());

        assertNotNull(savedLead);
        assertEquals("Test Student", savedLead.getName());
        assertEquals("Pune", savedLead.getLocation());
        assertEquals("12th Science", savedLead.getSchooling());
    }

    @Test
    public void testGetLeadById() {

        Lead lead = new Lead();

        lead.setName("Rahul");
        lead.setPhoneNumber("9999999992");
        lead.setLocation("Mumbai");
        lead.setSchooling("12th Commerce");

        leadRepository.save(lead);

        Lead result = leadRepository.getLeadById(lead.getId());

        assertNotNull(result);

        assertEquals(lead.getId(), result.getId());
        assertEquals("Rahul", result.getName());
        assertEquals("Mumbai", result.getLocation());
    }

    @Test
    public void testUpdateLead() {

        Lead lead = new Lead();

        lead.setName("Amit");
        lead.setPhoneNumber("9999999993");
        lead.setLocation("Pune");
        lead.setSchooling("12th Science");

        leadRepository.save(lead);

        lead.setName("Amit Patil");
        lead.setLocation("Nashik");
        lead.setSchooling("Diploma");

        leadRepository.update(lead);

        Lead updatedLead =
                leadRepository.getLeadById(lead.getId());

        assertNotNull(updatedLead);

        assertEquals("Amit Patil", updatedLead.getName());
        assertEquals("Nashik", updatedLead.getLocation());
        assertEquals("Diploma", updatedLead.getSchooling());
    }

    @Test
    public void testLeadDoesNotExist() {

        Lead lead = leadRepository.getLeadById(999999L);

        assertNull(lead);
    }
}