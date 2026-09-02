package com.sunrisedental.controller;

import com.sunrisedental.entity.Bill;
import com.sunrisedental.service.BillingService;
import com.sunrisedental.service.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillingService billingService;

    @GetMapping("/{appointmentNumber}")
    public String viewBill(@PathVariable String appointmentNumber, Model model) {
        Bill bill;
        try {
            bill = billingService.findByAppointmentNumber(appointmentNumber);
        } catch (ResourceNotFoundException ex) {
            bill = billingService.generateBill(appointmentNumber);
        }
        model.addAttribute("bill", bill);
        return "bill";
    }
}
