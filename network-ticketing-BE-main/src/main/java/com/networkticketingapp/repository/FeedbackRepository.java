package com.networkticketingapp.repository;

import com.networkticketingapp.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByTicket_Id(Long ticketId);

    boolean existsByTicket_Id(Long ticketId);

    long count();
    long countByRating(int rating);

}
