package repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import config.DatabaseConfig;
import model.Lead;

public class LeadRepository {

    private SessionFactory sf;

    public LeadRepository() {
        this.sf = DatabaseConfig.getSessionFactory();
    }

    public void save(Lead lead) {

        Session session = sf.openSession();
        Transaction transaction = null;

        try {

            transaction = session.beginTransaction();

            session.persist(lead);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw e;

        } finally {

            session.close();
        }
    }

    public void update(Lead updatedLead) {

        Session session = sf.openSession();
        Transaction transaction = session.beginTransaction();

        Lead lead = session.get(Lead.class, updatedLead.getId());

        lead.setName(updatedLead.getName());
        lead.setLocation(updatedLead.getLocation());
        lead.setSchooling(updatedLead.getSchooling());

        transaction.commit();
        session.close();
    }

    public Lead getLeadById(Long id) {

        Session session = sf.openSession();

        Lead lead = session.get(Lead.class, id);

        session.close();

        return lead;
    }
}