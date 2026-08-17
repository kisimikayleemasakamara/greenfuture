# REST API Contract — MVP

## 1. Conventions

- Base path: `/api/v1`
- Media type: `application/json`
- Authentication: short-lived JWT bearer access token with rotating opaque refresh session
- Timestamps: ISO 8601 UTC
- Identifiers exposed through the API: UUID strings
- Pagination: cursor-based for operational queues; page-based only where stable lists make it appropriate
- Mutating offline-capable requests require `Idempotency-Key`
- Mutable aggregates use an `ETag`/version value and `If-Match` for conflict detection
- Public endpoints expose dedicated public response models

The API must not serialize persistence entities directly.

## 2. Standard responses

### Success envelope

```json
{
  "data": {},
  "meta": {
    "requestId": "uuid"
  }
}
```

### Collection envelope

```json
{
  "data": [],
  "meta": {
    "requestId": "uuid",
    "nextCursor": null,
    "total": 0
  }
}
```

### Error envelope

```json
{
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "The request could not be accepted.",
    "fieldErrors": [
      { "field": "answers.PLASTIC_KG_WEEK", "code": "REQUIRED", "message": "A value is required." }
    ],
    "requestId": "uuid"
  }
}
```

Important status codes:

- `400` malformed request
- `401` unauthenticated
- `403` authenticated but not authorized/in scope
- `404` resource unavailable to caller
- `409` invalid transition, duplicate business record, or synchronization conflict
- `412` stale `If-Match` version
- `422` valid JSON that violates business validation
- `429` rate limited

## 3. Authentication and current user

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/auth/login` | Authenticate user |
| POST | `/auth/refresh` | Rotate/refresh session tokens |
| POST | `/auth/logout` | Revoke current refresh session |
| POST | `/auth/forgot-password` | Start password recovery |
| POST | `/auth/reset-password` | Complete password recovery |
| GET | `/me` | Current user, roles, permissions, assignments, and context |

`GET /me` is the frontend's authority for navigation and scope. It returns no password or token internals.

The access token is held in frontend application memory and expires after approximately 15 minutes. The refresh token is delivered only in a Secure, HttpOnly, SameSite cookie, rotated on use, and represented by a hashed server-side refresh-session record. Logout revokes the session. Refresh-token reuse revokes the affected token family.

## 4. Users, roles, and assignments

| Method | Path | Permission |
| --- | --- | --- |
| GET | `/users` | Admin |
| POST | `/users` | Admin |
| GET | `/users/{userId}` | Admin; limited self via `/me` |
| PATCH | `/users/{userId}` | Admin |
| POST | `/users/{userId}/roles` | Admin |
| DELETE | `/users/{userId}/roles/{roleCode}` | Admin |
| GET | `/communities/{communityId}/assignments` | Admin, M&E read |
| POST | `/communities/{communityId}/assignments` | Admin |
| PATCH | `/assignments/{assignmentId}` | Admin |

Role and assignment mutations require an audit reason.

## 5. Communities and eligibility

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/communities` | Scoped community list |
| POST | `/communities` | Register internal community record (`registerCommunity`) |
| GET | `/communities/{communityId}` | Scoped community profile |
| PATCH | `/communities/{communityId}` | Edit permitted profile fields |
| GET | `/communities/{communityId}/history` | Lifecycle and audit history |
| POST | `/communities/{communityId}/eligibility-decisions` | Record eligibility decision |
| POST | `/communities/{communityId}/suspensions` | Suspend eligibility |
| POST | `/communities/{communityId}/reinstatements` | Reinstate eligibility |

Community list filters include lifecycle status, district, assignment, eligibility, and search term.

Initial registration request:

```json
{
  "name": "Example Community",
  "ward": "Ward 1",
  "chiefdom": "Example Chiefdom",
  "district": "Example District",
  "estimatedHouseholds": 450,
  "estimatedPopulation": 2300,
  "estimatedWomen": 1200,
  "estimatedYouth15To35": 700,
  "estimatedPersonsWithDisabilities": 85,
  "demographicsAsOfDate": "2026-08-17",
  "demographicsSource": "COMMUNITY_LEADER_ESTIMATE",
  "communityLeaderName": "Example Leader",
  "communityLeaderPhone": "+23200000000",
  "communityFocalPersonName": "Example Focal Person",
  "communityFocalPersonPhone": "+23200000000"
}
```

