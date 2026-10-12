package controller.crm;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm    list of every crm route, unknown /crm/... paths get a json 404
public class CrmIndexServlet extends CrmServlet {

    private static final Map<String, String> ROUTES = new LinkedHashMap<>();

    static {
        ROUTES.put("GET /crm/admin", "hardcoded admin account (no password)");
        ROUTES.put("GET /crm/dashboard", "totals, recent leads and feedbacks, upcoming schedules, resources per category");
        ROUTES.put("GET /crm/leads", "all leads with record counts (?search=&page=&size=)");
        ROUTES.put("GET /crm/leads/{id}", "one lead with exams, grades, feedbacks, chats, note and schedule");
        ROUTES.put("GET /crm/leads/{id}/exams", "exams of a lead (?page=&size=)");
        ROUTES.put("GET /crm/leads/{id}/grades", "grades of a lead (?page=&size=)");
        ROUTES.put("GET /crm/leads/{id}/feedbacks", "feedbacks of a lead (?page=&size=)");
        ROUTES.put("GET /crm/leads/{id}/chats", "chats of a lead in conversation order (?page=&size=)");
        ROUTES.put("GET /crm/leads/{id}/note", "note of a lead");
        ROUTES.put("GET /crm/leads/{id}/schedule", "follow-up schedule of a lead");
        ROUTES.put("GET /crm/exams", "all exams (?leadId=&page=&size=)");
        ROUTES.put("GET /crm/grades", "all grades (?leadId=&page=&size=)");
        ROUTES.put("GET /crm/feedbacks", "all feedbacks (?leadId=&page=&size=)");
        ROUTES.put("GET /crm/chats", "all chat messages (?leadId=&page=&size=)");
        ROUTES.put("GET /crm/notes", "all notes (?leadId=&page=&size=)");
        ROUTES.put("GET /crm/schedules", "all follow-up schedules (?leadId=&page=&size=)");
        ROUTES.put("GET /crm/resources", "all resources (?category=&search=&page=&size=)");
        ROUTES.put("GET /crm/resources/{id}", "one resource");
    }

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        String path = req.getServletPath() + (req.getPathInfo() == null ? "" : req.getPathInfo());

        if (!path.isEmpty() && !path.equals("/")) {
            throw CrmException.notFound("Unknown route " + req.getRequestURI());
        }

        Map<String, Object> index = new LinkedHashMap<>();
        index.put("name", "SmartAgent CRM API");
        index.put("routes", ROUTES);
        return index;
    }
}
