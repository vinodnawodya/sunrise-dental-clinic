package com.sunrisedental.controller;

import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final AppointmentRepository appointmentRepository;
    private final BillingService billingService;

    @GetMapping("/daily")
    public String dailyAppointments(Model model) {
        model.addAttribute("appointments", appointmentRepository.findTodaysAppointments());
        return "report-daily";
    }

    @GetMapping("/revenue")
    public String revenueByTreatment(Model model) {
        model.addAttribute("revenueRows", billingService.revenueByTreatment());
        return "report-revenue";
    }
}
