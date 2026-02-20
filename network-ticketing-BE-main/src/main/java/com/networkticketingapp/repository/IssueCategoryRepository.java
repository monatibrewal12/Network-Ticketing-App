package com.networkticketingapp.repository;

import com.networkticketingapp.entity.IssueCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssueCategoryRepository extends JpaRepository<IssueCategory, Long> {
    Optional<IssueCategory> findByName(String name);
}
