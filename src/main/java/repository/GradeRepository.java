package repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import dto.GradeDTO;
import model.Grade;
import model.Lead;

public class GradeRepository {
    private SessionFactory sessionFactory;

    public GradeRepository() {
        this.sessionFactory = config.DatabaseConfig.getSessionFactory();
    }

    // adds a grade (e.g. "12th", 85.5) for the lead, returns the saved grade
    public GradeDTO createGrade(Long leadId, String qualification, Double percentage) throws Exception {
        validate(qualification, percentage);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Lead lead = session.get(Lead.class, leadId);

            if (lead == null) {
                throw new Exception("Cannot create grade with null user for userId : " + leadId);
            }

            Grade grade = new Grade();
            grade.setLead(lead);
            grade.setQualification(qualification.trim());
            grade.setPercentage(percentage);
            session.persist(grade);

            transaction.commit();
            return new GradeDTO(grade);
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    // all grades of the lead in the order they were added, empty list when there are none
    public List<GradeDTO> getGradesByLeadId(Long leadId) {
        Session session = sessionFactory.openSession();
        Query<Grade> query = session.createQuery("FROM Grade WHERE lead.id = :lead_id ORDER BY id", Grade.class);
        query.setParameter("lead_id", leadId);
        List<Grade> grades = query.list();
        session.close();
        return grades.stream().map(GradeDTO::new).toList();
    }

    // grade of one qualification (e.g. "12th") for the lead, null when not found
    public GradeDTO getGradeByQualification(Long leadId, String qualification) {
        Session session = sessionFactory.openSession();
        Query<Grade> query = session.createQuery(
                "FROM Grade WHERE lead.id = :lead_id AND lower(qualification) = :qualification ORDER BY id DESC",
                Grade.class);
        query.setParameter("lead_id", leadId);
        query.setParameter("qualification", qualification == null ? null : qualification.trim().toLowerCase());
        query.setMaxResults(1);
        Grade grade = query.uniqueResult();
        session.close();
        return grade == null ? null : new GradeDTO(grade);
    }

    // null when the grade does not exist
    public GradeDTO getGradeById(Long gradeId) {
        Session session = sessionFactory.openSession();
        Grade grade = session.get(Grade.class, gradeId);
        session.close();
        return grade == null ? null : new GradeDTO(grade);
    }

    // adds the grade, or updates the percentage if the lead already has this qualification
    // so the agent does not create duplicates when the student repeats their marks
    public GradeDTO saveOrUpdateGrade(Long leadId, String qualification, Double percentage) throws Exception {
        validate(qualification, percentage);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Lead lead = session.get(Lead.class, leadId);

            if (lead == null) {
                throw new Exception("Cannot create grade with null user for userId : " + leadId);
            }

            Query<Grade> query = session.createQuery(
                    "FROM Grade WHERE lead.id = :lead_id AND lower(qualification) = :qualification ORDER BY id DESC",
                    Grade.class);
            query.setParameter("lead_id", leadId);
            query.setParameter("qualification", qualification.trim().toLowerCase());
            query.setMaxResults(1);
            Grade grade = query.uniqueResult();

            if (grade != null) {
                grade.setPercentage(percentage);
            } else {
                grade = new Grade();
                grade.setLead(lead);
                grade.setQualification(qualification.trim());
                grade.setPercentage(percentage);
                session.persist(grade);
            }

            transaction.commit();
            return new GradeDTO(grade);
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void deleteGrade(Long gradeId) throws Exception {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Grade grade = session.get(Grade.class, gradeId);

            if (grade == null) {
                throw new Exception("Grade not found for gradeId : " + gradeId);
            }

            // remove it from the lead's list too, otherwise cascade could save it again
            if (grade.getLead() != null) {
                grade.getLead().getGrades().remove(grade);
            }
            session.remove(grade);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private void validate(String qualification, Double percentage) throws Exception {
        if (qualification == null || qualification.isBlank()) {
            throw new Exception("Qualification is required, e.g. 10th, 12th or Diploma");
        }
        if (percentage == null || percentage.isNaN() || percentage < 0 || percentage > 100) {
            throw new Exception("Percentage must be between 0 and 100, got : " + percentage);
        }
    }
}
