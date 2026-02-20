package com.networkticketingapp.service;

import com.networkticketingapp.entity.Feedback;

import java.util.Map;

public interface FeedbackService {

    Feedback submitFeedback(
            Long ticketId,
            Long customerId,
            int rating,
            String comment
    );

    Feedback getFeedbackByTicket(Long ticketId);
    Map<String, Long> getFeedbackSummary();

}
