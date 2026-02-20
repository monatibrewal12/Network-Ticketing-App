package com.networkticketingapp.service.impl;

import com.networkticketingapp.entity.*;
import com.networkticketingapp.repository.*;
import com.networkticketingapp.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final SLARepository slaRepository;
    private final TicketActionLogRepository ticketActionLogRepository;
    private final IssueCategoryRepository issueCategoryRepository;
    private final FeedbackRepository feedbackRepository;


    /* ================= CREATE TICKET ================= */
    @Override
    public Ticket createTicket(Long customerId, String description, String issueCategoryName) {

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        IssueCategory issueCategory = issueCategoryRepository.findByName(issueCategoryName)
                .orElseThrow(() -> new RuntimeException("Invalid issue category"));

        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .description(description)
                .status(TicketStatus.OPEN)
                .customer(customer)
                .issueCategory(issueCategory)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Ticket saved = ticketRepository.save(ticket);
        // 2️⃣ generate business ticket id
        String ticketId = String.format("T-%03d", saved.getId());
        saved.setTicketId(ticketId);

        ticketRepository.save(saved);

        ticketActionLogRepository.save(
                TicketActionLog.builder()
                        .ticket(saved)
                        .performedBy(customer)
                        .action("TICKET_CREATED")
                        .actionTime(now)
                        .build()
        );

        return saved;
    }

    /* ================= UPDATE PRIORITY & SEVERITY ================= */
    @Override
    public Ticket updatePrioritySeverity(Long ticketId, String priority, String severity, Long userId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User agent = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean updated = false;

        if (priority != null && !priority.isBlank() && (ticket.getPriority() == null || ticket.getPriority().isBlank())) {
            ticket.setPriority(priority);
            updated = true;
        }

        if (severity != null && !severity.isBlank() && (ticket.getSeverity() == null || !ticket.getSeverity().isBlank())) {
            ticket.setSeverity(severity);
            updated = true;
        }

        if (!updated) {
            throw new RuntimeException("Nothing to update");
        }

        // SET SLA ONLY WHEN BOTH ARE PRESENT
        if (ticket.getPriority() != null && ticket.getSeverity() != null && !ticket.getSeverity().isBlank()) {

            SLA sla = slaRepository.findByPriorityAndSeverity(
                    ticket.getPriority(),
                    ticket.getSeverity()
            );

            if (sla == null) {
                throw new RuntimeException(
                        "SLA not configured for Priority=" + ticket.getPriority()
                                + " Severity=" + ticket.getSeverity()
                );
            }

            ticket.setSlaDueTime(LocalDateTime.now().plusHours(sla.getResolutionHours()));
            ticket.setSlaBreached(false);

            // MOVE TO IN_PROGRESS ONLY IF ENGINEER EXISTS
            if (ticket.getStatus() == TicketStatus.OPEN &&
                    ticket.getAssignedEngineer() != null &&
                ticket.getSeverity() != null &&
                    !ticket.getSeverity().isBlank()){
                ticket.setStatus(TicketStatus.IN_PROGRESS);
            }
        }

        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updatedTicket = ticketRepository.save(ticket);

        ticketActionLogRepository.save(
                TicketActionLog.builder()
                        .ticket(updatedTicket)
                        .performedBy(agent)
                        .action("PRIORITY_SEVERITY_UPDATED")
                        .actionTime(LocalDateTime.now())
                        .build()
        );

        return updatedTicket;
    }

    /* ================= ASSIGN ENGINEER ================= */
    @Override
    public Ticket assignEngineer(Long ticketId, Long engineerId, Long performedByUserId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() -> new RuntimeException("Engineer not found"));

        if (engineer.getRole() != Role.ENGINEER) {
            throw new RuntimeException("User is not an engineer");
        }

        User performedBy = userRepository.findById(performedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ticket.setAssignedEngineer(engineer);
        ticket.setAssignedAt(LocalDateTime.now());

        // MOVE TO IN_PROGRESS ONLY IF PRIORITY + SEVERITY EXIST
        if (ticket.getStatus() == TicketStatus.OPEN &&
                ticket.getPriority() != null &&
                ticket.getSeverity() != null) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updated = ticketRepository.save(ticket);

        ticketActionLogRepository.save(
                TicketActionLog.builder()
                        .ticket(updated)
                        .performedBy(performedBy)
                        .action("ENGINEER_ASSIGNED")
                        .actionTime(LocalDateTime.now())
                        .build()
        );

        return updated;
    }

    /* ================= UPDATE STATUS ================= */
    @Override
    public Ticket updateStatus(Long ticketId, String status, Long userId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TicketStatus newStatus = TicketStatus.valueOf(status);

        // FINAL SAFETY LOCK
        if (newStatus == TicketStatus.IN_PROGRESS) {
            if (ticket.getAssignedEngineer() == null ||
                    ticket.getPriority() == null ||
                    ticket.getSeverity() == null) {
                throw new RuntimeException(
                        "Cannot move to IN_PROGRESS until Engineer, Priority and Severity are set"
                );
            }
        }

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        if (newStatus == TicketStatus.CLOSED) {
            boolean feedbackExists = feedbackRepository.existsByTicket_Id(ticketId);
            if (!feedbackExists) {
                throw new RuntimeException("Cannot close ticket before customer feedback");
            }
            ticket.setClosedAt(LocalDateTime.now());

        }

        if (newStatus == TicketStatus.RESOLVED && ticket.getSlaDueTime() != null) {
            ticket.setSlaBreached(LocalDateTime.now().isAfter(ticket.getSlaDueTime()));
            ticket.setResolvedAt(LocalDateTime.now());
        }

        Ticket updated = ticketRepository.save(ticket);

        ticketActionLogRepository.save(
                TicketActionLog.builder()
                        .ticket(updated)
                        .performedBy(user)
                        .action("STATUS_UPDATED_TO_" + status)
                        .actionTime(LocalDateTime.now())
                        .build()
        );

        return updated;
    }

    /* ================= REOPEN TICKET ================= */
    @Override
    public Ticket reopenTicket(Long ticketId, Long performedByUserId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User user = userRepository.findById(performedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (ticket.getStatus() != TicketStatus.RESOLVED &&
                ticket.getStatus() != TicketStatus.CLOSED) {
            throw new RuntimeException("Only resolved or closed tickets can be reopened");
        }

        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAssignedEngineer(null);
        ticket.setSlaDueTime(null);
        ticket.setSlaBreached(false);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updated = ticketRepository.save(ticket);

        ticketActionLogRepository.save(
                TicketActionLog.builder()
                        .ticket(updated)
                        .performedBy(user)
                        .action("TICKET_REOPENED")
                        .actionTime(LocalDateTime.now())
                        .build()
        );

        return updated;
    }

    /* ================= QUERIES ================= */
    @Override
    public List<Ticket> getTicketsByCustomer(Long customerId) {
        return ticketRepository.findByCustomer_Id(customerId);
    }

    @Override
    public List<Ticket> getTicketsByEngineer(Long engineerId) {
        return ticketRepository.findByAssignedEngineer_Id(engineerId);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(TicketStatus.valueOf(status));
    }

    @Override
    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    @Override
    public List<TicketActionLog> getTicketHistory(Long ticketId) {
        return ticketActionLogRepository.findByTicketIdOrderByActionTimeAsc(ticketId);
    }

    /* ================= DASHBOARD COUNTS ================= */
    @Override
    public Map<String, Long> getTicketCounts() {

        List<Ticket> tickets = ticketRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        Map<String, Long> counts = new HashMap<>();
        counts.put("TOTAL", (long) tickets.size());
        counts.put("OPEN", tickets.stream().filter(t -> t.getStatus() == TicketStatus.OPEN).count());
        counts.put("IN_PROGRESS", tickets.stream().filter(t -> t.getStatus() == TicketStatus.IN_PROGRESS).count());
        counts.put("RESOLVED", tickets.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED).count());
        counts.put("CLOSED", tickets.stream().filter(t -> t.getStatus() == TicketStatus.CLOSED).count());

        counts.put("SLA_ACTIVE", tickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.IN_PROGRESS)
                .filter(t -> t.getSlaDueTime() != null)
                .filter(t -> now.isBefore(t.getSlaDueTime()))
                .count());

        counts.put("SLA_BREACHED", tickets.stream()
                .filter(t -> t.getSlaDueTime() != null)
                .filter(t -> now.isAfter(t.getSlaDueTime()))
                .count());

        return counts;
    }
}
