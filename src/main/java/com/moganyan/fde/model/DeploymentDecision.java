package com.moganyan.fde.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record DeploymentDecision(
        String deploymentId,
        String decision,
        Instant evaluatedAt,
        Map<String, Boolean> gates,
        List<String> blockers) {
}
