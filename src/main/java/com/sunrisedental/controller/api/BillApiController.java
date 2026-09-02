package com.sunrisedental.controller.api;

import com.sunrisedental.dto.BillResponseDto;
import com.sunrisedental.service.BillingService;
import com.sunrisedental.service.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillApiController {

    private final BillingService billingService;

    @GetMapping("/{appointmentNumber}")
    public BillResponseDto getByAppointmentNumber(@PathVariable String appointmentNumber) {
        try {
            return BillResponseDto.from(billingService.findByAppointmentNumber(appointmentNumber));
        } catch (ResourceNotFoundException ex) {
            return BillResponseDto.from(billingService.generateBill(appointmentNumber));
        }
    }
}
