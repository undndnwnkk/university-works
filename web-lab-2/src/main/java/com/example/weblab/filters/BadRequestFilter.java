package com.example.weblab.filters;

import com.example.weblab.model.PointRequest;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;

@WebFilter("/controller")
public class BadRequestFilter implements Filter {
    private final Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!httpRequest.getMethod().equalsIgnoreCase("POST")) {
            chain.doFilter(request, response);
            return;
        }

        // MVVM

        if (httpRequest.getContentType() == null || !httpRequest.getContentType().startsWith("application/json")) {
            httpResponse.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Content-type must be application/json");
            return;
        }

        String jsonBody = httpRequest.getReader().lines().collect(Collectors.joining());

        if (jsonBody.trim().isEmpty()) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Body is empty");
            return;
        }

        try {
            PointRequest pointRequest = gson.fromJson(jsonBody, PointRequest.class);

            if (pointRequest == null || pointRequest.getX_value() == null || pointRequest.getY_value() == null || pointRequest.getR_value() == null) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data: one or more parameters are missing.");
                return;
            }

            double x = pointRequest.getX_value();
            double y = pointRequest.getY_value();
            double r = pointRequest.getR_value();

            if (y <= -3 || y > 5 || x < -4 || x > 4 || r < 1 || r > 5) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect data in request");
                return;
            }

            request.setAttribute("pointRequest", pointRequest);
            chain.doFilter(request, response);
        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid json");
        }
    }
}
