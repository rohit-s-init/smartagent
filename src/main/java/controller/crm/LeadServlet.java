package controller.crm;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/leads                      all leads (?search=&page=&size=)
// GET /crm/leads/{id}                 one lead with exams, grades, feedbacks, chats, note and schedule
// GET /crm/leads/{id}/exams           exams of the lead       (?page=&size=)
// GET /crm/leads/{id}/grades          grades of the lead      (?page=&size=)
// GET /crm/leads/{id}/feedbacks       feedbacks of the lead   (?page=&size=)
// GET /crm/leads/{id}/chats           chats of the lead       (?page=&size=)
// GET /crm/leads/{id}/note            note of the lead
// GET /crm/leads/{id}/schedule        follow-up schedule of the lead
public class LeadServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        String[] parts = pathParts(req);

        if (parts.length == 0) {
            return repository.listLeads(textParam(req, "search"), page(req));
        }

        Long leadId = parseId(parts[0], "lead id");

        if (parts.length == 1) {
            Map<String, Object> lead = repository.getLeadDetail(leadId);
            if (lead == null) {
                throw CrmException.notFound("Lead " + leadId + " not found");
            }
            return lead;
        }

        if (parts.length > 2) {
            throw CrmException.notFound("Unknown route " + req.getRequestURI());
        }

        if (!repository.leadExists(leadId)) {
            throw CrmException.notFound("Lead " + leadId + " not found");
        }

        return switch (parts[1]) {
            case "exams" -> repository.listExams(leadId, page(req));
            case "grades" -> repository.listGrades(leadId, page(req));
            case "feedbacks" -> repository.listFeedbacks(leadId, page(req));
            case "chats" -> repository.listChats(leadId, page(req));
            case "note" -> single(repository.listNotes(leadId, new Page(1, 1)), "Lead " + leadId + " has no note");
            case "schedule" -> single(repository.listSchedules(leadId, new Page(1, 1)),
                    "Lead " + leadId + " has no schedule");
            default -> throw CrmException.notFound("Unknown route " + req.getRequestURI());
        };
    }

    // note and schedule are one per lead, so send the record itself instead of a page
    private Object single(Map<String, Object> pageResult, String notFoundMessage) throws CrmException {
        List<?> items = (List<?>) pageResult.get("items");
        if (items.isEmpty()) {
            throw CrmException.notFound(notFoundMessage);
        }
        return items.get(0);
    }
}
