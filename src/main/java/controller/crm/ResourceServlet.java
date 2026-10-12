package controller.crm;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

// GET /crm/resources         all resources (?category=COURSE&search=&page=&size=)
// GET /crm/resources/{id}    one resource
public class ResourceServlet extends CrmServlet {

    @Override
    protected Object handleGet(HttpServletRequest req) throws CrmException {
        String[] parts = pathParts(req);

        if (parts.length == 0) {
            return repository.listResources(textParam(req, "category"), textParam(req, "search"), page(req));
        }

        if (parts.length > 1) {
            throw CrmException.notFound("Unknown route " + req.getRequestURI());
        }

        Long id = parseId(parts[0], "resource id");
        Map<String, Object> resource = repository.getResource(id);
        if (resource == null) {
            throw CrmException.notFound("Resource " + id + " not found");
        }
        return resource;
    }
}
