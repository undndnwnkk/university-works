package com.example.weblab.servlets;

import com.example.weblab.model.CheckResult;
import com.example.weblab.model.PointChecker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long startTime = System.nanoTime();

        try {
            double x = Double.parseDouble(request.getParameter("x_value"));
            double y = Double.parseDouble(request.getParameter("y_value"));
            double r = Double.parseDouble(request.getParameter("z_value"));

            if (y < -3 || y > 5 || x < -4 || x > 4 || r < 1 || r > 5) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect data in request");
            }

            boolean hit = PointChecker.checkHit(x, y, r);
            long executionTime = System.nanoTime() - startTime;
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
            CheckResult result = new CheckResult(x, y, r, hit, now, executionTime);

            HttpSession session = request.getSession();
            List<CheckResult> results = (List<CheckResult>) session.getAttribute("results");

            if (results == null) {
                results = new ArrayList<>();
            }

            results.add(result);
            session.setAttribute("results", results);

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Check result</title></head>");
            out.println("<body>");
            out.println("<h1>Check result</h1>");
            out.println("<table border='1'>");
            out.println("<tr><td>Param</td><td>Value</td></tr>");
            out.println("<tr><td>X</td><td>" + x + "</td></tr>");
            out.println("<tr><td>Y</td><td>" + y + "</td></tr>");
            out.println("<tr><td>R</td><td>" + r + "</td></tr>");
            out.println("<tr><td>Result</td><td><b>" + (hit ? "+" : "-") + "</b></td></tr>");
            out.println("</table>");
            out.println("<p><a href='" + request.getContextPath() + "/controller'>Go to main page</a></p>");
            out.println("</body></html>");

        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "One parameter is not a number");
        }
    }
}
