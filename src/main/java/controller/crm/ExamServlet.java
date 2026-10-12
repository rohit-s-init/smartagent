package controller.crm;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/exams    all exams with their lead (?leadId=&page=&size=)
public class ExamServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        return repository.listExams(leadIdParam(req), page(req));
    }
}
