package repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dto.LeadDTO;
import model.Feedback;


public class FeedbackRepoTest {

    private static final String TEST_PHONE_NUMBER = "8459781390";
    private static final String OTHER_TEST_PHONE_NUMBER = "8459781391";

    private FeedbackRepository feedbackRepository;
    private LeadRepository leadRepository;
    private Long leadId;

    @BeforeEach
    void setUp() {
        // creating (or fetching) the testing lead
        leadRepository = new LeadRepository();
        feedbackRepository = new FeedbackRepository();

        LeadDTO lead = leadRepository.getLeadByPhoneNumber(TEST_PHONE_NUMBER);

        leadId = lead.getId();

        assertNotNull(leadId);
    }

    // ---------- multiple feedbacks for one lead ----------

    @Test
    void testCreateMultipleFeedbacksForLead() throws Exception {
        feedbackRepository.makeFeedback(leadId, "Counsellor was very helpful");
        feedbackRepository.makeFeedback(leadId, "Wants hostel details");
        feedbackRepository.makeFeedback(leadId, "Will visit campus on Monday");

        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(leadId);

        assertEquals(3, feedbacks.size());

        List<String> messages = feedbacks.stream().map(Feedback::getMessage).toList();
        assertTrue(messages.contains("Counsellor was very helpful"));
        assertTrue(messages.contains("Wants hostel details"));
        assertTrue(messages.contains("Will visit campus on Monday"));
    }

    @Test
    void testFeedbacksReturnedNewestFirst() throws Exception {
        feedbackRepository.makeFeedback(leadId, "First");
        feedbackRepository.makeFeedback(leadId, "Second");
        feedbackRepository.makeFeedback(leadId, "Third");

        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(leadId);

        assertEquals("Third", feedbacks.get(0).getMessage());
        assertEquals("Second", feedbacks.get(1).getMessage());
        assertEquals("First", feedbacks.get(2).getMessage());
    }

    @Test
    void testEachFeedbackHasOwnId() throws Exception {
        feedbackRepository.makeFeedback(leadId, "One");
        feedbackRepository.makeFeedback(leadId, "Two");

        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(leadId);

        assertEquals(2, feedbacks.size());
        assertNotNull(feedbacks.get(0).getId());
        assertNotNull(feedbacks.get(1).getId());
        assertNotEquals(feedbacks.get(0).getId(), feedbacks.get(1).getId());
    }

    @Test
    void testSameMessageTwiceCreatesTwoFeedbacks() throws Exception {
        feedbackRepository.makeFeedback(leadId, "Same message");
        feedbackRepository.makeFeedback(leadId, "Same message");

        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(leadId);

        assertEquals(2, feedbacks.size());
    }

    @Test
    void testAllFeedbacksBelongToCorrectLead() throws Exception {
        feedbackRepository.makeFeedback(leadId, "One");
        feedbackRepository.makeFeedback(leadId, "Two");

        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(leadId);

        for (Feedback feedback : feedbacks) {
            assertNotNull(feedback.getLead());
            assertEquals(leadId, feedback.getLead().getId());
            assertEquals(TEST_PHONE_NUMBER, feedback.getLead().getPhoneNumber());
        }
    }

    @Test
    void testCreatedAtIsSetForEveryFeedback() throws Exception {
        feedbackRepository.makeFeedback(leadId, "One");
        feedbackRepository.makeFeedback(leadId, "Two");

        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(leadId);

        for (Feedback feedback : feedbacks) {
            assertNotNull(feedback.getCreatedAt());
        }
    }

    @Test
    void testGetFeedbackByLeadIdReturnsLatest() throws Exception {
        feedbackRepository.makeFeedback(leadId, "Old feedback");
        feedbackRepository.makeFeedback(leadId, "Latest feedback");

        Feedback result = feedbackRepository.getFeedbackByLeadId(leadId);

        assertNotNull(result);
        assertEquals("Latest feedback", result.getMessage());
    }

    @Test
    void testFeedbacksAreSeparatedBetweenLeads() throws Exception {
        Long otherLeadId = leadRepository.getLeadByPhoneNumber(OTHER_TEST_PHONE_NUMBER).getId();

        feedbackRepository.makeFeedback(leadId, "For first lead 1");
        feedbackRepository.makeFeedback(leadId, "For first lead 2");
        feedbackRepository.makeFeedback(otherLeadId, "For other lead");

        List<Feedback> first = feedbackRepository.getAllFeedbacksByLeadId(leadId);
        List<Feedback> other = feedbackRepository.getAllFeedbacksByLeadId(otherLeadId);

        assertEquals(2, first.size());
        assertEquals(1, other.size());
        assertEquals("For other lead", other.get(0).getMessage());
    }

    // ---------- lead has no feedback ----------

    @Test
    void testNoFeedbacksReturnsEmptyList() {
        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(leadId);

        assertNotNull(feedbacks);
        assertTrue(feedbacks.isEmpty());
    }

    @Test
    void testNoFeedbackReturnsNullForLatest() {
        Feedback result = feedbackRepository.getFeedbackByLeadId(leadId);

        assertNull(result);
    }

    // ---------- lead does not exist ----------

    @Test
    void testCannotMakeFeedbackForNonExistingLead() {
        Long invalidLeadId = -1L;

        Exception exception = assertThrows(Exception.class, () -> {
            feedbackRepository.makeFeedback(invalidLeadId, "Should not be saved");
        });

        assertTrue(exception.getMessage().contains("Cannot create feedback"));
        assertTrue(feedbackRepository.getAllFeedbacksByLeadId(invalidLeadId).isEmpty());
    }

    @Test
    void testGetAllFeedbacksForNonExistingLeadReturnsEmptyList() {
        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(-1L);

        assertNotNull(feedbacks);
        assertTrue(feedbacks.isEmpty());
    }

    @Test
    void testGetAllFeedbacksForNullLeadIdReturnsEmptyList() {
        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(null);

        assertNotNull(feedbacks);
        assertTrue(feedbacks.isEmpty());
    }

    @Test
    void testCannotMakeFeedbackForNullLeadId() {
        assertThrows(Exception.class, () -> {
            feedbackRepository.makeFeedback(null, "Should not be saved");
        });
    }

    @Test
    void testFailedFeedbackDoesNotAffectExistingFeedbacks() throws Exception {
        feedbackRepository.makeFeedback(leadId, "Valid feedback");

        assertThrows(Exception.class, () -> {
            feedbackRepository.makeFeedback(-1L, "Invalid feedback");
        });

        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(leadId);

        assertEquals(1, feedbacks.size());
        assertEquals("Valid feedback", feedbacks.get(0).getMessage());
    }

    // ---------- lead deleted ----------

    @Test
    void testFeedbacksDeletedWhenLeadDeleted() throws Exception {
        feedbackRepository.makeFeedback(leadId, "Will be removed 1");
        feedbackRepository.makeFeedback(leadId, "Will be removed 2");

        leadRepository.deleteByPhoneNumber(TEST_PHONE_NUMBER);

        assertTrue(feedbackRepository.getAllFeedbacksByLeadId(leadId).isEmpty());
    }

    @AfterEach
    void cleanUp() {
        if (leadId == null) {
            return;
        }

        // removing testing users from the database (feedbacks are removed by cascade)
        leadRepository.deleteByPhoneNumber(TEST_PHONE_NUMBER);
        leadRepository.deleteByPhoneNumber(OTHER_TEST_PHONE_NUMBER);
    }
}
