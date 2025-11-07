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
        PointRequest pointRequest = (PointRequest) request.getAttribute("pointRequest");

        try {
            request.setAttribute("x_value", pointRequest.getX_value());
            request.setAttribute("y_value", pointRequest.getY_value());
            request.setAttribute("r_value", pointRequest.getR_value());

            request.setAttribute("fromController", true);

            request.getServletContext().getRequestDispatcher("/area_check").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error :(");
        }
    }
}
