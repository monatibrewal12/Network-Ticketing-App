package com.networkticketingapp.service;

import com.networkticketingapp.entity.Ticket;
import com.networkticketingapp.entity.TicketActionLog;

import java.util.List;
import java.util.Map;

public interface TicketService {

    // CUSTOMER
    Ticket createTicket(Long customerId, String description, String issueCategoryName);
    List<Ticket> getTicketsByCustomer(Long customerId);

    // AGENT / ADMIN
    Ticket updatePrioritySeverity(Long ticketId, String priority, String severity, Long userId);
    Ticket assignEngineer(Long ticketId, Long engineerId, Long performedByUserId);

    // ENGINEER / AGENT
    Ticket updateStatus(Long ticketId, String status, Long userId);

    // CUSTOMER
    Ticket reopenTicket(Long ticketId, Long performedByUserId);

    // COMMON
    List<Ticket> getAllTickets();
    List<Ticket> getTicketsByStatus(String status);
    List<Ticket> getTicketsByEngineer(Long engineerId);
    Ticket getTicketById(Long ticketId);
    List<TicketActionLog> getTicketHistory(Long ticketId);

    // DASHBOARD
    Map<String, Long> getTicketCounts();
}
