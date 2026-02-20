package com.networkticketingapp.dto;

import lombok.Data;

@Data
public class PrioritySeverityUpdateRequest {
    private String priority;
    private String severity;
    private Long performedByUserId;
}
