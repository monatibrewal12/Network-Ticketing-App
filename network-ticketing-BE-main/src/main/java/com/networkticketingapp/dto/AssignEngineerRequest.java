package com.networkticketingapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignEngineerRequest {
    private Long engineerId;
    private Long performedBy; // userId
}