Only `name` is required for initial registration. The remaining values can be completed or corrected before baseline submission. The API normalizes phone numbers where possible and never returns contact fields through public community responses.

## 6. Baseline questionnaires and assessments

### Questionnaire administration

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/questionnaires/community-baseline/active` | Active published baseline definition |
| GET | `/questionnaires/{questionnaireId}/versions/{version}` | Exact version definition |
| POST | `/questionnaires` | Create questionnaire; Admin |
| POST | `/questionnaires/{questionnaireId}/versions` | Create draft version; Admin |
| PATCH | `/questionnaire-versions/{versionId}` | Edit draft definition; Admin |
| POST | `/questionnaire-versions/{versionId}/publish` | Publish immutable version; Admin |

### Baseline operations

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/baseline-assessments` | Scoped assessment/review list |
| POST | `/communities/{communityId}/baseline-assessments` | Start assessment |
| GET | `/baseline-assessments/{assessmentId}` | Assessment with permitted revision data |
| PATCH | `/baseline-assessments/{assessmentId}/draft` | Save current draft |
| POST | `/baseline-assessments/{assessmentId}/submit` | Lock and submit revision |
| POST | `/baseline-assessments/{assessmentId}/correction-revisions` | Open corrected draft |
| POST | `/baseline-assessments/{assessmentId}/decisions` | Verify, reject, or request correction |
| GET | `/baseline-assessments/{assessmentId}/history` | Revisions and decisions |

Draft save request shape:

```json
{
  "questionnaireVersionId": "uuid",
  "assessmentAt": "2026-08-16T17:58:40Z",
  "location": { "latitude": 8.48, "longitude": -13.23, "accuracyMeters": 15 },
  "answers": [
    {
      "questionCode": "PLASTIC_PER_WEEK",
      "decimalValue": 250,
      "unitCode": "KG",
      "measurementMethod": "ESTIMATED"
    },
    { "questionCode": "COMMUNITY_WILLING", "booleanValue": true }
  ]
}
```

Question definitions determine valid typed fields, options, and evidence requirements.

The exact initial question codes, conditions, validation, rubrics, and evidence requirements are defined in `baseline-questionnaire-v1.md`.

## 7. Competitions and participation

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/competitions` | Scoped competition list |
| POST | `/competitions` | Create competition; Admin |
| GET | `/competitions/{competitionId}` | Competition detail |
| PATCH | `/competitions/{competitionId}` | Edit draft configuration |
| POST | `/competitions/{competitionId}/publish` | Publish configuration |
| POST | `/competitions/{competitionId}/open` | Open reporting |
| POST | `/competitions/{competitionId}/close` | Close reporting |
| POST | `/competitions/{competitionId}/finalize` | Finalize results |
| GET | `/competitions/{competitionId}/participants` | Participant list |
| POST | `/competitions/{competitionId}/participants` | Enroll eligible community |
| POST | `/participations/{participationId}/activate` | Activate enrollment |
| POST | `/participations/{participationId}/suspend` | Suspend participation |
| POST | `/participations/{participationId}/reinstate` | Reinstate participation |

## 8. Operational submissions

Use a common submission API with type-specific payload schemas.

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/submissions` | Scoped submissions or review queue |
| POST | `/participations/{participationId}/submissions` | Create draft |
| GET | `/submissions/{submissionId}` | Current permitted representation |
| PATCH | `/submissions/{submissionId}/draft` | Save draft |
| POST | `/submissions/{submissionId}/submit` | Validate and submit revision |
| POST | `/submissions/{submissionId}/correction-revisions` | Create corrected draft |
| POST | `/submissions/{submissionId}/withdraw` | Withdraw eligible draft/submission |
| GET | `/submissions/{submissionId}/history` | Revision and decision history |
| POST | `/submissions/{submissionId}/decisions` | Approve/reject/request correction |

