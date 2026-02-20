package com.networkticketingapp.dto;

import lombok.Data;

@Data
public class TicketStatusUpdateRequest {
    private String status;
    private Long performedByUserId;
}
