package com.networkticketingapp.controller;

import com.networkticketingapp.dto.FeedbackRequest;
import com.networkticketingapp.entity.Feedback;
import com.networkticketingapp.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // ✅ Submit feedback (CUSTOMER)
    @PostMapping("/submit")
    public Feedback submitFeedback(@RequestBody FeedbackRequest request) {

        if (request.getCustomerId() == null || request.getCustomerId() <= 0) {
            throw new RuntimeException("Invalid customer id");
        }

        return feedbackService.submitFeedback(
                request.getTicketId(),
                request.getCustomerId(),
                request.getRating(),
                request.getComment()
        );
    }

    // ✅ View feedback (ADMIN)
    @GetMapping("/ticket/{ticketId}")
    public Feedback getFeedbackByTicket(@PathVariable Long ticketId) {
        return feedbackService.getFeedbackByTicket(ticketId);
    }
}
