package com.networkticketingapp.dto;

public class TicketReopenRequest {

    private Long performedByUserId;

    public Long getPerformedByUserId() {
        return performedByUserId;
    }

    public void setPerformedByUserId(Long performedByUserId) {
        this.performedByUserId = performedByUserId;
    }
}
