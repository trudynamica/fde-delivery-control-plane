package com.moganyan.fde.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiscoveryEvidence(
        @NotNull EvidenceType type,
        @NotBlank String source,
        @NotBlank String finding) {
}
