# Architecture and Product Specifications

These documents are the authoritative planning baseline for the Cleanest & Greenest Community Platform MVP.

## Reading order

1. [`requirements.md`](requirements.md) — product scope and business rules
2. [`permissions.md`](permissions.md) — roles, scope, and separation of duties
3. [`workflows.md`](workflows.md) — states and permitted transitions
4. [`baseline-questionnaire-v1.md`](baseline-questionnaire-v1.md) — exact baseline contract
5. [`reporting-forms-v1.md`](reporting-forms-v1.md) — exact operational reporting contracts
6. [`scoring-specification.md`](scoring-specification.md) — scoring framework and pending M&E decisions
7. [`database-design.md`](database-design.md) — logical PostgreSQL model and ERD
8. [`api-contract.md`](api-contract.md) — REST API conventions and endpoints
9. [`frontend-structure.md`](frontend-structure.md) — screens, routes, and frontend responsibilities
10. [`offline-synchronization.md`](offline-synchronization.md) — offline queue, retry, and conflicts
11. [`system-architecture.md`](system-architecture.md) — runtime and approved deployment architecture
12. [`backend-code-structure.md`](backend-code-structure.md) — modular-monolith packages and coding boundaries
13. [`evidence-security-retention.md`](evidence-security-retention.md) — evidence, privacy, security, and proposed retention
14. [`test-strategy.md`](test-strategy.md) — automated, integration, security, and field testing
15. [`implementation-roadmap.md`](implementation-roadmap.md) — milestone and branch sequencing

## Approved foundation

- React, TypeScript, and Vite PWA
- Java 21 and Spring Boot API
- PostgreSQL with Flyway migrations and UUID identifiers
- Short-lived JWT access tokens with rotating refresh sessions
- Render Static Site, Render Web Service, and Render PostgreSQL
- Vercel branch/commit previews for the frontend
- Private Cloudflare R2 evidence storage
- Versioned questionnaires, forms, rules, revisions, and score snapshots
- Verified-data-only scoring
- Offline IndexedDB queue with idempotency and conflict detection

## Pending external approval

- Exact scoring formulas, targets, rubrics, and periods from M&E
- Final privacy/consent and safeguarding wording
- Final evidence, financial, personal-data, audit, and backup retention durations
- Data-location/donor requirements
- Production account ownership, billing, domains, monitoring, and incident contacts

No application implementation should contradict these specifications without an explicit reviewed documentation change.
