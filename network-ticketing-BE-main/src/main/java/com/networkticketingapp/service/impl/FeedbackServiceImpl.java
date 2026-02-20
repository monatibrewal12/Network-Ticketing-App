package com.networkticketingapp.service.impl;

import com.networkticketingapp.entity.Feedback;
import com.networkticketingapp.entity.Ticket;
import com.networkticketingapp.entity.User;
import com.networkticketingapp.repository.FeedbackRepository;
import com.networkticketingapp.repository.TicketRepository;
import com.networkticketingapp.repository.UserRepository;
import com.networkticketingapp.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    public Feedback submitFeedback(
            Long ticketId,
            Long customerId,
            int rating,
            String comment
    ) {

        // ✅ prevent duplicate feedback
        if (feedbackRepository.existsByTicket_Id(ticketId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feedback already submitted for this ticket"
            );
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Ticket not found"
                        )
                );

        User customer = userRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Customer not found"
                        )
                );

        Feedback feedback = Feedback.builder()
                .ticket(ticket)
                .customer(customer)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        return feedbackRepository.save(feedback); // ✅ THIS WAS MISSING
    }

    @Override
    public Feedback getFeedbackByTicket(Long ticketId) {
        return feedbackRepository
                .findByTicket_Id(ticketId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Feedback not submitted yet"
                        )
                );
    }

    @Override
    public Map<String, Long> getFeedbackSummary() {
        Map<String, Long> summary = new HashMap<>();
        summary.put("TOTAL", feedbackRepository.count());
        summary.put("RATING_5", feedbackRepository.countByRating(5));
        summary.put("RATING_4", feedbackRepository.countByRating(4));
        summary.put("RATING_3", feedbackRepository.countByRating(3));
        summary.put("RATING_2", feedbackRepository.countByRating(2));
        summary.put("RATING_1", feedbackRepository.countByRating(1));
        return summary;
    }
}
