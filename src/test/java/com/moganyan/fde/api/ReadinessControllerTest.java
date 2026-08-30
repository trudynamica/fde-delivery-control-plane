package com.moganyan.fde.api;

import com.moganyan.fde.service.DeploymentReadinessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReadinessController.class)
class ReadinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeploymentReadinessService service;

    @Test
    void returnsProblemDetailsForInvalidAssessment() throws Exception {
        mockMvc.perform(post("/api/readiness/evaluate")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("The readiness assessment failed validation."))
                .andExpect(jsonPath("$.errors").isArray());
    }
}
