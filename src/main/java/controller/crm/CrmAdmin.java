package controller.crm;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// hardcoded crm admin account, can read every record in the database
// TODO: move the credentials to .env / database when authentication is added
public final class CrmAdmin {

    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin@123";
    public static final String NAME = "CRM Admin";
    public static final String EMAIL = "admin@smartagent.local";
    public static final String ROLE = "ADMIN";
    public static final List<String> PERMISSIONS = List.of("READ_ALL");

    private CrmAdmin() {
    }

    // will be used by the login route once authentication is added
    public static boolean matches(String username, String password) {
        return USERNAME.equals(username) && PASSWORD.equals(password);
    }

    // admin details that are safe to send to the crm (never the password)
    public static Map<String, Object> profile() {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("username", USERNAME);
        profile.put("name", NAME);
        profile.put("email", EMAIL);
        profile.put("role", ROLE);
        profile.put("permissions", PERMISSIONS);
        return profile;
    }
}
