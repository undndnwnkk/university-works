package com.example.weblab.metrics;

import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.servlet.jakarta.exporter.MetricsServlet;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "PrometheusServlet", urlPatterns = "/metrics")
public class PrometheusServlet extends MetricsServlet {
    static {
        DefaultExports.initialize();
    }
}
