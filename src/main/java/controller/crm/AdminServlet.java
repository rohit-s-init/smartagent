package controller.crm;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/admin    the hardcoded crm admin account (without the password)
public class AdminServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        return CrmAdmin.profile();
    }
}
