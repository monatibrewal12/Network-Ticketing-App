package com.networkticketingapp.service;

import com.networkticketingapp.dto.DashboardSummaryDTO;
import com.networkticketingapp.entity.TicketStatus;
import com.networkticketingapp.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;

    public DashboardSummaryDTO getDashboardSummary() {

        return new DashboardSummaryDTO(
                ticketRepository.count(),
                ticketRepository.countByStatus(TicketStatus.OPEN),
                ticketRepository.countByStatus(TicketStatus.IN_PROGRESS),
                ticketRepository.countByStatus(TicketStatus.RESOLVED)
        );
    }
}
