package controller.crm;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/feedbacks    all feedbacks, newest first, with their lead (?leadId=&page=&size=)
public class FeedbackServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        return repository.listFeedbacks(leadIdParam(req), page(req));
    }
}
