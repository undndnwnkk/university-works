package com.example.weblab.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String x =  request.getParameter("x_value");
        String y = request.getParameter("y_value");
        String r = request.getParameter("z_value");

        if (x != null && y != null && r != null) {
            request.getServletContext().getRequestDispatcher("/check-area").forward(request, response);
        } else {
            request.getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}
