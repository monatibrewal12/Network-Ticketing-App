package com.networkticketingapp.controller;

import com.networkticketingapp.dto.DashboardSummaryDTO;
import com.networkticketingapp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryDTO getSummary() {
        return dashboardService.getDashboardSummary();
    }
}
