package com.resources.controller;

import com.resources.model.Pricing;
import com.resources.service.LaundryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/admin/*")
public class AdminController extends HttpServlet {
    
    private LaundryService service;
    
    @Override
    public void init() {
        service = new LaundryService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        
        try {
            if ("/pricing".equals(path)) {
                Pricing p = service.getPricing();
                out.write("{\"success\":true,\"pricing\":{\"id\":" + p.getId() + ",\"washPrice\":" + p.getWashPrice() + ",\"dryPrice\":" + p.getDryPrice() + ",\"foldPrice\":" + p.getFoldPrice() + "}}");
            }
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        
        if ("/pricing".equals(path)) {
            String json = readBody(req);
            try {
                Pricing p = new Pricing();
                p.setId(Integer.parseInt(extract(json, "id")));
                p.setWashPrice(Double.parseDouble(extract(json, "washPrice")));
                p.setDryPrice(Double.parseDouble(extract(json, "dryPrice")));
                p.setFoldPrice(Double.parseDouble(extract(json, "foldPrice")));
                boolean success = service.updatePricing(p);
                out.write("{\"success\":" + success + ",\"message\":\"" + (success ? "Updated" : "Failed") + "\"}");
            } catch (Exception e) {
                out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = req.getReader()) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
    
    private String extract(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        }
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(start, end).trim();
    }
}