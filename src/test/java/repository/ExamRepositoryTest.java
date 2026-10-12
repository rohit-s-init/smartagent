package repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dto.ExamDTO;
import dto.LeadDTO;


public class ExamRepositoryTest {

    private static final String TEST_PHONE_NUMBER = "8459781392";
    private static final String OTHER_TEST_PHONE_NUMBER = "8459781393";

    private ExamRepository examRepository;
    private LeadRepository leadRepository;
    private Long leadId;

    @BeforeEach
    void setUp() {
        // creating (or fetching) the testing lead
        leadRepository = new LeadRepository();
        examRepository = new ExamRepository();

        LeadDTO lead = leadRepository.getLeadByPhoneNumber(TEST_PHONE_NUMBER);

        leadId = lead.getId();

        assertNotNull(leadId);
    }

    // ---------- creating exams ----------

    @Test
    void testCreateExamForLead() throws Exception {
        examRepository.createExam(leadId, "JEE Main", 92.5, "percentile");

        List<ExamDTO> exams = examRepository.getExamsByLeadId(leadId);

        assertEquals(1, exams.size());

        ExamDTO exam = exams.get(0);
        assertNotNull(exam.getId());
        assertEquals("JEE Main", exam.getExamName());
        assertEquals(92.5, exam.getScore());
        assertEquals("percentile", exam.getScoreType());
    }

    @Test
    void testCreateMultipleExamsForLead() throws Exception {
        examRepository.createExam(leadId, "JEE Main", 92.5, "percentile");
        examRepository.createExam(leadId, "MHT-CET", 98.0, "percentile");
        examRepository.createExam(leadId, "HSC", 85.0, "percentage");

        List<ExamDTO> exams = examRepository.getExamsByLeadId(leadId);

        assertEquals(3, exams.size());

        List<String> names = exams.stream().map(ExamDTO::getExamName).toList();
        assertTrue(names.contains("JEE Main"));
        assertTrue(names.contains("MHT-CET"));
        assertTrue(names.contains("HSC"));
    }

    @Test
    void testEachExamHasOwnId() throws Exception {
        examRepository.createExam(leadId, "JEE Main", 92.5, "percentile");
        examRepository.createExam(leadId, "MHT-CET", 98.0, "percentile");

        List<ExamDTO> exams = examRepository.getExamsByLeadId(leadId);

        assertEquals(2, exams.size());
        assertNotNull(exams.get(0).getId());
        assertNotNull(exams.get(1).getId());
        assertNotEquals(exams.get(0).getId(), exams.get(1).getId());
    }

    @Test
    void testExamsAreSeparatedBetweenLeads() throws Exception {
        Long otherLeadId = leadRepository.getLeadByPhoneNumber(OTHER_TEST_PHONE_NUMBER).getId();

        examRepository.createExam(leadId, "JEE Main", 92.5, "percentile");
        examRepository.createExam(leadId, "MHT-CET", 98.0, "percentile");
        examRepository.createExam(otherLeadId, "HSC", 85.0, "percentage");

        List<ExamDTO> first = examRepository.getExamsByLeadId(leadId);
        List<ExamDTO> other = examRepository.getExamsByLeadId(otherLeadId);

        assertEquals(2, first.size());
        assertEquals(1, other.size());
        assertEquals("HSC", other.get(0).getExamName());
    }

    // ---------- lead has no exams ----------

    @Test
    void testNoExamsReturnsEmptyList() {
        List<ExamDTO> exams = examRepository.getExamsByLeadId(leadId);

        assertNotNull(exams);
        assertTrue(exams.isEmpty());
    }

    // ---------- lead does not exist ----------

    @Test
    void testCannotCreateExamForNonExistingLead() {
        Long invalidLeadId = -1L;

        Exception exception = assertThrows(Exception.class, () -> {
            examRepository.createExam(invalidLeadId, "JEE Main", 92.5, "percentile");
        });

        assertTrue(exception.getMessage().contains("Cannot create exam"));
        assertTrue(examRepository.getExamsByLeadId(invalidLeadId).isEmpty());
    }

    @Test
    void testGetExamsForNonExistingLeadReturnsEmptyList() {
        List<ExamDTO> exams = examRepository.getExamsByLeadId(-1L);

        assertNotNull(exams);
        assertTrue(exams.isEmpty());
    }

    @Test
    void testCannotCreateExamForNullLeadId() {
        assertThrows(Exception.class, () -> {
            examRepository.createExam(null, "JEE Main", 92.5, "percentile");
        });
    }

    @Test
    void testFailedExamDoesNotAffectExistingExams() throws Exception {
        examRepository.createExam(leadId, "JEE Main", 92.5, "percentile");

        assertThrows(Exception.class, () -> {
            examRepository.createExam(-1L, "MHT-CET", 98.0, "percentile");
        });

        List<ExamDTO> exams = examRepository.getExamsByLeadId(leadId);

        assertEquals(1, exams.size());
        assertEquals("JEE Main", exams.get(0).getExamName());
    }

    // ---------- lead deleted ----------

    @Test
    void testExamsDeletedWhenLeadDeleted() throws Exception {
        examRepository.createExam(leadId, "JEE Main", 92.5, "percentile");
        examRepository.createExam(leadId, "MHT-CET", 98.0, "percentile");

        leadRepository.deleteByPhoneNumber(TEST_PHONE_NUMBER);

        assertTrue(examRepository.getExamsByLeadId(leadId).isEmpty());
    }

    @AfterEach
    void cleanUp() {
        if (leadId == null) {
            return;
        }

        // removing testing users from the database (exams are removed by cascade)
        leadRepository.deleteByPhoneNumber(TEST_PHONE_NUMBER);
        leadRepository.deleteByPhoneNumber(OTHER_TEST_PHONE_NUMBER);
    }
}
