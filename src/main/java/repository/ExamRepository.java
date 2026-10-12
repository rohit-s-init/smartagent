package repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import dto.ExamDTO;
import model.Exam;
import model.Lead;

public class ExamRepository {
    private SessionFactory sessionFactory;

    public ExamRepository() {
        this.sessionFactory = config.DatabaseConfig.getSessionFactory();
    }

    public void createExam(Long leadId, String examName, Double score, String scoreType) throws Exception {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Lead lead = session.get(Lead.class, leadId);

            if (lead == null) {
                throw new Exception("Cannot create exam with null user for userId : " + leadId);
            }

            Exam exam = new Exam();
            exam.setLead(lead);
            exam.setExamName(examName);
            exam.setScore(score);
            exam.setScoreType(scoreType);
            session.persist(exam);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<ExamDTO> getExamsByLeadId(Long leadId) {
        Session session = sessionFactory.openSession();
        Query<Exam> query = session.createQuery("FROM Exam WHERE lead.id = :lead_id", Exam.class);
        query.setParameter("lead_id", leadId);
        List<Exam> exams = query.list();
        session.close();

        List<ExamDTO> examDTOs = new ArrayList<>();
        for (Exam exam : exams) {
            examDTOs.add(new ExamDTO(exam));
        }
        return examDTOs;
    }
}
