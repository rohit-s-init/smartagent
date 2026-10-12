package controller.crm;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/notes    all notes, newest first, with their lead (?leadId=&page=&size=)
public class NoteServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        return repository.listNotes(leadIdParam(req), page(req));
    }
}
