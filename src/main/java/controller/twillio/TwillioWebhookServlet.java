package controller.twillio;

import java.io.IOException;

import dto.LeadDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import langchain.Agent;
import repository.LeadRepository;

public class TwillioWebhookServlet extends HttpServlet {

    // created once and shared by every call and turn, so the chat memory is kept
    private final Agent agent = new Agent();
    private final LeadRepository leadRepository = new LeadRepository();

    // Indian English for both speech recognition (<Gather>) and the voice (<Say>)
    private static final String LANGUAGE = "en-IN";
    // natural sounding neural voice, e.g. "Google.en-IN-Neural2-A" also works
    private static final String VOICE = "Polly.Kajal-Neural";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String speech = req.getParameter("SpeechResult");
        String callSid = req.getParameter("CallSid");
        // set on the <Redirect> below, means the student did not say anything last turn
        boolean silent = req.getParameter("silent") != null;

        // relative path "/twillio/webhook", twilio resolves it against the https url it
        // called
        // (getRequestURL() gives http:// behind ngrok, which twilio cannot reach)
        String url = "https://twiddle-dictate-plunging.ngrok-free.dev/twillio/webhook";

        // outbound call (we call the student): the student is "To"
        // inbound call (student calls us): the student is "From"
        String studentNumber = req.getParameter("To");

        System.out.println("speech is : " + speech);

        String reply;

        if ((speech == null || speech.isBlank()) && silent) {
            // student went quiet, check in instead of repeating the greeting
            reply = "Hello? Are you still there? Take your time, I'm listening.";
        } else if (speech == null || speech.isBlank()) {
            reply = "Hi there! This is Kajal from MIT ADT University. "
                    + "I'm here to help you with admissions, courses, or anything else you'd like to know. "
                    + "So, what can I help you with today?";
        } else {
            System.out.println("Call: " + callSid);
            System.out.println("Student: " + speech);

            try {
                LeadDTO lead = leadRepository.getLeadByPhoneNumber(toLocalNumber(studentNumber));
                reply = agent.chat(lead.getId(), speech);
            } catch (Exception e) {
                // twilio waits only ~15 seconds, so answer something instead of failing the
                // call
                System.out.println("Agent failed for call " + callSid + " : " + e.getMessage());
                reply = "Oh, sorry, I didn't quite catch that. Could you say it once more?";
            }

            System.out.println("Agent: " + reply);
        }

        resp.setContentType("text/xml;charset=UTF-8");

        resp.getWriter().print(
                "<Response>" +
                        // phone_call + enhanced is twilio's most accurate model for phone audio
                        // the student can start talking while the agent is still speaking
                        "<Gather input=\"speech\" action=\"" + url + "\" " +
                        "method=\"POST\" speechTimeout=\"auto\" " +
                        "speechModel=\"phone_call\" enhanced=\"true\" " +
                        "language=\"" + LANGUAGE + "\">" +
                        "<Say voice=\"" + VOICE + "\" language=\"" + LANGUAGE + "\">" +
                        escapeXml(reply) +
                        "</Say>" +
                        "</Gather>" +
                        // reached only when the student said nothing
                        "<Redirect method=\"POST\">" + url + "?silent=true</Redirect>" +
                        "</Response>");

    }

    // twilio sends "+918459781390", the leads table stores "8459781390"
    private String toLocalNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.startsWith("+91") ? number.substring(3) : number;
    }

    private String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
