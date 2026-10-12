package controller.crm;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/grades    all grades with their lead (?leadId=&page=&size=)
public class GradeServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        return repository.listGrades(leadIdParam(req), page(req));
    }
}
