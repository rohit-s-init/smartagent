package config;


import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import controller.HomeController;
import controller.crm.AdminServlet;
import controller.crm.ChatServlet;
import controller.crm.CrmIndexServlet;
import controller.crm.DashboardServlet;
import controller.crm.ExamServlet;
import controller.crm.FeedbackServlet;
import controller.crm.GradeServlet;
import controller.crm.LeadServlet;
import controller.crm.NoteServlet;
import controller.crm.ResourceServlet;
import controller.crm.ScheduleServlet;
import controller.twillio.TwillioWebhookServlet;

public class HttpServer {
    protected Server server = new Server(8081);

    public void start() throws Exception{

        ServletContextHandler homeContext = new ServletContextHandler("/public");
        homeContext.addServlet(HomeController.class, "/");

        ServletContextHandler twillioContext = new ServletContextHandler("/twillio");
        twillioContext.addServlet(TwillioWebhookServlet.class, "/webhook");
        // twillioContext.addServlet(TwillioWebhookServlet.class, "/makecall");

        // crm backend, read only json routes (GET /crm lists all of them)
        ServletContextHandler crmContext = new ServletContextHandler("/crm");
        crmContext.addServlet(CrmIndexServlet.class, "/");
        crmContext.addServlet(AdminServlet.class, "/admin");
        crmContext.addServlet(DashboardServlet.class, "/dashboard");
        crmContext.addServlet(LeadServlet.class, "/leads/*");
        crmContext.addServlet(ExamServlet.class, "/exams");
        crmContext.addServlet(GradeServlet.class, "/grades");
        crmContext.addServlet(FeedbackServlet.class, "/feedbacks");
        crmContext.addServlet(ChatServlet.class, "/chats");
        crmContext.addServlet(NoteServlet.class, "/notes");
        crmContext.addServlet(ScheduleServlet.class, "/schedules");
        crmContext.addServlet(ResourceServlet.class, "/resources/*");


        ContextHandlerCollection contextHandlers = new ContextHandlerCollection();
        contextHandlers.addHandler(homeContext);
        contextHandlers.addHandler(twillioContext);
        contextHandlers.addHandler(crmContext);

        server.setHandler(contextHandlers);

        server.start();

    }

    public void stop() throws Throwable{
        server.stop();
    }
    

}
