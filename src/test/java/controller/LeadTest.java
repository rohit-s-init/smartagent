package controller;

import config.DatabaseConfig;
import model.Exam;
import model.Grade;
import model.Lead;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LeadTest {

    @Test
    public void testLead() {

        DatabaseConfig db = new DatabaseConfig();
        db.start();

        try (Session session = db.getSessionFactory().openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                Lead lead = new Lead();
                lead.setPhoneNumber("+919876543210");
                lead.setName("Rohit");
                lead.setLocation("Pune");

                Exam exam = new Exam();
                exam.setExamName("JEE");
                exam.setScore(90.0);
                exam.setLead(lead);

                Grade grade = new Grade();
                grade.setQualification("12th");
                grade.setPercentage(85.0);
                grade.setLead(lead);

                lead.getExams().add(exam);
                lead.getGrades().add(grade);

                session.persist(lead);
                session.flush();

                assertNotNull(lead.getId());
                assertNotNull(exam.getId());
                assertNotNull(grade.getId());

                tx.rollback();

            } catch (Exception e) {
                tx.rollback();
                throw e;
            }

        } finally {
            db.stop();
        }
    }
}