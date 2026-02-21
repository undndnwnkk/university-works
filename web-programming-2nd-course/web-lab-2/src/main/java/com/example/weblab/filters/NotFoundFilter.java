package com.example.weblab.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@WebFilter("/*")
public class NotFoundFilter implements Filter {
    private final Set<String> registeredServletPaths = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();

        servletContext.getServletRegistrations().values().forEach(servletRegistration -> {
            registeredServletPaths.addAll(servletRegistration.getMappings());
        });

        System.out.println("NotFoundFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,  ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getServletPath();

        if (registeredServletPaths.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        URL resource = request.getServletContext().getResource(path);
        if (resource != null) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, path + " not found");
    }

    @Override
    public void destroy() {}
}
