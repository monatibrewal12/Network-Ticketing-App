package com.networkticketingapp.dto;

import lombok.Data;

@Data
public class ResetEmailRequest {
    private String oldEmail;
    private String newEmail;
}
