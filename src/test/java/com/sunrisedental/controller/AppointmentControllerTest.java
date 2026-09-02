package com.sunrisedental.controller;

import com.sunrisedental.config.SecurityConfig;
import com.sunrisedental.repository.DentistRepository;
import com.sunrisedental.repository.TreatmentRepository;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AppointmentController.class)
@Import(SecurityConfig.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;
    @MockBean
    private PatientService patientService;
    @MockBean
    private DentistRepository dentistRepository;
    @MockBean
    private TreatmentRepository treatmentRepository;

    @Test
    void newAppointmentForm_redirectsToLogin_whenNotAuthenticated() throws Exception {
        // A browser-style Accept header, so Spring Security's entry-point
        // negotiation picks the form-login redirect rather than the Basic
        // challenge it also offers to non-browser clients (see SecurityConfig).
        mockMvc.perform(get("/appointments/new").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "STAFF")
    void newAppointmentForm_rendersForm_whenAuthenticated() throws Exception {
        org.mockito.Mockito.when(patientService.findAll()).thenReturn(List.of());
        org.mockito.Mockito.when(dentistRepository.findAll()).thenReturn(List.of());
        org.mockito.Mockito.when(treatmentRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/appointments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-form"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "STAFF")
    void createAppointment_rerendersFormWithErrors_whenInvalid() throws Exception {
        org.mockito.Mockito.when(patientService.findAll()).thenReturn(List.of());
        org.mockito.Mockito.when(dentistRepository.findAll()).thenReturn(List.of());
        org.mockito.Mockito.when(treatmentRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(post("/appointments").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-form"));

        verify(appointmentService, never()).createAppointment(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }
}
