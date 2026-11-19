package controller.twillio;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Twillio;

public class MakeCallServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        String phoneNumber = req.getParameter("phone");
        Twillio.makeCall(phoneNumber, "https://twiddle-dictate-plunging.ngrok-free.dev/twillio/webhook");
    }
}
