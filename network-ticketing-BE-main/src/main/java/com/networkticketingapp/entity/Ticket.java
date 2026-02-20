package com.networkticketingapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private String priority;
    private String severity;

    // ✅ KEEP ONLY ONE createdAt & updatedAt
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime assignedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;


    // ✅ SLA FIELDS
    private LocalDateTime slaDueTime;
    private boolean slaBreached;

    @ManyToOne
    private User customer;

    @Column(name = "ticket_id", unique = true)
    private String ticketId;


    @ManyToOne
    private User assignedEngineer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issue_category_id")
    private IssueCategory issueCategory;
}
