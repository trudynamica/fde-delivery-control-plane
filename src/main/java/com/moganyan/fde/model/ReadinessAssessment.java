package com.moganyan.fde.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReadinessAssessment(
        @NotBlank String deploymentId,
        @NotEmpty List<@Valid DiscoveryEvidence> discoveryEvidence,
        @NotNull List<@Valid OperationalConstraint> constraints,
        @NotNull @Valid AiQualityEvaluation evaluation,
        @NotBlank String businessOwner,
        @NotBlank String technicalOwner,
        @NotBlank String rollbackPlan) {
}
