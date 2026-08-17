# Backend Code Structure and Engineering Standards

## 1. Architectural style

The backend is a modular monolith built with Spring Boot. Code is organized by business feature first, then by responsibility inside each feature.

```text
HTTP/API
→ Application use cases
→ Domain model and policies
→ Ports
→ Infrastructure adapters
```

The application remains one deployable service. This structure provides clear module boundaries without premature microservices.

## 2. Service versus Manager decision

The backend will not add a general `Manager` layer.

`Manager` is usually ambiguous and often duplicates service responsibilities. Instead, use precise names:

| Responsibility | Naming |
| --- | --- |
| Coordinate a business use case/transaction | `*ApplicationService` |
| Execute a focused command when a module becomes complex | `*CommandHandler` |
| Serve optimized read-only queries | `*QueryService` |
| Express a domain rule spanning entities/value objects | `*Policy` or `*DomainService` |
| Abstract persistence | `*Repository` port |
| Abstract object storage, clock, IDs, messaging | Purpose-specific `*Port` |
| Implement an external/persistence port | `*Adapter` |

Examples:

- `CommunityApplicationService`
- `BaselineApplicationService`
- `BaselineReviewApplicationService`
- `CommunityQueryService`
- `EligibilityPolicy`
- `SeparationOfDutiesPolicy`
- `CommunityRepository`
- `EvidenceStoragePort`
- `R2EvidenceStorageAdapter`

Avoid names such as `CommunityManager`, `DataManager`, `CommonService`, `HelperService`, or `Utils` unless a narrowly defined technical responsibility genuinely justifies them.

## 3. Domain-oriented API and use-case naming

Use CRUD names for genuinely simple CRUD concepts and business-intent names for domain workflows.

| Avoid | Prefer |
| --- | --- |
| `CreateCommunityRequest` | `RegisterCommunityRequest` |
| `UpdateCommunityRequest` | `EditCommunityProfileRequest` |
| `UpdateBaselineRequest` | `SaveBaselineDraftRequest` |
| `UpdateBaselineStatusRequest` | `SubmitBaselineRequest`, `VerifyBaselineRequest` |
| `UpdateCommunityStatusRequest` | `SuspendCommunityRequest`, `ReinstateCommunityRequest` |
| `UpdateOfficerRequest` | `AssignCommunityOfficerRequest` |
| `UpdateVerificationRequest` | `ApproveSubmissionRequest`, `RejectSubmissionRequest`, `RequestSubmissionCorrectionRequest` |

Core use cases use explicit intent, including:

- `registerCommunity(...)`
- `editCommunityProfile(...)`
- `assignCommunityOfficer(...)`
- `startBaselineAssessment(...)`
- `saveBaselineDraft(...)`
- `submitBaseline(...)`
- `requestBaselineCorrection(...)`
- `verifyBaseline(...)`
- `grantEligibility(...)`
- `enrollCommunity(...)`
- `activateParticipation(...)`
- `suspendCommunity(...)`
- `submitWasteCollection(...)`
- `approveSubmission(...)`
- `publishScoringRules(...)`
- `calculateScoreSnapshot(...)`
- `publishLeaderboard(...)`

Avoid ambiguous workflow methods such as `updateCommunity(...)`, `updateStatus(...)`, `process(...)`, or `handle(...)` when a business action can be named directly.

REST resource paths may remain noun-oriented, while command endpoints, request types, application methods, audit actions, and domain methods express the business transition. For example, `POST /communities` may invoke `registerCommunity(...)`; `POST /baseline-assessments/{id}/submit` invokes `submitBaseline(...)`.

Simple reference/configuration concepts such as waste categories may use conventional create, read, update, and deactivate naming when no richer business transition exists.

Do not introduce `BaseService`, `CrudService`, `BaseController`, generic Manager abstractions, or generic status-update endpoints.

## 4. Top-level package structure

```text
src/main/java/com/ewomen/greenfuture/
├── GreenfutureApplication.java
├── common/
│   ├── api/
│   ├── audit/
│   ├── config/
│   ├── error/
│   ├── persistence/
│   ├── security/
│   └── validation/
├── auth/
├── identity/
├── community/
├── baseline/
├── competition/
├── submission/
├── evidence/
├── verification/
├── scoring/
├── synchronization/
├── reporting/
└── publicportal/
```

`common` contains only capabilities genuinely shared by multiple modules. Feature-specific code must not be placed there for convenience.

## 5. Structure inside a feature

Example community module:

