package com.example.weblab.servlets;

import com.example.weblab.model.PointRequest;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ControllerServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
            try {
                PointRequest pointRequest = gson.fromJson(request.getReader(), PointRequest.class);

                if (pointRequest == null || pointRequest.getX_value() == null || pointRequest.getY_value() == null || pointRequest.getR_value() == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data: one or more parameters are missing.");
                    return;
                }

                double x = pointRequest.getX_value();
                double y = pointRequest.getY_value();
                double r = pointRequest.getR_value();

                request.setAttribute("x_value", x);
                request.setAttribute("y_value", y);
                request.setAttribute("r_value", r);

                request.setAttribute("fromController", true);

                request.getServletContext().getRequestDispatcher("/area_check").forward(request, response);
                } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON or  you something didn't wrote");
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Content type not supported");
        }
    }
}
