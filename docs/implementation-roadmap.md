# Implementation Roadmap

## 1. Delivery principles

- Specifications are acceptance criteria.
- Each implementation branch starts from current `origin/main` after the documentation branch is reviewed/merged.
- Backend and frontend use separate repositories and corresponding feature branches where both are affected.
- Deliver vertical, testable workflows rather than disconnected entities/screens.
- Do not carry existing EcoTrike or citizen-reporting behavior into MVP scope accidentally.
- Preserve useful prototype code only when it conforms to the approved domain/security model.

## 2. Pre-implementation gate

Before application code:

- Review and approve planning documents
- Resolve material contradictions/open decisions for the first milestone
- Commit and push `docs/architecture-specification`
- Review/merge documentation into `main`
- Confirm both repositories are current with remote `main`
- Create implementation branches from `origin/main`

## 3. Milestone 1 — Foundation

Branches:

```text
greenfuture:          feature/foundation
greenfuture-frontend: feature/foundation
```

Backend deliverables:

- Environment-based configuration and secret placeholders
- Flyway integration and initial schema conventions
- UUID/audit base types
- Standard API success/error behavior and request IDs
- Authentication with short-lived JWT and rotating refresh sessions
- Multi-role authorization foundation
- Health/readiness endpoints
- Testcontainers integration test foundation
- R2 storage abstraction with local/test adapter

Frontend deliverables:

- TypeScript migration
- Feature-oriented project structure
- API client/error handling
- Authentication/session bootstrap
- Role-aware workspace shell
- TanStack Query and form/validation foundation
- IndexedDB and PWA shell foundation
- Testing stack and CI checks
- Vercel preview deployment configuration with environment-specific API base URL

Exit criteria:

- User can securely log in/logout/refresh session
- `/me` returns roles/assignments
- Protected/public routes behave correctly
- Empty-database Flyway migration passes
- Frontend builds as installable foundation PWA
- A frontend branch/commit preview is available on Vercel without exposing secrets

## 4. Milestone 2 — Communities and assignments

Branches: `feature/community-management`

- Community registration/profile
- Ward, chiefdom, district, household/population demographics, community leader, and focal-person contacts
- Geographic/reference data
- User management and multiple roles
- Field/Community Officer assignments
- Community lifecycle and audit history
- Scoped lists/details
- Admin and M&E community screens

Exit criteria:

- Admin registers a community and assigns officers
- Field Officer sees only assigned communities
- Community Officer context cannot be changed client-side

## 5. Milestone 3 — Baseline and eligibility

Branches: `feature/community-baseline`

- Questionnaire v1.0 seed/configuration and version enforcement
- Baseline draft/revision responses
- GPS/general/hotspot evidence
- Offline baseline save and synchronization
- Submission, review, correction, and independent verification
- Eligibility decision
- Full audit/history UI

Exit criteria:

- Critical baseline end-to-end journey passes, including offline capture and correction
- Self-verification is impossible
- Published questionnaire revisions are immutable
- Eligibility requires verified baseline and approved checklist

## 6. Milestone 4 — Competitions and activation

Branches: `feature/competition-management`

- Competition configuration/lifecycle
- Reporting periods
- Eligibility-aware enrollment
- Participation activation/suspension/completion
- Public/private visibility foundation
- Community dashboard activation behavior

Exit criteria:

- Eligible community can be activated for one competition
- Ineligible/inactive community cannot report or appear publicly

## 7. Milestone 5 — Core environmental reporting

Branches: `feature/environmental-reporting`

- Clean-up, collection, weighing, segregation, and sustainability forms
- Waste categories and units
- Waste-batch lineage and mass-balance controls
- Evidence policies
- Offline submission/evidence queues
- Submission list/detail/history

Exit criteria:

- Weighted batch can move through collection/segregation without duplicate mass
- Interrupted upload/sync resumes safely
- Submitted revisions are immutable

## 8. Milestone 6 — Waste-to-Wealth

