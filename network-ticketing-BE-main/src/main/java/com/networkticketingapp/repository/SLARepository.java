package com.networkticketingapp.repository;

import com.networkticketingapp.entity.SLA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SLARepository extends JpaRepository<SLA, Long> {
    SLA findByPriorityAndSeverity(String priority, String severity);
}