Initial `type` values:

- `CLEANUP_ACTIVITY`
- `WASTE_COLLECTION`
- `WASTE_SEGREGATION`
- `WASTE_WEIGHING`
- `RECYCLING`
- `UPCYCLING`
- `WASTE_SALE`
- `PRODUCT`
- `ENTERPRISE`
- `FINANCIAL_OUTCOME`
- `JOBS_AND_PARTICIPATION`
- `INNOVATION`
- `SUSTAINABILITY_ACTIVITY`

The API returns a schema/configuration reference so forms and evidence policies can evolve without silently changing submitted revisions. The initial field contracts, evidence policies, record linkages, and verified-fact outputs are defined in `reporting-forms-v1.md`.

## 9. Evidence upload

Evidence uses a staged upload process suitable for object storage and offline retry.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/evidence/upload-sessions` | Request upload session/object target |
| PUT | Provider URL | Upload bytes directly where supported |
| POST | `/evidence/{evidenceId}/complete` | Confirm upload and begin validation |
| GET | `/evidence/{evidenceId}` | Metadata and access status |
| POST | `/baseline-assessments/{id}/evidence-links` | Link evidence to baseline revision |
| POST | `/submissions/{id}/evidence-links` | Link evidence to submission revision |
| DELETE | `/evidence-links/{linkId}` | Remove link from editable draft only |

Evidence access endpoints return short-lived authorized URLs rather than public storage paths.

## 10. Reference data

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/reference-data/waste-categories` | Active/configured categories |
| GET | `/reference-data/measurement-units` | Units and conversions |
| GET | `/reference-data/submission-schemas` | Form and evidence policies |
| GET | `/reference-data/districts` | Geographic reference data |
| GET | `/sync/bootstrap` | Scoped offline reference bundle |

Reference responses include a version/ETag so clients can avoid unnecessary downloads.

## 11. Scoring, dashboards, and reports

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/competitions/{id}/scoring-rulesets` | Ruleset versions |
| POST | `/competitions/{id}/scoring-rulesets` | Create draft ruleset |
| PATCH | `/scoring-rulesets/{id}` | Edit draft ruleset |
| POST | `/scoring-rulesets/{id}/review` | Submit/recommend review |
| POST | `/scoring-rulesets/{id}/publish` | Publish ruleset; Admin |
| POST | `/competitions/{id}/score-calculations` | Start draft recalculation |
| GET | `/score-calculations/{id}` | Calculation progress/result |
| POST | `/score-snapshots/{id}/publish` | Publish approved snapshot |
| GET | `/dashboards/community` | Current user's community dashboard |
| GET | `/dashboards/field` | Assigned field workload |
| GET | `/dashboards/me` | M&E monitoring dashboard |
| GET | `/dashboards/admin` | Program dashboard |
| GET | `/reports` | Available scoped reports |
| POST | `/report-exports` | Generate authorized export |

Long-running calculations and exports return `202 Accepted` with a status resource.

## 12. Public API

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/public/competitions` | Published competitions |
| GET | `/public/competitions/{id}` | Public competition information |
| GET | `/public/competitions/{id}/leaderboard` | Published leaderboard snapshot |
| GET | `/public/communities` | Published active communities |
| GET | `/public/communities/{id}` | Approved public community profile |
| GET | `/public/competitions/{id}/awards` | Published awards |
| GET | `/public/impact` | Approved aggregate impact metrics |

Public endpoints are cacheable and exclude private evidence, contacts, buyer/payment data, costs, and detailed profit.

## 13. Synchronization endpoint

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/sync/operations` | Process ordered metadata operations |
| GET | `/sync/changes?cursor=...` | Pull permitted server changes |
| GET | `/sync/operations/{clientOperationId}` | Resolve uncertain operation outcome |

The sync contract is detailed in `offline-synchronization.md`.

## 14. API items still requiring refinement

- Search/filter vocabulary
- Export formats and retention
- Final Cloudflare R2 signed-upload/access details
- Rate limits
- Bulk Admin operations
- Notification delivery model
