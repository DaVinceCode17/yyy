package com.resources.dao;

import com.resources.model.Order;
import com.resources.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    public Order save(Order order) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, services, service_type, status, queue_number, weight, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setString(2, order.getServices());
            pstmt.setString(3, order.getServiceType());
            pstmt.setString(4, order.getStatus());
            pstmt.setInt(5, order.getQueueNumber());
            pstmt.setDouble(6, order.getWeight());
            pstmt.setDouble(7, order.getPrice());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) order.setId(rs.getInt(1));
                return order;
            }
            return null;
        }
    }
    
    public int getNextQueueNumber() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders WHERE status IN ('pending', 'ongoing')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1) + 1;
            return 1;
        }
    }
    
    public List<Order> findByCustomerId(int customerId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.first_name, c.last_name FROM orders o LEFT JOIN customers c ON o.customer_id = c.id WHERE o.customer_id = ? ORDER BY o.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    
    public List<Order> findByStatus(String status) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.first_name, c.last_name FROM orders o LEFT JOIN customers c ON o.customer_id = c.id WHERE o.status = ? ORDER BY o.queue_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    
    public List<Order> findAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.first_name, c.last_name FROM orders o LEFT JOIN customers c ON o.customer_id = c.id ORDER BY o.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    
    public Order findById(int id) throws SQLException {
        String sql = "SELECT o.*, c.first_name, c.last_name FROM orders o LEFT JOIN customers c ON o.customer_id = c.id WHERE o.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }
    
    public Order updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
            return findById(orderId);
        }
    }
    
    public Order updateOrder(int orderId, String status, double weight, double price) throws SQLException {
        String sql = "UPDATE orders SET status = ?, weight = ?, price = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setDouble(2, weight);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, orderId);
            pstmt.executeUpdate();
            return findById(orderId);
        }
    }
    
    public boolean delete(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCustomerId(rs.getInt("customer_id"));
        String fn = rs.getString("first_name");
        String ln = rs.getString("last_name");
        if (fn != null && ln != null) o.setCustomerName(fn + " " + ln);
        o.setServices(rs.getString("services"));
        o.setServiceType(rs.getString("service_type"));
        o.setStatus(rs.getString("status"));
        o.setQueueNumber(rs.getInt("queue_number"));
        o.setWeight(rs.getDouble("weight"));
        o.setPrice(rs.getDouble("price"));
        o.setCreatedAt(rs.getString("created_at"));
        o.setUpdatedAt(rs.getString("updated_at"));
        return o;
    }
}