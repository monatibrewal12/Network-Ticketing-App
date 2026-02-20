package com.networkticketingapp.repository;

import com.networkticketingapp.entity.Ticket;
import com.networkticketingapp.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countByStatus(TicketStatus status);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByIssueCategory_Name(String name);

    List<Ticket> findByAssignedEngineer_Id(Long engineerId);

    List<Ticket> findByCustomer_Id(Long customerId);

    long countBySlaBreachedTrue();
    long countBySlaBreachedFalse();

}