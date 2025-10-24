package com.example.weblab.servlets;

import com.example.weblab.model.CheckResult;
import com.example.weblab.model.PointChecker;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
            double r = Double.parseDouble(request.getParameter("r_value"));

            if (y < -3 || y > 5 || x < -4 || x > 4 || r < 1 || r > 5) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect data in request");
                return;
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

            response.sendRedirect(request.getContextPath() + "/index.jsp");

        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "One parameter is not a number");
        }
    }
}
