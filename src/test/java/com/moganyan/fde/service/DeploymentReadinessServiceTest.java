package com.moganyan.fde.service;

import com.moganyan.fde.model.AiQualityEvaluation;
import com.moganyan.fde.model.DeploymentDecision;
import com.moganyan.fde.model.DiscoveryEvidence;
import com.moganyan.fde.model.EvidenceType;
import com.moganyan.fde.model.OperationalConstraint;
import com.moganyan.fde.model.ReadinessAssessment;
import com.moganyan.fde.model.RiskLevel;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeploymentReadinessServiceTest {

    private static final Instant EVALUATED_AT = Instant.parse("2026-08-30T12:00:00Z");
    private final DeploymentReadinessService service = new DeploymentReadinessService(
            Clock.fixed(EVALUATED_AT, ZoneOffset.UTC));

    @Test
    void approvesDeploymentWhenEveryGatePasses() {
        DeploymentDecision decision = service.evaluate(readyAssessment());

        assertThat(decision.decision()).isEqualTo("READY");
        assertThat(decision.blockers()).isEmpty();
        assertThat(decision.gates()).allSatisfy((name, passed) -> assertThat(passed).isTrue());
        assertThat(decision.evaluatedAt()).isEqualTo(EVALUATED_AT);
    }

    @Test
    void holdsDeploymentWhenCriticalConstraintIsUnresolved() {
        ReadinessAssessment baseline = readyAssessment();
        ReadinessAssessment assessment = new ReadinessAssessment(
                baseline.deploymentId(),
                baseline.discoveryEvidence(),
                List.of(new OperationalConstraint(
                        "PII-01", RiskLevel.CRITICAL, "PII may appear in model output", false)),
                baseline.evaluation(),
                baseline.businessOwner(),
                baseline.technicalOwner(),
                baseline.rollbackPlan());

        DeploymentDecision decision = service.evaluate(assessment);

        assertThat(decision.decision()).isEqualTo("HOLD");
        assertThat(decision.blockers()).containsExactly("criticalConstraintsResolved");
    }

    @Test
    void reportsEachFailedQualityAndDeliveryGate() {
        ReadinessAssessment assessment = new ReadinessAssessment(
                "finance-reporting-copilot-v1",
                List.of(new DiscoveryEvidence(EvidenceType.PROCESS, "workshop", "Monthly close workflow")),
                List.of(),
                new AiQualityEvaluation(0.79, 0.94, 0.02, 2_001),
                " ",
                "engineering@example.com",
                " ");

        DeploymentDecision decision = service.evaluate(assessment);

        assertThat(decision.decision()).isEqualTo("HOLD");
        assertThat(decision.blockers()).containsExactly(
                "discoveryCoverage",
                "retrievalRecall",
                "groundedClaimCoverage",
                "errorRate",
                "p95Latency",
                "accountableOwners",
                "rollbackPlan");
    }

    private ReadinessAssessment readyAssessment() {
        return new ReadinessAssessment(
                "finance-reporting-copilot-v1",
                List.of(
                        new DiscoveryEvidence(EvidenceType.PROCESS, "operator workshop", "Mapped close workflow"),
                        new DiscoveryEvidence(EvidenceType.DATA, "lineage review", "Validated source ownership"),
                        new DiscoveryEvidence(EvidenceType.INTEGRATION, "sandbox trace", "Verified API boundaries")),
                List.of(new OperationalConstraint(
                        "AUDIT-01", RiskLevel.HIGH, "Every answer requires traceable evidence", true)),
                new AiQualityEvaluation(0.88, 0.98, 0.005, 1_450),
                "finance-operations@example.com",
                "ai-platform@example.com",
                "Disable route and restore the prior reporting workflow");
    }
}
