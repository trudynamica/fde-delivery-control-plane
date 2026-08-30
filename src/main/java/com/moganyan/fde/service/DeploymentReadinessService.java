package com.moganyan.fde.service;

import com.moganyan.fde.model.DeploymentDecision;
import com.moganyan.fde.model.DiscoveryEvidence;
import com.moganyan.fde.model.EvidenceType;
import com.moganyan.fde.model.OperationalConstraint;
import com.moganyan.fde.model.ReadinessAssessment;
import com.moganyan.fde.model.RiskLevel;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeploymentReadinessService {

    private static final double MINIMUM_RETRIEVAL_RECALL = 0.80;
    private static final double MINIMUM_GROUNDED_COVERAGE = 0.95;
    private static final double MAXIMUM_ERROR_RATE = 0.01;
    private static final long MAXIMUM_P95_LATENCY_MS = 2_000;

    private final Clock clock;

    public DeploymentReadinessService() {
        this(Clock.systemUTC());
    }

    DeploymentReadinessService(Clock clock) {
        this.clock = clock;
    }

    public DeploymentDecision evaluate(ReadinessAssessment assessment) {
        Map<String, Boolean> gates = new LinkedHashMap<>();
        gates.put("discoveryCoverage", hasCompleteDiscovery(assessment.discoveryEvidence()));
        gates.put("criticalConstraintsResolved", criticalConstraintsResolved(assessment.constraints()));
        gates.put("retrievalRecall", assessment.evaluation().retrievalRecall() >= MINIMUM_RETRIEVAL_RECALL);
        gates.put("groundedClaimCoverage",
                assessment.evaluation().groundedClaimCoverage() >= MINIMUM_GROUNDED_COVERAGE);
        gates.put("errorRate", assessment.evaluation().errorRate() <= MAXIMUM_ERROR_RATE);
        gates.put("p95Latency", assessment.evaluation().p95LatencyMs() <= MAXIMUM_P95_LATENCY_MS);
        gates.put("accountableOwners", hasText(assessment.businessOwner()) && hasText(assessment.technicalOwner()));
        gates.put("rollbackPlan", hasText(assessment.rollbackPlan()));

        List<String> blockers = gates.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .toList();
        return new DeploymentDecision(
                assessment.deploymentId(),
                blockers.isEmpty() ? "READY" : "HOLD",
                Instant.now(clock),
                Map.copyOf(gates),
                blockers);
    }

    private boolean hasCompleteDiscovery(List<DiscoveryEvidence> evidence) {
        Set<EvidenceType> observed = evidence.stream()
                .map(DiscoveryEvidence::type)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(EvidenceType.class)));
        return observed.containsAll(EnumSet.allOf(EvidenceType.class));
    }

    private boolean criticalConstraintsResolved(List<OperationalConstraint> constraints) {
        return constraints.stream()
                .filter(constraint -> constraint.risk() == RiskLevel.CRITICAL)
                .allMatch(OperationalConstraint::resolved);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