```text
community/
├── api/
│   ├── CommunityController.java
│   ├── request/
│   │   ├── RegisterCommunityRequest.java
│   │   └── EditCommunityProfileRequest.java
│   ├── response/
│   │   └── CommunityResponse.java
│   └── CommunityApiMapper.java
├── application/
│   ├── CommunityApplicationService.java
│   ├── CommunityQueryService.java
│   ├── command/
│   │   ├── RegisterCommunityCommand.java
│   │   └── EditCommunityProfileCommand.java
│   └── result/
│       └── CommunityResult.java
├── domain/
│   ├── Community.java
│   ├── CommunityId.java
│   ├── CommunityStatus.java
│   ├── CommunityRepository.java
│   ├── CommunityPolicy.java
│   ├── event/
│   └── exception/
└── infrastructure/
    └── persistence/
        ├── CommunityJpaEntity.java
        ├── SpringDataCommunityRepository.java
        ├── JpaCommunityRepositoryAdapter.java
        └── CommunityPersistenceMapper.java
```

Small features do not need empty folders or one class per method. Add command handlers or subpackages only when they improve clarity.

## 6. Layer responsibilities

### API layer

Responsible for:

- HTTP route and status semantics
- Request deserialization
- Syntactic validation
- Calling one application use case
- Converting application results to response DTOs
- Passing idempotency/version/request context

Not responsible for:

- Business rules
- Direct repository calls
- Transaction orchestration
- JPA entity manipulation
- Score or permission decisions

Controllers should be small and contain no workflow logic.

### Application layer

Responsible for:

- Use-case orchestration
- Transaction boundaries
- Loading/saving aggregates through ports
- Calling authorization scope checks and domain policies
- Coordinating evidence, audit, and domain events
- Returning application results

Application services are stateless Spring beans. Public methods represent business use cases, not generic CRUD helpers.

### Domain layer

Responsible for:

- Entities and aggregate roots
- Value objects
- State transitions and invariants
- Domain policies
- Domain events
- Repository port interfaces
- Domain-specific exceptions

The domain layer must not depend on Spring MVC, HTTP DTOs, JPA repositories, Cloudflare SDKs, or frontend concepts.

### Infrastructure layer

Responsible for:

- JPA persistence implementation
- Spring Data repository interfaces
- Cloudflare R2/S3 adapter
- JWT/cryptographic implementation details
- Email/notification adapters when added
- System clock/ID implementations where abstraction is valuable
- External configuration bindings

Infrastructure implements ports owned by the application/domain side.

## 7. Domain modeling rules

- Model important concepts as types, not primitive strings: `CommunityId`, `SubmissionId`, `Money`, `WasteMass`, `ReportingPeriod`.
- Aggregate roots protect their own state transitions.
- Do not expose public setters that allow invalid state.
- State changes occur through named methods such as `submit()`, `verify()`, `requestCorrection()`, `grantEligibility()`, or `activateParticipation()` rather than unrestricted status setters.
- Immutable submitted revisions are modeled as immutable records/entities after transition.
- Store timestamps as `Instant` unless a genuine local calendar value is required.
- Store money with `BigDecimal` and currency code.
- Store official quantities with `BigDecimal`, original unit, and normalized value.
- Enums stored in PostgreSQL-facing records use stable string codes.

## 8. Persistence approach

Domain models and JPA models are separate where the domain has meaningful invariants or persistence annotations would distort the model.

- Domain repository interfaces live in the feature's domain/application boundary.
- Spring Data interfaces and JPA entities live in infrastructure.
- Persistence adapters map between domain and JPA models.
- Flyway owns schema changes.
- Hibernate production mode validates rather than modifies the schema.
- Avoid exposing lazy JPA relationships outside infrastructure transactions.
- Use explicit queries/projections for review queues and dashboards.

For simple immutable reference data, pragmatic shared mapping may be accepted when reviewed; separation is the default for core workflow aggregates.

## 9. Command and query pattern

Commands represent requested changes:

```java
public record RegisterCommunityCommand(
        String name,
        String districtCode,
        String boundaryDescription) {
}
```

Application service methods express intent:

```java
public CommunityResult registerCommunity(RegisterCommunityCommand command) {
    // authorize, validate, create aggregate, persist, audit
}
```

Queries do not load full aggregates unnecessarily. `CommunityQueryService` may return optimized projections/read models for lists and dashboards.

We do not adopt a complex CQRS infrastructure in the MVP. Command/query separation is a code-clarity convention within one PostgreSQL-backed application.

## 10. DTO and mapping rules

