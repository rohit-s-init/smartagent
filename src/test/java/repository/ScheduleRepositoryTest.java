package repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import config.DatabaseConfig;
import dto.ScheduleDAO;
import model.Lead;
import model.Schedule;
import dto.LeadDTO;


public class ScheduleRepositoryTest {

    private ScheduleRepository scheduleRepository;
    private SessionFactory sessionFactory;
    private Long leadId;

    @BeforeEach
    void setUp() {
        // testing the get method for new phone number
        LeadRepository leadRepository = new LeadRepository();
        ScheduleRepository scheduleRepository = new ScheduleRepository();
        this.scheduleRepository = scheduleRepository;
        this.sessionFactory = DatabaseConfig.getSessionFactory();
        
        LeadDTO lead = leadRepository.getLeadByPhoneNumber("8459781390");

        leadId = lead.getId();

        assertNotEquals(0, lead.getId());
    }

    @Test
    void testCreateScheduleWhenLeadExists() throws Exception {
        LocalTime time = LocalTime.of(10, 30);

        scheduleRepository.makeSchedule(leadId, time);

        ScheduleDAO result = scheduleRepository.getScheduleByLeadId(leadId);

        assertNotNull(result);
        assertEquals(time, result.getScheduledTime());
    }

    @Test
    void testUpdateScheduleWhenAlreadyExists() throws Exception {
        LocalTime oldTime = LocalTime.of(10, 30);
        LocalTime newTime = LocalTime.of(14, 45);

        scheduleRepository.makeSchedule(leadId, oldTime);
        scheduleRepository.makeSchedule(leadId, newTime);

        ScheduleDAO result = scheduleRepository.getScheduleByLeadId(leadId);

        assertNotNull(result);
        assertEquals(newTime, result.getScheduledTime());
    }

    @Test
    void testScheduleNotPresentInitially() {
        ScheduleDAO result = scheduleRepository.getScheduleByLeadId(leadId);

        assertNull(result);
    }

    @Test
    void testCannotScheduleForNonExistingLead() {
        Long invalidLeadId = -1L;
        LocalTime time = LocalTime.of(12, 0);

        Exception exception = assertThrows(Exception.class, () -> {
            scheduleRepository.makeSchedule(invalidLeadId, time);
        });

        assertTrue(exception.getMessage().contains("Cannot create schedule"));
        assertNull(scheduleRepository.getScheduleByLeadId(invalidLeadId));
    }

    @Test
    void testSameScheduleIdAfterUpdate() throws Exception {
        scheduleRepository.makeSchedule(leadId, LocalTime.of(9, 0));

        ScheduleDAO first = scheduleRepository.getScheduleByLeadId(leadId);

        scheduleRepository.makeSchedule(leadId, LocalTime.of(16, 0));

        ScheduleDAO second = scheduleRepository.getScheduleByLeadId(leadId);

        assertEquals(first.getId(), second.getId());
    }

    @AfterEach
    void cleanUp() {
        if (leadId == null) {
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            // reoving testign user from the database
            LeadRepository leadRepository = new LeadRepository();

            leadRepository.deleteByPhoneNumber("8459781390");
        }
    }
}