package repository;

import java.util.List;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import dto.FeedbackDTO;
import model.Feedback;
import model.Lead;

public class FeedbackRepository {
    private SessionFactory sessionFactory;

    public FeedbackRepository() {
        this.sessionFactory = config.DatabaseConfig.getSessionFactory();
    }

    public void makeFeedback(Long leadId, String content) throws Exception {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

       try{
         Lead lead = session.get(Lead.class,leadId);

        if(lead == null){
            throw new Exception("Cannot create feedback with null user for userId : "+leadId);
        }

        Feedback feedback = new Feedback();
        feedback.setLead(lead);
        feedback.setMessage(content);
        session.persist(feedback);

        transaction.commit();
       }
       catch(Exception e){
            transaction.rollback();
            throw e;
       }
       finally{
            session.close();
       }
    }

    // returns the latest feedback of the lead
    public Feedback getFeedbackByLeadId(Long leadId) {
        Session session = sessionFactory.openSession();
        Query<Feedback> query = session.createQuery("FROM Feedback WHERE lead.id = :lead_id ORDER BY createdAt DESC, id DESC", Feedback.class);
        query.setParameter("lead_id", leadId);
        query.setMaxResults(1);
        Feedback feedback = query.uniqueResult();
        session.close();
        return feedback;
    }

    public List<Feedback> getAllFeedbacksByLeadId(Long leadId) {
        Session session = sessionFactory.openSession();
        Query<Feedback> query = session.createQuery("FROM Feedback WHERE lead.id = :lead_id ORDER BY createdAt DESC, id DESC", Feedback.class);
        query.setParameter("lead_id", leadId);
        List<Feedback> feedbacks = query.list();
        session.close();
        return feedbacks;
    }

    public List<FeedbackDTO> getAllFeedbacksDTOByLeadId(Long leadId) {
        Session session = sessionFactory.openSession();
        Query<Feedback> query = session.createQuery("FROM Feedback WHERE lead.id = :lead_id ORDER BY createdAt DESC, id DESC", Feedback.class);
        query.setParameter("lead_id", leadId);
        List<Feedback> feedbacks = query.list();
        session.close();
        return feedbacks.stream().map(new Function<Feedback,FeedbackDTO>() {
            @Override
            public FeedbackDTO apply(Feedback feedback) {
                return new FeedbackDTO(feedback);
            }
        }).toList();
    }
    

}
