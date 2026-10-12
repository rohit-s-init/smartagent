package repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dto.LeadDTO;
import model.Note;


public class NoteRepositoryTest {

    private static final String TEST_PHONE_NUMBER = "8459781390";

    private NoteRepository noteRepository;
    private LeadRepository leadRepository;
    private Long leadId;

    @BeforeEach
    void setUp() {
        // creating (or fetching) the testing lead
        leadRepository = new LeadRepository();
        noteRepository = new NoteRepository();

        LeadDTO lead = leadRepository.getLeadByPhoneNumber(TEST_PHONE_NUMBER);

        leadId = lead.getId();

        assertNotNull(leadId);
    }

    @Test
    void testCreateNoteWhenLeadExists() throws Exception {
        String content = "Interested in B.Tech CSE";

        noteRepository.makeNote(leadId, content);

        Note result = noteRepository.getNoteByLeadId(leadId);

        assertNotNull(result);
        assertEquals(content, result.getContent());
    }

    // @Test
    // void testUpdateNoteWhenAlreadyExists() throws Exception {
    //     String oldContent = "Call back next week";
    //     String newContent = "Wants hostel details";

    //     noteRepository.makeNote(leadId, oldContent);
    //     noteRepository.makeNote(leadId, newContent);

    //     Note result = noteRepository.getNoteByLeadId(leadId);

    //     assertNotNull(result);
    //     assertEquals(newContent, result.getContent());
    // }

    @Test
    void testNoteNotPresentInitially() {
        Note result = noteRepository.getNoteByLeadId(leadId);

        assertNull(result);
    }

    @Test
    void testCannotMakeNoteForNonExistingLead() {
        Long invalidLeadId = -1L;

        Exception exception = assertThrows(Exception.class, () -> {
            noteRepository.makeNote(invalidLeadId, "Should not be saved");
        });

        assertTrue(exception.getMessage().contains("Cannot create note"));
        assertNull(noteRepository.getNoteByLeadId(invalidLeadId));
    }

    @Test
    void testSameNoteIdAfterUpdate() throws Exception {
        noteRepository.makeNote(leadId, "First note");

        Note first = noteRepository.getNoteByLeadId(leadId);

        noteRepository.makeNote(leadId, "Second note");

        Note second = noteRepository.getNoteByLeadId(leadId);

        assertEquals(first.getId(), second.getId());
    }

    @Test
    void testNoteBelongsToCorrectLead() throws Exception {
        noteRepository.makeNote(leadId, "Linked to lead");

        Note result = noteRepository.getNoteByLeadId(leadId);

        assertNotNull(result.getLead());
        assertEquals(leadId, result.getLead().getId());
        assertEquals(TEST_PHONE_NUMBER, result.getLead().getPhoneNumber());
    }

    @Test
    void testCreatedAtIsSetOnCreate() throws Exception {
        noteRepository.makeNote(leadId, "Timestamp check");

        Note result = noteRepository.getNoteByLeadId(leadId);

        assertNotNull(result.getCreatedAt());
    }

    @Test
    void testNoteDeletedWhenLeadDeleted() throws Exception {
        noteRepository.makeNote(leadId, "Will be removed with lead");

        leadRepository.deleteByPhoneNumber(TEST_PHONE_NUMBER);

        assertNull(noteRepository.getNoteByLeadId(leadId));
    }

    @AfterEach
    void cleanUp() {
        if (leadId == null) {
            return;
        }

        // removing testing user from the database (note is removed by cascade)
        leadRepository.deleteByPhoneNumber(TEST_PHONE_NUMBER);
    }
}
