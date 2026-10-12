package controller.crm;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// base class for every crm route: read only (GET), always answers with json
public abstract class CrmServlet extends HttpServlet {

    // dates are written as "2026-10-12T14:53:00" instead of number arrays
    protected static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected static final int DEFAULT_PAGE_SIZE = 50;
    protected static final int MAX_PAGE_SIZE = 500;

    protected final CrmRepository repository = new CrmRepository();

    // returns the json body for a GET request
    protected abstract Object handleGet(HttpServletRequest req) throws CrmException;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // the crm frontend may run on another port (e.g. vite / react dev server)
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        String method = req.getMethod();

        if ("OPTIONS".equals(method)) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        if (!"GET".equals(method)) {
            sendJson(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    Map.of("error", "Only GET is supported, the crm is read only"));
            return;
        }

        try {
            sendJson(resp, HttpServletResponse.SC_OK, handleGet(req));
        } catch (CrmException e) {
            sendJson(resp, e.getStatus(), Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Something went wrong while reading the data"));
        }
    }

    protected void sendJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        MAPPER.writeValue(resp.getWriter(), body);
    }

    // path after the servlet mapping split into parts, "/5/exams" -> ["5", "exams"]
    protected String[] pathParts(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            return new String[0];
        }
        return path.replaceAll("^/+|/+$", "").split("/+");
    }

    protected Long parseId(String value, String name) throws CrmException {
        try {
            long id = Long.parseLong(value);
            if (id <= 0) {
                throw CrmException.badRequest(name + " must be a positive number");
            }
            return id;
        } catch (NumberFormatException e) {
            throw CrmException.badRequest(name + " must be a number, got '" + value + "'");
        }
    }

    // optional ?leadId= filter
    protected Long leadIdParam(HttpServletRequest req) throws CrmException {
        String value = req.getParameter("leadId");
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseId(value.trim(), "leadId");
    }

    protected String textParam(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    // ?page=1&size=50
    protected Page page(HttpServletRequest req) throws CrmException {
        int page = intParam(req, "page", 1);
        int size = intParam(req, "size", DEFAULT_PAGE_SIZE);

        if (page < 1) {
            throw CrmException.badRequest("page must be 1 or more");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw CrmException.badRequest("size must be between 1 and " + MAX_PAGE_SIZE);
        }
        return new Page(page, size);
    }

    private int intParam(HttpServletRequest req, String name, int defaultValue) throws CrmException {
        String value = req.getParameter(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw CrmException.badRequest(name + " must be a number, got '" + value + "'");
        }
    }

    public record Page(int page, int size) {
        public int offset() {
            return (page - 1) * size;
        }
    }
}
