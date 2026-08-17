# Cleanest & Greenest Community Platform — MVP Requirements

## 1. Purpose

This document defines the agreed MVP scope and business rules for the Cleanest & Greenest Community platform. It is a living specification and must be versioned when approved requirements change.

The platform measures three transformations:

- Waste to cleaner communities
- Waste to resources
- Waste to wealth

The initial rollout targets approximately 20 communities through a React and TypeScript mobile-first progressive web application (PWA), a Spring Boot API, PostgreSQL, and private evidence storage.

## 2. MVP boundaries

### Included

- Authentication and role-based authorization
- Community registration and profile management
- Versioned baseline questionnaire and assessment
- Independent baseline verification
- Community eligibility and competition activation
- Community, Field Officer, M&E, and Admin workflows
- Environmental activity and waste reporting
- Waste weighing and configurable waste categories
- Waste-to-Wealth reporting
- Photo, GPS, document, receipt, and weighing evidence
- Offline drafts and queued synchronization
- Correction and immutable submission revision history
- M&E verification
- Configurable competition indicators and scoring
- Dashboards, reports, awards, and public leaderboard

### Deferred beyond the MVP

- EcoTrike operations and assignments
- Citizen waste reporting
- Native Android and iOS applications

Deferred modules must not shape or complicate the MVP domain model. They may integrate through future modules and APIs.

## 3. Users, roles, and access

A user may hold multiple roles. Permissions are additive, subject to record ownership, community assignment, workflow status, and separation-of-duty rules.

| Role | Default access |
| --- | --- |
| Admin | All communities, configuration, users, competitions, verification, reporting, and public content |
| Community Officer | Their assigned active community and its permitted reporting workflows |
| Field Officer | Assigned communities, field assessment, visits, evidence capture, and submissions |
| M&E | All participating communities, verification queues, monitoring, scoring review, and reports |

Rules:

1. A Community Officer is assigned to a specific community.
2. A Community Officer cannot manually change community context.
3. Field Officers normally access only assigned communities.
4. M&E and Admin access all communities by default.
5. A user must not perform final baseline verification on an assessment they created or submitted.
6. All authorization is enforced by the backend; hiding a frontend control is not authorization.

## 4. Community participation lifecycle

Community existence, eligibility, and competition participation are separate concepts.

### Initial community registration

The initial registration captures:

- Community name (required)
- Ward
- Chiefdom
- District
- Estimated households
- Estimated population
- Estimated women population
- Estimated youth population, ages 15–35
- Estimated persons with disabilities
- Community leader name
- Community leader phone
- Community focal-person name
- Community focal-person phone

All fields except community name are optional at initial registration and may be completed or corrected during the baseline process. Demographic values are estimates with an as-of date and source where available. Phone numbers and named contacts are restricted operational data and are never public by default.

### Community lifecycle

```text
Draft
→ Baseline In Progress
→ Pending Baseline Verification
→ Correction Required / Baseline Verified / Rejected
→ Eligible
→ Suspended / Inactive
```

### Competition participation lifecycle

```text
Invited
→ Enrolled
→ Active
→ Suspended / Withdrawn / Completed
```

`Eligible` means the community has passed the general baseline gate. `Active` belongs to the community's participation in a particular competition.

Only eligible communities with active competition participation may appear in that competition's reporting tools, rankings, or public leaderboard.

## 5. Baseline questionnaire version 1

Questionnaires are versioned. Published versions are immutable; additions or changes create a new version. Every assessment records the questionnaire version used.

The implementable version 1.0 question contract is defined in `baseline-questionnaire-v1.md`.

### Assessment metadata

- Community
- Assessment date and time
- Assessor and assessor role
- Assessment GPS coordinates
- General site photographs
- Notes

### Waste conditions

- Estimated plastic generated per week
- Original quantity and unit
- Normalized quantity in kilograms
- Measurement method: weighed, estimated, or reported
- Quantity evidence where available
- Number of known dumping sites
- Number of functioning collection points
- Pollution severity, rated 1–5
- Waste-hotspot description, GPS locations, and photos
- Observed configurable waste categories

Estimated or reported baseline quantities describe starting conditions but do not become verified competition quantities without approved weighing evidence.

### Organization and participation

- Leadership readiness, rated 1–5
- Women's participation, rated 1–5
- Youth participation, rated 1–5
- Disability inclusion, rated 1–5
- Stakeholder partnership potential, rated 1–5
- Existing participating groups
- Community focal person and contact details
- Willingness to participate
- Community commitments
- Person confirming commitments and confirmation date

### Operational readiness

- Digital readiness, rated 1–5
- Recycling potential, rated 1–5
- Clean-up frequency
- Existing segregation practices
- Existing collection method
- Existing recycling or upcycling activity
- Existing waste-based enterprises
- Availability of weighing equipment
- Accessibility of collection points
- EcoTrike suitability, optional and excluded from MVP eligibility and scoring

### Rating scale

| Rating | Meaning |
| ---: | --- |
| 1 | None or very poor |
| 2 | Limited or weak |
| 3 | Moderate or basic |
| 4 | Good or established |
| 5 | Strong or advanced |

