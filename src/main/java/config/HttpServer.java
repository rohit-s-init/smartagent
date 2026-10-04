package config;


import java.io.IOException;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import controller.HomeController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpServer {
    protected Server server = new Server(8080);

    public void start() throws Exception{

        ServletContextHandler homeContext = new ServletContextHandler("/public");
        homeContext.addServlet(HomeController.class, "/");


        ContextHandlerCollection contextHandlers = new ContextHandlerCollection();
        contextHandlers.addHandler(homeContext);  

        server.setHandler(contextHandlers);

        server.start();

    }

    public void stop() throws Throwable{
        server.stop();
    }
    

}
