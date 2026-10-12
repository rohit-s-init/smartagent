package controller.crm;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/chats    all chat messages, newest first, with their lead (?leadId=&page=&size=)
public class ChatServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        return repository.listChats(leadIdParam(req), page(req));
    }
}