Branches: `feature/waste-to-wealth`

- Waste sales
- Recycling and upcycling
- Products and enterprises
- Financial outcomes
- Jobs/participation
- Innovations
- Sensitive financial access and public aggregation boundaries

Exit criteria:

- Linked material/revenue cannot be double counted
- Private buyer/cost/payment data never appears publicly
- Verified economic facts remain traceable to evidence/revision

## 9. Milestone 7 — M&E verification

Branches: `feature/verification`

- Unified review queues
- Evidence access and checklists
- Approve/reject/correction workflows
- Duplicate warnings and batch-balance review
- Verified-fact materialization/invalidation
- Notifications and workload summaries

Exit criteria:

- Every form type follows exact revision/decision workflow
- Only approved facts become eligible for scoring
- Audit and separation of duties pass security tests

Some verification foundation exists earlier for baselines; this milestone generalizes it for all operational submissions.

## 10. Milestone 8 — Scoring and awards

Branches: `feature/scoring-engine`

Prerequisite: M&E approval of exact indicator formulas, targets, rubrics, periods, and missing-data policy.

- Indicator/ruleset configuration
- Ruleset validation/versioning/publication
- Verified-fact aggregation
- Score components/snapshots
- Ranking and tie-breakers
- Awards
- Calculation jobs and review/publication

Exit criteria:

- Known fixtures reproduce approved expected results
- Historical snapshots remain unchanged after new rules
- Equal-rank behavior and public publication are correct

## 11. Milestone 9 — Dashboards, public portal, and reports

Branches: `feature/dashboards-reports`

- Community, Field, M&E, and Admin dashboards
- Public community profiles
- Leaderboard and awards
- Approved aggregate impact and revenue
- CSV/JSON exports
- Accessibility and performance refinement

Exit criteria:

- Public responses contain no restricted data
- Dashboards reflect permissions and published/draft distinctions
- Export authorization matches interactive access

## 12. Milestone 10 — Pilot readiness

Branches: `release/pilot`

- Render staging environments and Cloudflare R2 staging bucket
- Production-like migrations and configuration
- Security review
- Backup and restore test
- Real-device offline/PWA testing
- Evidence volume and image-compression testing
- Monitoring and operational runbooks
- User training/support material
- Pilot with two or three communities

Exit criteria are defined in `test-strategy.md`.

## 13. Milestone 11 — Production rollout

Branches/tags follow the agreed release process.

- Organizational privacy/retention approvals
- Production Render/Cloudflare accounts and domains
- Production PostgreSQL/R2 configuration
- Secrets and least-privilege access
- Final migrations and smoke tests
- Monitoring/alerts and incident contacts
- Phased expansion toward approximately 20 communities
- Post-launch support and feedback cadence

## 14. Pull request expectations

Each PR includes:

- Linked requirement/workflow
- Clear scope and out-of-scope notes
- Migration/API compatibility implications
- Tests proportional to risk
- Security/privacy impact
- Screenshots for material frontend changes
- Offline behavior where relevant
- Documentation update when contract changes

Avoid large PRs spanning multiple milestones or unrelated refactoring.

## 15. Legacy prototype treatment

Before each feature:

1. Identify existing code that overlaps the approved requirement.
2. Test whether its behavior/security/data model conforms.
3. Retain/refactor only conforming parts.
4. Remove/deprecate out-of-scope endpoints through an explicit reviewed change.
5. Plan data migration if any real retained data exists.

The existing hard-coded scoring, single-role model, local public uploads, EcoTrike workflow, and citizen reports must not be treated as the target architecture.

## 16. Immediate next action after documentation approval

1. Commit the complete specification set on `docs/architecture-specification`.
2. Push the documentation branch and review it.
3. Merge approved documentation into `main`.
4. Create `feature/foundation` from updated `origin/main` in both repositories.
5. Implement Milestone 1 only after the branch and acceptance criteria are confirmed.