Ratings support assessor comments. Question-specific rating guidance may be added in later questionnaire versions.

### Initial eligibility gate

A community may be marked eligible when:

1. Identity and location are confirmed.
2. An authorized officer completed and submitted all required baseline fields.
3. Required GPS and photographic evidence is present.
4. Waste conditions and hotspots are documented.
5. Leadership and participation information is complete.
6. The community confirmed its willingness and commitments.
7. A different authorized M&E or Admin user verified the assessment.
8. No required correction remains unresolved.

Low baseline ratings do not automatically disqualify a community. The baseline describes the starting position from which improvement can be measured.

## 6. Reporting, evidence, and revisions

The implementable version 1.0 operational form contract is defined in `reporting-forms-v1.md`.

Submission workflow:

```text
Draft
→ Submitted
→ Pending M&E Review
→ Approved / Rejected / Correction Required
→ Verified Indicator Data
```

Rules:

1. Only approved, verified data contributes to official scores.
2. Evidence requirements are configurable by submission/report type.
3. Waste weights require weighing evidence.
4. Sales require quantity, buyer information, and receipt or confirmation evidence.
5. Products require photographs and production information.
6. Field activities require date, location, and photographic evidence.
7. Financial claims require supporting evidence where configured.
8. A correction creates a new revision; it never overwrites a submitted revision.
9. Original values, evidence, reviewer comments, timestamps, changed fields, and decisions remain auditable.
10. Only the latest approved revision is official.

Evidence files are stored in private object storage. PostgreSQL stores metadata, ownership, integrity information, and access rules rather than large file contents.

## 7. Measurements and money

- Kilograms are the canonical waste mass unit.
- The UI may accept grams, kilograms, and tonnes/metric tonnes.
- Bags or other non-standard units may be recorded, but cannot feed official quantity scoring without a verified conversion.
- Original value and unit are retained alongside normalized kilograms.
- SLE is the canonical currency for the initial implementation.
- Monetary values are stored using fixed-precision decimal values, never floating point.

## 8. Competition and scoring

The six fixed category allocations total 100 points:

| Category | Points |
| --- | ---: |
| Cleanliness and sanitation | 25 |
| Waste collection and segregation | 15 |
| Waste-to-Wealth and business creation | 25 |
| Community participation and inclusion | 15 |
| Environmental sustainability | 10 |
| Digital reporting and evidence | 10 |

The scoring engine uses a configurable weighted hybrid model:

- Binary/compliance indicators
- Normalized quantitative indicators
- Qualitative rubric indicators

Rules, formulas, thresholds, weights, effective dates, and evidence requirements are versioned. Historical score snapshots retain the scoring-rule version used.

Tie-breakers are applied in order:

1. Higher Waste-to-Wealth score
2. Higher Waste Collection and Segregation score
3. Higher Cleanliness and Sanitation score
4. Higher verified waste diverted
5. Equal rank if still tied

## 9. Public information

Public users may see active participating communities, approved profiles, scores, rankings, awards, verified environmental impact, and aggregated verified revenue.

The following are private by default:

- Costs and detailed profit
- Individual payments
- Buyer and payment details
- Enterprise-sensitive financial records
- Internal evidence and reviewer comments
- Personal contact information

## 10. Offline and synchronization requirements

1. Users can create and edit permitted drafts without connectivity.
2. Locally created records use globally unique client-generated identifiers.
3. The UI shows Saved Offline, Pending Sync, Synced, and Sync Failed states.
4. API writes are idempotent so retries do not create duplicates.
5. Evidence uploads use a durable queue and retry independently from record metadata.
6. Version-based conflict detection prevents silent overwrites.
7. Conflicting versions are preserved for explicit resolution.
8. Approved records are locked and changed only through revisions.
9. A user/device owns a draft by default to reduce concurrent-edit conflicts.

## 11. Non-functional requirements

- Mobile-first responsive interface
- Installable PWA for modern Android devices
- Desktop layouts for Admin and M&E
- Encryption in transit
- Secure password handling and token/session controls
- Least-privilege authorization
- Input and file validation
- Immutable audit trail for material decisions
- Database migrations and recoverable backups
- Monitoring and structured error logging
- Accessible forms, navigation, labels, and validation feedback
- Traceable exports and reports

## 12. Acceptance of this specification

This document establishes the MVP baseline. Unresolved details—especially exact scoring formulas, question-specific rubrics, reporting forms, and retention periods—must be completed in their dedicated specifications before the affected module is implemented.

Approved foundation decisions:

- React, TypeScript, and Vite for the frontend PWA
- PostgreSQL with Flyway-controlled migrations
- UUID identifiers for API/offline-facing records
- Short-lived JWT access tokens with rotating server-tracked refresh sessions
- Render Static Site for the frontend
- Render Web Service for the Spring Boot API
- Render PostgreSQL for the managed relational database
- Private Cloudflare R2 for S3-compatible evidence storage

Security, evidence handling, proposed retention, testing, and delivery sequencing are defined in `evidence-security-retention.md`, `test-strategy.md`, and `implementation-roadmap.md`.
