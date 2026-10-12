package controller.crm;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/dashboard    totals of every table, recent leads and feedbacks, upcoming schedules,
// resources per category
public class DashboardServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        return repository.dashboard();
    }
}
