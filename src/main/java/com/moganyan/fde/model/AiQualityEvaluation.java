package com.moganyan.fde.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;

public record AiQualityEvaluation(
        @DecimalMin("0.0") @DecimalMax("1.0") double retrievalRecall,
        @DecimalMin("0.0") @DecimalMax("1.0") double groundedClaimCoverage,
        @DecimalMin("0.0") @DecimalMax("1.0") double errorRate,
        @PositiveOrZero long p95LatencyMs) {
}
