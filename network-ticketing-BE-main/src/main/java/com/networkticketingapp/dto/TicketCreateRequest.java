package com.networkticketingapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketCreateRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Issue category is required")
    private String issueCategory;

    @NotBlank(message = "Priority is required")
    private String priority;

    @NotBlank(message = "Severity is required")
    private String severity;
}
