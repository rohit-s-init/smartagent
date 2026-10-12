package controller.crm;

// error that is sent back to the crm with its http status, e.g. 404 lead not found
public class CrmException extends Exception {

    private final int status;

    public CrmException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static CrmException badRequest(String message) {
        return new CrmException(400, message);
    }

    public static CrmException notFound(String message) {
        return new CrmException(404, message);
    }
}
