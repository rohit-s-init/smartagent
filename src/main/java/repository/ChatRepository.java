package repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.processing.HQL;
import org.hibernate.query.Query;

import config.DatabaseConfig;
import model.Chat;
import model.Lead;

public class ChatRepository {
    SessionFactory sf;

    public ChatRepository() {
        this.sf = DatabaseConfig.getSessionFactory();
    }

    public List<Chat> getChats(Long leadId) {
        Session session = sf.getCurrentSession();

        Query<Chat> hql = session.createQuery("from Chat", Chat.class);
        List<Chat> chats = hql.getResultList();
        return chats;
    }

    public void addChat(Chat chat){
        Session session = sf.getCurrentSession();

        Transaction transaction = session.beginTransaction();

        session.persist(chat);

        transaction.commit();

    }
}
