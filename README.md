# FDE Delivery Control Plane

[![CI](https://github.com/trudynamica/fde-delivery-control-plane/actions/workflows/ci.yml/badge.svg)](https://github.com/trudynamica/fde-delivery-control-plane/actions/workflows/ci.yml)
[![CodeQL](https://github.com/trudynamica/fde-delivery-control-plane/actions/workflows/codeql.yml/badge.svg)](https://github.com/trudynamica/fde-delivery-control-plane/actions/workflows/codeql.yml)
[![Release](https://img.shields.io/github/v/release/trudynamica/fde-delivery-control-plane)](https://github.com/trudynamica/fde-delivery-control-plane/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

An auditable production-readiness gate for forward-deployed AI systems. The service turns field discovery, operational risk, model quality, accountable ownership, and rollback planning into an explicit `READY` or `HOLD` decision.

This reference implementation demonstrates how I approach forward-deployed engineering: learn the operating environment with domain experts, encode what production-safe means, and make deployment decisions inspectable instead of subjective.

## Delivery model

```text
Discover the operation
        |
        v
Map process, data, and integration evidence
        |
        v
Resolve operational constraints
        |
        v
Evaluate AI quality and runtime behavior
        |
        v
Assign business/technical owners and rollback plan
        |
        v
Issue an auditable READY or HOLD decision
```

## Release gates

| Gate | Default |
| --- | ---: |
| Discovery coverage | Process + data + integration evidence |
| Critical operational risks | All resolved |
| Retrieval recall | >= 0.80 |
| Grounded claim coverage | >= 0.95 |
| Error rate | <= 0.01 |
| P95 latency | <= 2,000 ms |
| Accountability | Business and technical owners assigned |
| Recovery | Rollback plan documented |

## Run it

Requires Java 21 and Maven 3.9+.

```bash
mvn spring-boot:run
```

Evaluate the included finance-reporting example:

```bash
curl --request POST \
  --header 'Content-Type: application/json' \
  --data @examples/readiness-assessment.json \
  http://localhost:8080/api/readiness/evaluate
```

The response exposes every evaluated gate and any blockers:

```json
{
  "deploymentId": "finance-reporting-copilot-v1",
  "decision": "READY",
  "evaluatedAt": "2026-08-30T12:00:00Z",
  "gates": {
    "discoveryCoverage": true,
    "criticalConstraintsResolved": true,
    "retrievalRecall": true,
    "groundedClaimCoverage": true,
    "errorRate": true,
    "p95Latency": true,
    "accountableOwners": true,
    "rollbackPlan": true
  },
  "blockers": []
}
```

Change a critical constraint to `"resolved": false` and the same endpoint
returns an inspectable hold decision:

```json
{
  "deploymentId": "finance-reporting-copilot-v1",
  "decision": "HOLD",
  "gates": {
    "criticalConstraintsResolved": false
  },
  "blockers": ["criticalConstraintsResolved"]
}
```

## Verify

```bash
mvn verify
```

The tests cover a release-ready deployment, an unresolved critical constraint, compound quality and delivery failures, and RFC 9457-style validation errors.

## Engineering controls

- typed request, quality-evaluation, constraint, and decision contracts;
- explicit release thresholds with deterministic blocker reporting;
- request validation with machine-readable Problem Details responses;
- graceful shutdown and a production-packaged executable artifact;
- CodeQL analysis and automated Maven and GitHub Actions updates.

## Why this is public

This is an original, synthetic reference implementation. It contains no employer, client, production, or proprietary source code or data.

## About

Built by [Mkrtych Oganyan](https://www.moganyan.com), Enterprise AI Architect and Forward-Deployed Engineering leader. See also the [Enterprise RAG Evaluation Harness](https://github.com/trudynamica/enterprise-rag-evaluation-harness).