- Request/response DTOs belong to the API layer.
- API DTOs are never JPA entities.
- Application commands/results do not depend on HTTP annotations.
- Public API DTOs are separate from authenticated/internal DTOs.
- Mapping is explicit and feature-local.
- MapStruct may be considered only when it reduces repetitive safe mapping; reflection-based generic mapping is not used for security-sensitive boundaries.
- Never accept server-owned values such as actor, community context, score, verification state, or audit timestamps from client input.

## 11. Validation

Validation occurs at complementary levels:

1. API validation: shape, required input, string bounds, basic formats.
2. Application validation: authorization, existence, uniqueness, current version, use-case prerequisites.
3. Domain validation: invariants and valid transitions.
4. Database validation: foreign keys, uniqueness, checks, and nullability.

Business rules must not exist only in frontend validation.

## 12. Transactions and concurrency

- `@Transactional` belongs on application-service use cases, not controllers.
- Read-only application queries use `@Transactional(readOnly = true)` when appropriate.
- External file/network calls are not held inside long database transactions.
- Evidence workflows use explicit states to coordinate PostgreSQL and R2 without pretending they share one transaction.
- Mutable aggregates use optimistic versions.
- API `If-Match`/base versions map to concurrency checks.
- Idempotency records protect retryable mutations.

## 13. Errors

- Domain/application code throws typed exceptions with stable internal error codes.
- A shared API exception handler maps them to the standard error envelope.
- Do not leak stack traces, SQL, storage keys, or security details to clients.
- Expected business conflicts use appropriate `409`, `412`, or `422` responses.
- Unexpected failures include a request ID for support and structured logging.

## 14. Security boundaries

- Method/use-case authorization complements route-level security.
- Scope checks occur before returning whether an out-of-scope record exists.
- Controllers never trust community/user IDs to establish ownership.
- Separation of duties is a reusable policy invoked by verification use cases.
- Public API queries use explicitly approved public projections.
- Sensitive fields require explicit inclusion; they are not removed after generic serialization.

## 15. Audit and domain events

- High-impact use cases record audit events in the same database transaction as the state change when possible.
- Domain events describe completed domain facts, such as `BaselineSubmitted` or `EligibilityGranted`.
- In-process event handlers may update related state after transaction-safe publication.
- External messaging infrastructure is not required for MVP foundation.
- If asynchronous delivery becomes necessary, use a transactional outbox rather than publishing untracked messages inside a database transaction.

## 16. Testing structure

Tests mirror production features:

```text
src/test/java/com/ewomen/greenfuture/
├── auth/
├── community/
├── baseline/
├── competition/
├── submission/
└── support/
```

- Domain tests use ordinary unit tests without Spring where possible.
- Application tests use controlled port fakes or focused Spring integration tests.
- Persistence/security/API behavior uses PostgreSQL Testcontainers and Spring tests.
- Architecture tests should detect controllers calling repositories and feature modules depending on another feature's infrastructure package.

## 17. Inter-module communication

- A feature must not reach into another feature's `infrastructure` package.
- Cross-feature calls use a public application interface or stable domain identifier/result.
- Avoid bidirectional compile-time dependencies.
- Shared database access does not justify bypassing module APIs.
- Reporting/public projections may query across tables through dedicated read infrastructure without modifying other aggregates.

## 18. Coding conventions

- Constructor injection only; no field injection.
- Prefer immutable records/value objects for commands and results.
- Use clear business names rather than abbreviations except approved codes such as M&E at the UI level.
- Keep methods focused and avoid boolean parameters that hide intent.
- Do not return `null` collections.
- Do not use exceptions for normal query absence when `Optional`/explicit result is clearer.
- Comments explain decisions and constraints, not obvious syntax.
- No secrets or environment-specific URLs in source code.
- No generic base controller/service/repository that obscures feature behavior.
- Use domain-intent names for workflow request types, commands, methods, events, and audit actions.
- No Lombok `@Data` on domain/JPA entities; generated equality/setters can break identity and invariants.

## 19. Legacy migration rule

Existing controllers, services, entities, and repositories are prototype inputs, not the required target structure.

During `feature/foundation` and later feature milestones:

1. Preserve working behavior only when it matches approved requirements.
2. Move/refactor code feature by feature, not through an uncontrolled bulk rewrite.
3. Do not create compatibility wrappers that preserve insecure or obsolete domain behavior indefinitely.
4. Remove EcoTrike/citizen features only through explicit reviewed scope changes.
5. Keep every intermediate commit buildable when practical.

## 20. Foundation-stage structure

`feature/foundation` initially creates only packages needed by foundation behavior:

- `common`
- `auth`
- `identity`
- evidence storage port/adapter foundation

Other feature packages are added when their milestones begin. Do not create large sets of empty packages/classes merely to match the future tree.
