package repository;

import java.util.List;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.processing.HQL;
import org.hibernate.query.Query;

import config.DatabaseConfig;
import dto.ChatDTO;
import model.Chat;
import model.Lead;

public class ChatRepository {
    SessionFactory sf;

    public ChatRepository() {
        this.sf = DatabaseConfig.getSessionFactory();
    }

    public List<ChatDTO> getChats(Long leadId) {
        Session session = sf.getCurrentSession();

        Query<Chat> hql = session.createQuery("from Chat", Chat.class);
        List<Chat> chats = hql.getResultList();
        return chats.stream().map(new Function<Chat, ChatDTO>() {
            @Override
            public ChatDTO apply(Chat t) {
                return new ChatDTO(t);
            }

        }).toList();
    }

    public void addChat(Chat chat) {
        Session session = sf.getCurrentSession();

        Transaction transaction = session.beginTransaction();

        session.persist(chat);

        transaction.commit();

    }

    public void addChats(List<ChatDTO> chats, Long leadId) {
        if (chats == null || chats.isEmpty()) {
            return;
        }

        Session session = sf.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Lead lead = session.get(Lead.class, leadId);
            if (lead == null) {
                throw new IllegalArgumentException("Cannot add chats, lead not found for leadId : " + leadId);
            }

            for (ChatDTO dto : chats) {
                Chat chat = new Chat();
                chat.setLead(lead);
                chat.setRole(dto.getRole());
                chat.setMessage(dto.getMessage());
                // createdAt is filled in by @CreationTimestamp
                session.persist(chat);
            }

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public void saveMessage(Long leadId, String role, String message) {
        Session session = sf.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            Lead lead = session.get(Lead.class, leadId);
            if (lead == null) {
                throw new IllegalArgumentException("Lead not found: " + leadId);
            }
            Chat chat = new Chat();
            chat.setLead(lead);
            chat.setRole(role); // "user" or "assistant"
            chat.setMessage(message);
            session.persist(chat); // createdAt set by @CreationTimestamp
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

}
