package repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import config.DatabaseConfig;
import dto.LeadDTO;
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

    public void update(LeadDTO lead) {
        Session session = sf.openSession();
        Transaction transaction = session.beginTransaction();

        // session.persist(lead);
        Lead existingLead = session.get(Lead.class, lead.getId());
        if (existingLead == null) {
            session.persist(lead);
        } else {
            if (lead.getName() != null) {
                existingLead.setName(lead.getName());
            }
            if (lead.getLocation() != null) {
                existingLead.setLocation(lead.getLocation());
            }
            if (lead.getSchooling() != null) {
                existingLead.setSchooling(lead.getSchooling());
            }
            if (lead.getEmail() != null) {
                existingLead.setEmail(lead.getEmail());
            }
        }

        transaction.commit();
        session.close();
    }

    public LeadDTO getLeadByPhoneNumber(String phoneNumber) {

        Session session = sf.openSession();

        Lead lead = session.createQuery("FROM Lead WHERE phoneNumber = :phoneNumber", Lead.class)
                .setParameter("phoneNumber", phoneNumber)
                .uniqueResult();

        if (lead == null) {
            lead = new Lead();
            lead.setPhoneNumber(phoneNumber);
            session.beginTransaction();
            session.persist(lead);
            session.getTransaction().commit();
        }

        session.close();

        return new LeadDTO(lead.getId(), lead.getPhoneNumber(), lead.getName(), lead.getLocation(), lead.getSchooling(), lead.getEmail());
    }

    public LeadDTO getLeadById(Long id) {

        Session session = sf.openSession();

        Lead lead = session.get(Lead.class, id);

        session.close();

        return new LeadDTO(lead.getId(), lead.getPhoneNumber(), lead.getName(), lead.getLocation(), lead.getSchooling(), lead.getEmail());
    }

    public void deleteByPhoneNumber(String phoneNumber) {

        Session session = sf.openSession();
        Transaction transaction = session.beginTransaction();
   
        try {
            Lead lead = session.createQuery(
                    "FROM Lead WHERE phoneNumber = :phoneNumber",
                    Lead.class)
                    .setParameter("phoneNumber", phoneNumber)
                    .uniqueResult();

            if (lead != null) {
                session.remove(lead);
            }

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            throw e;

        } finally {
            session.close();
        }
    }
}
