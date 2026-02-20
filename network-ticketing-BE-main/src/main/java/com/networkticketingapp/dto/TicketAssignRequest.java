package com.networkticketingapp.dto;

import lombok.Data;

@Data
public class TicketAssignRequest {
    private Long engineerId;
    private Long performedByUserId;
}
