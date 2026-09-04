package com.sunrisedental.controller;

import com.sunrisedental.dto.AppointmentFormDto;
import com.sunrisedental.repository.DentistRepository;
import com.sunrisedental.repository.TreatmentRepository;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.DentistDoubleBookingException;
import com.sunrisedental.service.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;

    @GetMapping("/new")
    public String newAppointmentForm(Model model) {
        model.addAttribute("appointmentForm", new AppointmentFormDto());
        addFormReferenceData(model);
        return "appointment-form";
    }

    @PostMapping
    public String createAppointment(@Valid @ModelAttribute("appointmentForm") AppointmentFormDto appointmentForm,
                                     BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            addFormReferenceData(model);
            return "appointment-form";
        }

        try {
            var appointment = appointmentService.createAppointment(
                    appointmentForm.getPatientName(),
                    appointmentForm.getPatientAddress(),
                    appointmentForm.getPatientContactNumber(),
                    appointmentForm.getDentistId(),
                    appointmentForm.getTreatmentId(),
                    appointmentForm.getAppointmentDate(),
                    appointmentForm.getAppointmentTime());

            return "redirect:/appointments/search?number=" + appointment.getAppointmentNumber();
        } catch (DentistDoubleBookingException ex) {
            bindingResult.reject("dentistDoubleBooked", ex.getMessage());
            addFormReferenceData(model);
            return "appointment-form";
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "number", required = false) String number, Model model) {
        model.addAttribute("appointmentNumber", number);
        if (number != null && !number.isBlank()) {
            try {
                model.addAttribute("appointment", appointmentService.findByAppointmentNumber(number));
            } catch (ResourceNotFoundException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        return "appointment-search";
    }

    private void addFormReferenceData(Model model) {
        model.addAttribute("dentists", dentistRepository.findAll());
        model.addAttribute("treatments", treatmentRepository.findAll());
    }
}
