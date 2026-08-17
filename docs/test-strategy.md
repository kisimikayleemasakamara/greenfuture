# Test Strategy

## 1. Objectives

Testing must demonstrate that authorized users can complete real workflows safely under unreliable connectivity while preserving evidence, audit history, verification integrity, and correct scoring traceability.

## 2. Test layers

| Layer | Backend | Frontend |
| --- | --- | --- |
| Unit | Domain rules, validators, calculations, authorization helpers | Pure functions, validation schemas, queue reducers, formatting |
| Component | Spring services with controlled dependencies | Forms, status UI, evidence controls, role navigation |
| Integration | PostgreSQL repositories, Flyway, security filter chain, R2 adapter contract | API client, IndexedDB, service worker, sync queue |
| API/contract | Request validation, errors, permissions, idempotency, ETags | Generated/typed client compatibility |
| End-to-end | Full deployed workflow through API | Browser journeys across PWA/API |
| Field | Connectivity loss, GPS, camera, storage pressure, real devices | Primary focus |

## 3. Backend test stack and rules

- JUnit 5 and Spring Boot Test
- Testcontainers PostgreSQL for persistence/migration integration tests
- MockMvc or equivalent Spring HTTP testing for API/security behavior
- WireMock or controlled fake for the S3-compatible R2 adapter
- Flyway migration validation from an empty database and supported upgrade paths
- No critical domain test should depend on a developer's local PostgreSQL instance

Backend coverage priorities:

- Role and community-scope authorization
- Separation of duties
- Workflow transition guards
- Questionnaire validation and immutable revisions
- Idempotent sync operations
- Optimistic concurrency conflicts
- Waste-batch mass balance and duplicate controls
- Evidence requirements
- Verified-fact creation/invalidation
- Score calculation and historical snapshots
- Public/private response boundaries

## 4. Frontend test stack and rules

- Vitest for unit tests
- React Testing Library for accessible component behavior
- Playwright for end-to-end and browser/PWA journeys
- Fake IndexedDB or controlled browser database per test
- Mock Service Worker or equivalent controlled API mocking for focused frontend tests
- Real staging API for selected contract and end-to-end suites

Frontend coverage priorities:

- Role/workspace routing
- Dynamic baseline/form validation
- Offline save before success message
- Evidence queue progress and failure
- Retry without duplicate operation
- Conflict display and resolution
- Read-only submitted/approved revisions
- Connectivity and sync status
- Accessible labels, keyboard behavior, focus, and error summaries
- Public pages never rendering private fields

## 5. Critical end-to-end journeys

1. Admin creates community and assigns Field Officer.
2. Field Officer starts baseline online, continues offline, captures GPS/photos, and synchronizes.
3. M&E cannot verify their own assessment.
4. Independent M&E requests correction and sees immutable original revision.
5. Field Officer submits corrected revision; reviewer verifies it.
6. Admin grants eligibility, enrolls, and activates community.
7. Community Officer cannot change community context.
8. Community Officer submits weighted waste/evidence offline and retries after interruption.
9. M&E approves submission and traceable verified facts appear.
10. Duplicate/mass-balance conflict blocks incorrect approval.
11. Score recalculation uses only verified facts and preserves prior snapshots.
12. Published leaderboard exposes approved aggregates only.
13. Suspension prevents new submissions without deleting history.
14. Refresh-token reuse revokes the session family.
15. Unauthorized evidence URL/access request is rejected.

## 6. Offline and synchronization matrix

Test at these interruption points:

- Before local draft transaction commits
- After local save but before queue creation
- During evidence upload-session creation
- During evidence byte upload
- After server mutation but before response reaches client
- During ordered multi-operation push
- Before/after pull cursor application
- During token expiry/refresh
- While assignment or workflow state changes on server

Expected invariants:

- No acknowledged local work disappears.
- Retry creates one logical server result.
- Submitted revisions do not revert to editable state.
- Conflicts preserve local and server versions.
- Pull cursor advances only after transactional local application.

## 7. Security tests

- Unauthenticated/expired/revoked session behavior
- Role matrix and multi-role constraints
- Horizontal access attempts across communities
- Altered community/participation identifiers
- Self-verification attempt
- CSRF protection for cookie-backed refresh/logout operations
- JWT signature, issuer, audience, expiry, and malformed-token handling
- Refresh rotation/reuse
- Upload content-type spoofing and oversize files
- Path/object-key manipulation
- Signed evidence URL expiry
- Injection and unsafe free-text rendering
- Rate limits on authentication/recovery
- Public response data leakage
- Audit creation for high-impact actions

## 8. Scoring tests

- Category/total weights validate to 100
- Binary, quantitative, and rubric formulas
- Caps/floors and decimal precision
- Missing versus zero versus unranked behavior
- Unit normalization
- Duplicate verified-fact exclusion
- Superseded fact exclusion
- Reporting-period boundaries
- Rule-version immutability
- Ranking by unrounded values
- Tie-break order and equal-rank semantics
- Recalculation creates new snapshot rather than overwriting history

Exact expected values will be added when M&E approves production formulas.

## 9. Performance and reliability targets

Initial targets to validate in staging/pilot:

- Normal API reads: 95th percentile below 800 ms under expected pilot load
- Normal API writes: 95th percentile below 1.5 seconds excluding file transfer
- Dashboard initial useful data: below 3 seconds on representative mobile connectivity
- Draft local save acknowledgement: below 300 ms on supported devices
- Evidence upload: visible progress and safe retry; no false completion
- Approximately 20 communities with expected reporting/evidence volume without database query degradation

These are product targets, not guarantees until representative testing is performed.

## 10. Device and field matrix

Pilot on:

- At least two lower/mid-range Android phones
- Current Chrome and one reasonably older supported Chrome version
- Desktop Chrome/Edge for Admin and M&E
- Slow, intermittent, and fully offline connectivity
- Low available device storage
- Denied camera/location permission
- Poor GPS accuracy
- App close/reopen with queued work

## 11. Accessibility tests

- Automated accessibility checks on representative screens
- Keyboard navigation for desktop workflows
- Screen-reader spot checks for forms, errors, dialogs, tables, and status changes
- Contrast and non-color status indications
- Touch-target and zoom testing on mobile
- Error summary links/focus to invalid fields

## 12. Test data

Maintain deterministic fixtures for:

- Every role and useful multi-role combination
- Assigned/unassigned users
- Every community/participation state
- Questionnaire versions and baseline revisions
- Every submission type
- Valid, missing, rejected, and quarantined evidence
- Conflicting drafts and sync operations
- Verified facts, rulesets, scores, ties, and unranked cases

Production personal/evidence data must not be copied into development tests.

## 13. CI quality gates

Every pull request should run relevant:

- Compile/typecheck
- Formatting/lint
- Unit and component tests
- Backend integration/API tests
- Frontend build
- Migration validation
- Dependency/security checks
- Selected accessibility checks

Main/staging additionally runs full integration and critical end-to-end journeys. Production promotion requires passing staging smoke tests and an explicit approval.

## 14. Exit criteria for pilot

- No open critical security/data-loss defect
- All critical end-to-end journeys pass
- Offline retry/idempotency demonstrated on real devices
- Required evidence successfully captured and reviewed
- Role/scope and separation-of-duty tests pass
- Database restore procedure demonstrated
- M&E validates review and score traceability
- Field users can complete baseline/reporting with acceptable time/support
- Known limitations and support procedure documented

