package controller.crm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import config.DatabaseConfig;
import controller.crm.CrmServlet.Page;
import model.Chat;
import model.Exam;
import model.Feedback;
import model.Grade;
import model.Lead;
import model.Note;
import model.Resource;
import model.Schedule;

// read only queries used by the crm, every method opens and closes its own session
// rows are turned into maps inside the session so lazy relations are already loaded
public class CrmRepository {

    private final SessionFactory sessionFactory;

    public CrmRepository() {
        this.sessionFactory = DatabaseConfig.getSessionFactory();
    }

    // ---------- dashboard ----------

    public Map<String, Object> dashboard() {
        try (Session session = sessionFactory.openSession()) {
            Map<String, Object> totals = new LinkedHashMap<>();
            totals.put("leads", count(session, "Lead"));
            totals.put("exams", count(session, "Exam"));
            totals.put("grades", count(session, "Grade"));
            totals.put("feedbacks", count(session, "Feedback"));
            totals.put("chats", count(session, "Chat"));
            totals.put("notes", count(session, "Note"));
            totals.put("schedules", count(session, "Schedule"));
            totals.put("resources", count(session, "Resource"));

            List<Map<String, Object>> recentLeads = session
                    .createQuery("from Lead l order by l.id desc", Lead.class)
                    .setMaxResults(5)
                    .list().stream().map(this::leadRow).toList();

            List<Map<String, Object>> recentFeedbacks = session
                    .createQuery("from Feedback f left join fetch f.lead order by f.createdAt desc, f.id desc", Feedback.class)
                    .setMaxResults(5)
                    .list().stream().map(f -> feedbackRow(f, true)).toList();

            List<Map<String, Object>> upcomingSchedules = session
                    .createQuery("from Schedule s left join fetch s.lead order by s.scheduledTime asc", Schedule.class)
                    .setMaxResults(10)
                    .list().stream().map(s -> scheduleRow(s, true)).toList();

            Map<String, Object> resourcesByCategory = new LinkedHashMap<>();
            session.createQuery("select r.category, count(r) from Resource r group by r.category order by r.category", Object[].class)
                    .list()
                    .forEach(row -> resourcesByCategory.put(String.valueOf(row[0]), row[1]));

            Map<String, Object> dashboard = new LinkedHashMap<>();
            dashboard.put("totals", totals);
            dashboard.put("recentLeads", recentLeads);
            dashboard.put("recentFeedbacks", recentFeedbacks);
            dashboard.put("upcomingSchedules", upcomingSchedules);
            dashboard.put("resourcesByCategory", resourcesByCategory);
            return dashboard;
        }
    }

    // ---------- leads ----------

    // search matches name, phone number, email or location
    public Map<String, Object> listLeads(String search, Page page) {
        try (Session session = sessionFactory.openSession()) {
            String where = "";
            Map<String, Object> params = new LinkedHashMap<>();

            if (search != null) {
                where = " where lower(l.name) like :q or l.phoneNumber like :q"
                        + " or lower(l.email) like :q or lower(l.location) like :q";
                params.put("q", "%" + search.toLowerCase() + "%");
            }

            Query<Long> countQuery = session.createQuery("select count(l) from Lead l" + where, Long.class);
            params.forEach(countQuery::setParameter);
            long total = countQuery.uniqueResult();

            Query<Object[]> query = session.createQuery(
                    "select l, size(l.exams), size(l.grades), size(l.feedbacks), size(l.chats)"
                            + " from Lead l" + where + " order by l.id desc",
                    Object[].class);
            params.forEach(query::setParameter);
            query.setFirstResult(page.offset()).setMaxResults(page.size());

            List<Map<String, Object>> items = new ArrayList<>();
            for (Object[] row : query.list()) {
                Lead lead = (Lead) row[0];
                Map<String, Object> item = leadRow(lead);

                Map<String, Object> counts = new LinkedHashMap<>();
                counts.put("exams", row[1]);
                counts.put("grades", row[2]);
                counts.put("feedbacks", row[3]);
                counts.put("chats", row[4]);
                item.put("counts", counts);

                item.put("hasNote", lead.getNote() != null);
                item.put("scheduledTime", lead.getSchedule() == null ? null : lead.getSchedule().getScheduledTime());
                items.add(item);
            }

            return pageResult(items, total, page);
        }
    }

