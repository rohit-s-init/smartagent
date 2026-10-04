package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HomeController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("E:\\java_project_caller\\src\\main\\java\\controller\\Home.html"));
        StringBuilder sb = new StringBuilder();
        String ins;
        while((ins = br.readLine()) != null){
            sb.append(ins);
        }
        resp.getWriter().write(sb.toString());
        return;
    }
    
}
