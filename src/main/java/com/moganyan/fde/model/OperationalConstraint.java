package com.moganyan.fde.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OperationalConstraint(
        @NotBlank String id,
        @NotNull RiskLevel risk,
        @NotBlank String description,
        boolean resolved) {
}
