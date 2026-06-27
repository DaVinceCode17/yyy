package com.resources.controller;

import com.resources.model.Customer;
import com.resources.model.Order;
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
import java.util.List;

@WebServlet("/api/laundry/*")
public class LaundryController extends HttpServlet {
    
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
            if ("/customers".equals(path)) {
                List<Customer> customers = service.getAllCustomers();
                out.write("{\"success\":true,\"customers\":[");
                for (int i = 0; i < customers.size(); i++) {
                    Customer c = customers.get(i);
                    out.write("{\"id\":" + c.getId() + ",\"firstName\":\"" + c.getFirstName() + "\",\"lastName\":\"" + c.getLastName() + "\",\"contact\":\"" + c.getContact() + "\",\"role\":\"" + c.getRole() + "\"}");
                    if (i < customers.size() - 1) out.write(",");
                }
                out.write("]}");
            } else if (path != null && path.startsWith("/customer/")) {
                int id = Integer.parseInt(path.substring(10));
                Customer c = service.getCustomerById(id);
                if (c != null) {
                    out.write("{\"success\":true,\"customer\":{\"id\":" + c.getId() + ",\"firstName\":\"" + c.getFirstName() + "\",\"lastName\":\"" + c.getLastName() + "\",\"contact\":\"" + c.getContact() + "\",\"role\":\"" + c.getRole() + "\"}}");
                } else {
                    out.write("{\"success\":false,\"message\":\"Not found\"}");
                }
            } else if ("/orders".equals(path)) {
                List<Order> orders = service.getAllOrders();
                out.write("{\"success\":true,\"orders\":[");
                for (int i = 0; i < orders.size(); i++) {
                    Order o = orders.get(i);
                    out.write("{\"id\":" + o.getId() + ",\"customerName\":\"" + o.getCustomerName() + "\",\"services\":\"" + o.getServices() + "\",\"status\":\"" + o.getStatus() + "\",\"queueNumber\":" + o.getQueueNumber() + "}");
                    if (i < orders.size() - 1) out.write(",");
                }
                out.write("]}");
            } else if (path != null && path.startsWith("/order/")) {
                int id = Integer.parseInt(path.substring(7));
                Order o = service.getOrderById(id);
                if (o != null) {
                    out.write("{\"success\":true,\"order\":{\"id\":" + o.getId() + ",\"customerId\":" + o.getCustomerId() + ",\"services\":\"" + o.getServices() + "\",\"status\":\"" + o.getStatus() + "\",\"queueNumber\":" + o.getQueueNumber() + "}}");
                } else {
                    out.write("{\"success\":false,\"message\":\"Not found\"}");
                }
            } else if (path != null && path.startsWith("/orders/status/")) {
                String status = path.substring(15);
                List<Order> orders = service.getOrdersByStatus(status);
                out.write("{\"success\":true,\"orders\":[");
                for (int i = 0; i < orders.size(); i++) {
                    Order o = orders.get(i);
                    out.write("{\"id\":" + o.getId() + ",\"customerName\":\"" + o.getCustomerName() + "\",\"services\":\"" + o.getServices() + "\",\"queueNumber\":" + o.getQueueNumber() + "}");
                    if (i < orders.size() - 1) out.write(",");
                }
                out.write("]}");
            } else if (path != null && path.startsWith("/orders/customer/")) {
                int id = Integer.parseInt(path.substring(17));
                List<Order> orders = service.getOrdersByCustomer(id);
                out.write("{\"success\":true,\"orders\":[");
                for (int i = 0; i < orders.size(); i++) {
                    Order o = orders.get(i);
                    out.write("{\"id\":" + o.getId() + ",\"services\":\"" + o.getServices() + "\",\"status\":\"" + o.getStatus() + "\",\"queueNumber\":" + o.getQueueNumber() + "}");
                    if (i < orders.size() - 1) out.write(",");
                }
                out.write("]}");
            } else if ("/pricing".equals(path)) {
                Pricing p = service.getPricing();
                out.write("{\"success\":true,\"pricing\":{\"id\":" + p.getId() + ",\"washPrice\":" + p.getWashPrice() + ",\"dryPrice\":" + p.getDryPrice() + ",\"foldPrice\":" + p.getFoldPrice() + "}}");
            } else if ("/dashboard".equals(path)) {
                List<Order> pending = service.getOrdersByStatus("pending");
                List<Order> ongoing = service.getOrdersByStatus("ongoing");
                List<Order> completed = service.getOrdersByStatus("completed");
                List<Customer> customers = service.getAllCustomers();
                out.write("{\"success\":true,\"dashboard\":{\"pendingCount\":" + pending.size() + ",\"ongoingCount\":" + ongoing.size() + ",\"completedCount\":" + completed.size() + ",\"totalCustomers\":" + customers.size() + "}}");
            }
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String json = readBody(req);
        
        try {
            if ("/order/create".equals(path)) {
                int customerId = Integer.parseInt(extract(json, "customerId"));
                String services = extract(json, "services");
                String serviceType = extract(json, "serviceType");
                Order o = new Order(customerId, services, serviceType);
                Order created = service.createOrder(o);
                out.write("{\"success\":true,\"order\":{\"id\":" + created.getId() + ",\"queueNumber\":" + created.getQueueNumber() + ",\"status\":\"" + created.getStatus() + "\"}}");
            } else if ("/pricing/update".equals(path)) {
                Pricing p = new Pricing();
                p.setId(Integer.parseInt(extract(json, "id")));
                p.setWashPrice(Double.parseDouble(extract(json, "washPrice")));
                p.setDryPrice(Double.parseDouble(extract(json, "dryPrice")));
                p.setFoldPrice(Double.parseDouble(extract(json, "foldPrice")));
                boolean success = service.updatePricing(p);
                out.write("{\"success\":" + success + ",\"message\":\"" + (success ? "Updated" : "Failed") + "\"}");
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
        String json = readBody(req);
        
        try {
            if (path != null && path.startsWith("/order/status/")) {
                String[] parts = path.split("/");
                int orderId = Integer.parseInt(parts[3]);
                String status = extract(json, "status");
                Order o = service.updateOrderStatus(orderId, status);
                out.write("{\"success\":true,\"order\":{\"id\":" + o.getId() + ",\"status\":\"" + o.getStatus() + "\"}}");
            } else if ("/order/update".equals(path)) {
                int orderId = Integer.parseInt(extract(json, "id"));
                String status = extract(json, "status");
                double weight = Double.parseDouble(extract(json, "weight"));
                double price = Double.parseDouble(extract(json, "price"));
                Order o = service.updateOrder(orderId, status, weight, price);
                out.write("{\"success\":true,\"order\":{\"id\":" + o.getId() + ",\"status\":\"" + o.getStatus() + "\",\"weight\":" + o.getWeight() + ",\"price\":" + o.getPrice() + "}}");
            }
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        
        try {
            if (path != null && path.startsWith("/order/")) {
                int orderId = Integer.parseInt(path.substring(7));
                boolean success = service.deleteOrder(orderId);
                out.write("{\"success\":" + success + ",\"message\":\"" + (success ? "Deleted" : "Failed") + "\"}");
            }
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
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