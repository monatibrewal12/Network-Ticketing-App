package com.networkticketingapp.repository;

import com.networkticketingapp.entity.TicketActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketActionLogRepository extends JpaRepository<TicketActionLog, Long> {

    // ✅ REQUIRED FOR getTicketHistory
    List<TicketActionLog> findByTicketId(Long ticketId);

    List<TicketActionLog> findByTicketIdOrderByActionTimeAsc(Long ticketId);
}
