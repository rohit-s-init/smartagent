package controller.crm;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/schedules    all follow-up schedules, earliest time first, with their lead (?leadId=&page=&size=)
public class ScheduleServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        return repository.listSchedules(leadIdParam(req), page(req));
    }
}
