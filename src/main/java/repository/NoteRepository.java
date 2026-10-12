package repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import dto.NoteDTO;
import model.Lead;
import model.Note;

public class NoteRepository {
    private SessionFactory sessionFactory;

    public NoteRepository() {
        this.sessionFactory = config.DatabaseConfig.getSessionFactory();
    }

    public void makeNote(Long leadId, String content) throws Exception {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Lead lead = session.get(Lead.class, leadId);

            if (lead == null) {
                throw new Exception("Cannot create note with null user for userId : " + leadId);
            }

            Query<Note> query = session.createQuery("FROM Note WHERE lead.id = :lead_id", Note.class);
            query.setParameter("lead_id", leadId);
            Note existingNote = query.uniqueResult();

            if (existingNote != null) {
                String prevCont = existingNote.getContent();
                existingNote.setContent(prevCont + "\n" + content);
            } else {
                Note newNote = new Note();
                newNote.setLead(lead);
                newNote.setContent(content);
                session.persist(newNote);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public Note getNoteByLeadId(Long leadId) {
        Session session = sessionFactory.openSession();
        Query<Note> query = session.createQuery("FROM Note WHERE lead.id = :lead_id", Note.class);
        query.setParameter("lead_id", leadId);
        Note note = query.uniqueResult();
        session.close();
        return note;
    }

    public NoteDTO getNoteDTOByLeadId(Long leadId) {
        Session session = sessionFactory.openSession();
        Query<Note> query = session.createQuery("FROM Note WHERE lead.id = :lead_id", Note.class);
        query.setParameter("lead_id", leadId);
        Note note = query.uniqueResult();
        session.close();
        return new NoteDTO(note);
    }

}