    public boolean leadExists(Long leadId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Lead.class, leadId) != null;
        }
    }

    // lead with everything linked to it, null when the lead does not exist
    public Map<String, Object> getLeadDetail(Long leadId) {
        try (Session session = sessionFactory.openSession()) {
            Lead lead = session.get(Lead.class, leadId);
            if (lead == null) {
                return null;
            }

            Map<String, Object> detail = leadRow(lead);

            detail.put("exams", lead.getExams().stream()
                    .sorted(Comparator.comparing(Exam::getId))
                    .map(e -> examRow(e, false)).toList());

            detail.put("grades", lead.getGrades().stream()
                    .sorted(Comparator.comparing(Grade::getId))
                    .map(g -> gradeRow(g, false)).toList());

            // newest feedback first
            detail.put("feedbacks", lead.getFeedbacks().stream()
                    .sorted(Comparator.comparing(Feedback::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing(Feedback::getId, Comparator.reverseOrder()))
                    .map(f -> feedbackRow(f, false)).toList());

            // conversation in the order it happened
            detail.put("chats", lead.getChats().stream()
                    .sorted(Comparator.comparing(Chat::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder()))
                            .thenComparing(Chat::getId))
                    .map(c -> chatRow(c, false)).toList());

            detail.put("note", lead.getNote() == null ? null : noteRow(lead.getNote(), false));
            detail.put("schedule", lead.getSchedule() == null ? null : scheduleRow(lead.getSchedule(), false));
            return detail;
        }
    }

    // ---------- records linked to a lead (optional leadId filter) ----------

    public Map<String, Object> listExams(Long leadId, Page page) {
        return listByLead("Exam", Exam.class, leadId, "x.id desc", page, e -> examRow(e, leadId == null));
    }

    public Map<String, Object> listGrades(Long leadId, Page page) {
        return listByLead("Grade", Grade.class, leadId, "x.id desc", page, g -> gradeRow(g, leadId == null));
    }

    public Map<String, Object> listFeedbacks(Long leadId, Page page) {
        return listByLead("Feedback", Feedback.class, leadId, "x.createdAt desc, x.id desc", page,
                f -> feedbackRow(f, leadId == null));
    }

    // all chats: newest first, chats of one lead: in conversation order
    public Map<String, Object> listChats(Long leadId, Page page) {
        String orderBy = leadId == null ? "x.createdAt desc, x.id desc" : "x.createdAt asc, x.id asc";
        return listByLead("Chat", Chat.class, leadId, orderBy, page, c -> chatRow(c, leadId == null));
    }

    public Map<String, Object> listNotes(Long leadId, Page page) {
        return listByLead("Note", Note.class, leadId, "x.createdAt desc, x.id desc", page, n -> noteRow(n, leadId == null));
    }

    public Map<String, Object> listSchedules(Long leadId, Page page) {
        return listByLead("Schedule", Schedule.class, leadId, "x.scheduledTime asc", page,
                s -> scheduleRow(s, leadId == null));
    }

    // ---------- resources ----------

    // category is an exact match (e.g. COURSE), search matches the title
    public Map<String, Object> listResources(String category, String search, Page page) {
        try (Session session = sessionFactory.openSession()) {
            List<String> conditions = new ArrayList<>();
            Map<String, Object> params = new LinkedHashMap<>();

            if (category != null) {
                conditions.add("upper(r.category) = :category");
                params.put("category", category.toUpperCase());
            }
            if (search != null) {
                conditions.add("lower(r.title) like :q");
                params.put("q", "%" + search.toLowerCase() + "%");
            }

            String where = conditions.isEmpty() ? "" : " where " + String.join(" and ", conditions);

            Query<Long> countQuery = session.createQuery("select count(r) from Resource r" + where, Long.class);
            params.forEach(countQuery::setParameter);
            long total = countQuery.uniqueResult();

            Query<Resource> query = session.createQuery("from Resource r" + where + " order by r.id asc", Resource.class);
            params.forEach(query::setParameter);
            query.setFirstResult(page.offset()).setMaxResults(page.size());

            return pageResult(query.list().stream().map(this::resourceRow).toList(), total, page);
        }
    }

    public Map<String, Object> getResource(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Resource resource = session.get(Resource.class, id);
            return resource == null ? null : resourceRow(resource);
        }
    }

    // ---------- helpers ----------

    private long count(Session session, String entity) {
        return session.createQuery("select count(x) from " + entity + " x", Long.class).uniqueResult();
    }

    private <T> Map<String, Object> listByLead(String entity, Class<T> type, Long leadId, String orderBy,
            Page page, Function<T, Map<String, Object>> mapper) {

        try (Session session = sessionFactory.openSession()) {
            String where = leadId == null ? "" : " where x.lead.id = :leadId";

            Query<Long> countQuery = session.createQuery("select count(x) from " + entity + " x" + where, Long.class);
            if (leadId != null) {
                countQuery.setParameter("leadId", leadId);
            }
            long total = countQuery.uniqueResult();

            Query<T> query = session.createQuery(
                    "select x from " + entity + " x left join fetch x.lead" + where + " order by " + orderBy, type);
            if (leadId != null) {
                query.setParameter("leadId", leadId);
            }
            query.setFirstResult(page.offset()).setMaxResults(page.size());

            return pageResult(query.list().stream().map(mapper).toList(), total, page);
        }
    }

    private Map<String, Object> pageResult(List<Map<String, Object>> items, long total, Page page) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("page", page.page());
        result.put("size", page.size());
        result.put("total", total);
        result.put("totalPages", total == 0 ? 0 : (total + page.size() - 1) / page.size());
        result.put("items", items);
        return result;
    }

    // ---------- rows ----------

    private Map<String, Object> leadRow(Lead lead) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", lead.getId());
        row.put("phoneNumber", lead.getPhoneNumber());
        row.put("name", lead.getName());
        row.put("email", lead.getEmail());
        row.put("location", lead.getLocation());
        row.put("schooling", lead.getSchooling());
        return row;
    }

    // short lead info shown next to records in the global lists
    private Map<String, Object> leadRef(Lead lead) {
        if (lead == null) {
            return null;
        }
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("id", lead.getId());
        ref.put("name", lead.getName());
        ref.put("phoneNumber", lead.getPhoneNumber());
        return ref;
    }

    private Map<String, Object> examRow(Exam exam, boolean withLead) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", exam.getId());
        row.put("examName", exam.getExamName());
        row.put("score", exam.getScore());
        row.put("scoreType", exam.getScoreType());
        if (withLead) {
            row.put("lead", leadRef(exam.getLead()));
        }
        return row;
    }

    private Map<String, Object> gradeRow(Grade grade, boolean withLead) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", grade.getId());
        row.put("qualification", grade.getQualification());
        row.put("percentage", grade.getPercentage());
        if (withLead) {
            row.put("lead", leadRef(grade.getLead()));
        }
        return row;
    }

    private Map<String, Object> feedbackRow(Feedback feedback, boolean withLead) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", feedback.getId());
        row.put("message", feedback.getMessage());
        row.put("createdAt", feedback.getCreatedAt());
        if (withLead) {
            row.put("lead", leadRef(feedback.getLead()));
        }
        return row;
    }

    private Map<String, Object> chatRow(Chat chat, boolean withLead) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", chat.getId());
        row.put("role", chat.getRole());
        row.put("message", chat.getMessage());
        row.put("createdAt", chat.getCreatedAt());
        if (withLead) {
            row.put("lead", leadRef(chat.getLead()));
        }
        return row;
    }

    private Map<String, Object> noteRow(Note note, boolean withLead) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", note.getId());
        row.put("content", note.getContent());
        row.put("createdAt", note.getCreatedAt());
        if (withLead) {
            row.put("lead", leadRef(note.getLead()));
        }
        return row;
    }

    private Map<String, Object> scheduleRow(Schedule schedule, boolean withLead) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", schedule.getId());
        row.put("scheduledTime", schedule.getScheduledTime());
        if (withLead) {
            row.put("lead", leadRef(schedule.getLead()));
        }
        return row;
    }

    private Map<String, Object> resourceRow(Resource resource) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", resource.getId());
        row.put("category", resource.getCategory());
        row.put("title", resource.getTitle());
        row.put("description", resource.getDescription());
        return row;
    }
}
