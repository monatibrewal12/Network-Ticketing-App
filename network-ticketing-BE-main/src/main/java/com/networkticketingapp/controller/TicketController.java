package com.networkticketingapp.controller;
import com.networkticketingapp.dto.*;
import com.networkticketingapp.entity.Ticket;
import com.networkticketingapp.entity.TicketActionLog;
import com.networkticketingapp.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // ================= CUSTOMER =================

    @PostMapping("/create")
    public Ticket createTicket(@RequestBody TicketCreateRequest request) {
        return ticketService.createTicket(
                request.getCustomerId(),
                request.getDescription(),
                request.getIssueCategory()
        );
    }

    @GetMapping("/customer/{customerId}")
    public List<Ticket> getCustomerTickets(@PathVariable Long customerId) {
        return ticketService.getTicketsByCustomer(customerId);
    }

    // ✅ REOPEN TICKET (ONLY ONE METHOD – FIXED)
    @PutMapping("/{ticketId}/reopen")
    public Ticket reopenTicket(
            @PathVariable Long ticketId,
            @RequestBody TicketReopenRequest request) {

        return ticketService.reopenTicket(
                ticketId,
                request.getPerformedByUserId()
        );
    }

    // ================= AGENT / ADMIN =================

    @PutMapping("/{ticketId}/priority-severity")
    public Ticket updatePrioritySeverity(
            @PathVariable Long ticketId,
            @RequestBody PrioritySeverityUpdateRequest request) {

        return ticketService.updatePrioritySeverity(
                ticketId,
                request.getPriority(),
                request.getSeverity(),
                request.getPerformedByUserId()
        );
    }

    @PutMapping("/{ticketId}/assign")
    public Ticket assignEngineer(
            @PathVariable Long ticketId,
            @RequestBody AssignEngineerRequest request) {

        return ticketService.assignEngineer(
                ticketId,
                request.getEngineerId(),
                request.getPerformedBy()
        );
    }

    // ================= ENGINEER =================

    @PutMapping("/{ticketId}/status")
    public Ticket updateStatus(
            @PathVariable Long ticketId,
            @RequestBody TicketStatusUpdateRequest request) {

        return ticketService.updateStatus(
                ticketId,
                request.getStatus(),
                request.getPerformedByUserId()
        );
    }

    // ================= COMMON =================

    @GetMapping("/{ticketId}/history")
    public List<TicketActionLog> getTicketHistory(@PathVariable Long ticketId) {
        return ticketService.getTicketHistory(ticketId);
    }

    @GetMapping("/{ticketId}")
    public Ticket getTicket(@PathVariable Long ticketId) {
        return ticketService.getTicketById(ticketId);
    }



    @GetMapping("/filter/engineer/{engineerId}")
    public List<Ticket> filterByEngineer(@PathVariable Long engineerId) {
        return ticketService.getTicketsByEngineer(engineerId);
    }


    // ================= ADMIN =================

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/filter/status/{status}")
    public List<Ticket> filterByStatus(@PathVariable String status) {
        return ticketService.getTicketsByStatus(status);
    }


    @GetMapping("/report/counts")
    public Map<String, Long> getTicketCounts() {
        return ticketService.getTicketCounts();
    }
}