package com.example.weblab.servlets;

import com.example.weblab.model.CheckResult;
import com.example.weblab.model.PointChecker;

import com.example.weblab.model.PointRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object isFromController = request.getAttribute("fromController");

        if (isFromController == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        long startTime = System.nanoTime();

        try {
            PointRequest pointRequest = (PointRequest) request.getAttribute("pointRequest");

            double x = pointRequest.getX_value();
            double y = pointRequest.getY_value();
            double r = pointRequest.getR_value();

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

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "One parameter is not a number");
        }
    }
}
