package com.github.guilhermerblc.personalfinanceapi.controller;

import com.github.guilhermerblc.personalfinanceapi.dto.DashboardResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary/{userId}")
    public ResponseEntity<DashboardResponseDTO> summary(@PathVariable Long userId) {
        DashboardResponseDTO dashboardResponseDTO = dashboardService.summary(userId);
        return ResponseEntity.ok(dashboardResponseDTO);
    }
}
