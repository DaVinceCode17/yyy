package com.resources.service;

import com.resources.dao.CustomerDAO;
import com.resources.dao.OrderDAO;
import com.resources.dao.PricingDAO;
import com.resources.model.Customer;
import com.resources.model.Order;
import com.resources.model.Pricing;
import java.sql.SQLException;
import java.util.List;

public class LaundryService {
    
    private CustomerDAO customerDAO;
    private OrderDAO orderDAO;
    private PricingDAO pricingDAO;
    
    public LaundryService() {
        customerDAO = new CustomerDAO();
        orderDAO = new OrderDAO();
        pricingDAO = new PricingDAO();
    }
    
    // Auth
    public Customer login(String contact, String password) throws SQLException {
        return customerDAO.findByContactAndPassword(contact, password);
    }
    
    public boolean register(Customer customer) throws SQLException {
        if (customerDAO.findByContact(customer.getContact()) != null) {
            throw new IllegalArgumentException("Contact already registered");
        }
        return customerDAO.save(customer);
    }
    
    // Customer
    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.findAll();
    }
    
    public Customer getCustomerById(int id) throws SQLException {
        return customerDAO.findById(id);
    }
    
    // Order
    public Order createOrder(Order order) throws SQLException {
        order.setQueueNumber(orderDAO.getNextQueueNumber());
        order.setStatus("pending");
        return orderDAO.save(order);
    }
    
    public List<Order> getOrdersByCustomer(int customerId) throws SQLException {
        return orderDAO.findByCustomerId(customerId);
    }
    
    public List<Order> getOrdersByStatus(String status) throws SQLException {
        return orderDAO.findByStatus(status);
    }
    
    public List<Order> getAllOrders() throws SQLException {
        return orderDAO.findAll();
    }
    
    public Order getOrderById(int id) throws SQLException {
        return orderDAO.findById(id);
    }
    
    public Order updateOrderStatus(int orderId, String status) throws SQLException {
        return orderDAO.updateStatus(orderId, status);
    }
    
    public Order updateOrder(int orderId, String status, double weight, double price) throws SQLException {
        return orderDAO.updateOrder(orderId, status, weight, price);
    }
    
    public boolean deleteOrder(int orderId) throws SQLException {
        return orderDAO.delete(orderId);
    }
    
    // Pricing
    public Pricing getPricing() throws SQLException {
        Pricing p = pricingDAO.getPricing();
        if (p == null) {
            pricingDAO.insertDefaultPricing();
            p = pricingDAO.getPricing();
        }
        return p;
    }
    
    public boolean updatePricing(Pricing pricing) throws SQLException {
        return pricingDAO.updatePricing(pricing);
    }
}