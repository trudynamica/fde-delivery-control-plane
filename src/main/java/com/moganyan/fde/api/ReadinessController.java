package com.moganyan.fde.api;

import com.moganyan.fde.model.DeploymentDecision;
import com.moganyan.fde.model.ReadinessAssessment;
import com.moganyan.fde.service.DeploymentReadinessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readiness")
public class ReadinessController {

    private final DeploymentReadinessService service;

    public ReadinessController(DeploymentReadinessService service) {
        this.service = service;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<DeploymentDecision> evaluate(
            @Valid @RequestBody ReadinessAssessment assessment) {
        return ResponseEntity.status(HttpStatus.OK).body(service.evaluate(assessment));
    }
}
