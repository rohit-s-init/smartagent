package repository;

import java.time.LocalTime;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import dto.LeadDTO;
import dto.ScheduleDAO;
import model.Lead;
import model.Schedule;

public class ScheduleRepository {
    private SessionFactory sessionFactory;

    public ScheduleRepository() {
        this.sessionFactory = config.DatabaseConfig.getSessionFactory();
    }

    public void makeSchedule(Schedule schedule) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Query<Schedule> query = session.createQuery("FROM Schedule WHERE lead.id = :lead_id", Schedule.class);
        query.setParameter("lead_id", schedule.getLead().getId());
        Schedule existingSchedule = query.uniqueResult();

        if(existingSchedule != null) {
            existingSchedule.setScheduledTime(schedule.getScheduledTime());
        } else {
            session.persist(schedule);
        }

        transaction.commit();
        
        session.close();
    }

    public void makeSchedule(Long leadId,LocalTime time) throws Exception{
        
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

       try{
         Lead lead = session.get(Lead.class,leadId);

        if(lead == null){
            throw new Exception("Cannot create schedule with null user for userId : "+leadId);
        }

        Query<Schedule> query = session.createQuery("FROM Schedule WHERE lead.id = :lead_id", Schedule.class);
        query.setParameter("lead_id", leadId);
        Schedule existingSchedule = query.uniqueResult();


        if(existingSchedule != null) {
            existingSchedule.setScheduledTime(time);
        } else {
            Schedule newSchedule = new Schedule(time,lead);
            session.persist(newSchedule);
            
        }
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

    public ScheduleDAO getScheduleByLeadId(Long leadId) {
        Session session = sessionFactory.openSession();
        Query<Schedule> query = session.createQuery("FROM Schedule WHERE lead.id = :lead_id", Schedule.class);
        query.setParameter("lead_id", leadId);
        Schedule schedule = query.uniqueResult();
        session.close();

        if (schedule != null) {
            LeadDTO leadDTO = new LeadDTO(schedule.getLead().getId(), schedule.getLead().getPhoneNumber(), schedule.getLead().getName(), schedule.getLead().getLocation(), schedule.getLead().getSchooling(), schedule.getLead().getEmail());
            return new ScheduleDAO(schedule.getId(), schedule.getScheduledTime(), leadDTO);
        } else {
            return null;
        }
    }
    

}
