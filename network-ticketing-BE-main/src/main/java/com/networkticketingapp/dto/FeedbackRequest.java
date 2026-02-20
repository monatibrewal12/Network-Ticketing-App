package com.networkticketingapp.dto;

import lombok.Data;

@Data
public class FeedbackRequest {

    private Long ticketId;
    private Long customerId;
    private int rating;      // 1–5
    private String comment;
}
