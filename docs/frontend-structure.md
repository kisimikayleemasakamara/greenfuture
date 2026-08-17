# Frontend Screen and Navigation Structure

## 1. Frontend responsibility

The `greenfuture-frontend` repository will contain one React + TypeScript + Vite PWA. It serves mobile field/community workflows and responsive desktop Admin/M&E workflows against the Spring Boot API. Its production build will be deployed as a Render Static Site, while Vercel provides branch and commit preview deployments during development.

The frontend does not decide authorization, verification validity, eligibility, or scores. It reflects permissions and results returned by the API.

## 2. Application areas

```text
Public portal
Authenticated application
├── Community workspace
├── Field workspace
├── M&E workspace
├── Admin workspace
└── Shared account, evidence, notifications, and synchronization UI
```

Users with multiple roles may switch workspace, but record context and server authorization remain authoritative.

## 3. Public portal

| Route | Screen |
| --- | --- |
| `/` | Landing page and approved impact summary |
| `/communities` | Published participating communities |
| `/communities/:communityId` | Public community profile |
| `/competitions` | Published competitions |
| `/competitions/:competitionId` | Competition overview |
| `/competitions/:competitionId/leaderboard` | Published leaderboard |
| `/competitions/:competitionId/awards` | Published awards |
| `/about` | Program information |
| `/login` | Authentication entry |

Public pages show only published API data. Financial information is aggregated.

## 4. Shared authenticated screens

| Route | Screen |
| --- | --- |
| `/app` | Role-aware landing/redirect |
| `/app/select-workspace` | Workspace selector for multi-role users |
| `/app/profile` | User profile and security settings |
| `/app/notifications` | Action and status notifications |
| `/app/sync` | Offline queue, failures, and conflicts |
| `/app/unauthorized` | Permission error with safe navigation |

The application shell shows connectivity and synchronization status at all times on mobile workflows.

## 5. Community Officer workspace

Base route: `/app/community`

Primary mobile navigation:

- Home
- New report
- Submissions
- Community
- More/Sync

| Route | Screen |
| --- | --- |
| `/app/community` | Dashboard with activation, reporting, verification, score, and impact summaries |
| `/app/community/profile` | Assigned community profile |
| `/app/community/reports/new` | Submission-type chooser |
| `/app/community/reports/new/:type` | Dynamic reporting form |
| `/app/community/submissions` | Drafts, pending, correction, approved, and rejected submissions |
| `/app/community/submissions/:id` | Submission detail, evidence, history, and next action |
| `/app/community/scores` | Published/detailed permitted score breakdown |
| `/app/community/impact` | Waste and Waste-to-Wealth metrics |

If the community is not active, the dashboard displays eligibility/activation status and hides competition-report creation.

## 6. Field Officer workspace

Base route: `/app/field`

Primary mobile navigation:

- Assignments
- Baselines
- Field reports
- Sync
- Profile

| Route | Screen |
| --- | --- |
| `/app/field` | Assigned workload and pending synchronization |
| `/app/field/communities` | Assigned communities |
| `/app/field/communities/:id` | Community field profile and available actions |
| `/app/field/baselines` | Baseline list by status |
| `/app/field/baselines/new/:communityId` | Versioned baseline questionnaire |
| `/app/field/baselines/:id` | Baseline detail/history/correction action |
| `/app/field/reports/new/:type` | Field submission form |
| `/app/field/submissions` | Field-created submissions |
| `/app/field/sync` | Queue and conflict resolution entry |

Baseline and field forms support autosaved offline drafts, GPS capture, camera/file evidence, and visible completion requirements.

## 7. M&E workspace

Base route: `/app/me`

Desktop sidebar:

- Overview
- Baseline reviews
- Submission reviews
- Communities
- Competition monitoring
- Scores
- Reports
- Audit

| Route | Screen |
| --- | --- |
| `/app/me` | Monitoring dashboard and workload |
| `/app/me/baselines` | Filterable baseline review queue |
| `/app/me/baselines/:id/review` | Answers, evidence, comparison, checklist, and decision |
| `/app/me/submissions` | Submission review queue |
| `/app/me/submissions/:id/review` | Revision/evidence review and decision |
| `/app/me/communities` | All-community monitoring table |
| `/app/me/communities/:id` | Internal community performance profile |
| `/app/me/competitions/:id` | Competition monitoring |
| `/app/me/scores/:competitionId` | Draft/published score analysis |
| `/app/me/reports` | Authorized reports and exports |
| `/app/me/audit` | Operational audit search |

Review screens must display creator/submitter identity and disable decisions when separation of duties fails.

## 8. Admin workspace

Base route: `/app/admin`

Desktop sidebar:

- Overview
- Users and roles
- Communities
- Baseline configuration
- Competitions
- Indicators and scoring
- Reference data
- Public content
- Reports
- Audit and system status

| Route | Screen |
| --- | --- |
| `/app/admin` | Program dashboard |
| `/app/admin/users` | User list, invitations, roles, and status |
| `/app/admin/users/:id` | User roles and community assignments |
| `/app/admin/communities` | Community management |
| `/app/admin/communities/:id` | Profile, lifecycle, eligibility, assignments, and history |
| `/app/admin/questionnaires` | Questionnaire/version list |
| `/app/admin/questionnaires/:id/versions/:version` | Draft questionnaire editor/read-only published version |
| `/app/admin/competitions` | Competition management |
| `/app/admin/competitions/:id` | Configuration, participants, dates, lifecycle, and publication |
| `/app/admin/scoring/:competitionId` | Ruleset editor and validation |
| `/app/admin/reference-data` | Waste categories, units, and geographic data |
| `/app/admin/public-content` | Public visibility/publication controls |
| `/app/admin/reports` | Program exports |
| `/app/admin/audit` | Full audit search |
| `/app/admin/system` | Sync failures, processing jobs, and service health summaries |

Destructive or high-impact actions require confirmation and an audit reason.

## 9. Baseline form structure

The initial questionnaire is rendered from the published API definition, grouped into steps:

1. Community and assessment details
2. Waste conditions
3. Participation and leadership
4. Operational readiness
5. Hotspots, GPS, and evidence
6. Commitments
7. Review and submit

Each step shows required completion, validation, save/sync status, and evidence upload state. The form stores stable question codes rather than depending on visible labels.

## 10. Common UI states

Every data screen must deliberately handle:

- Loading
- Empty
- Error with retry
- Unauthorized/out-of-scope
- Offline but locally available
- Offline and unavailable
- Pending synchronization
- Synchronization failed
- Conflict requiring resolution
- Stale server version
- Read-only due to workflow state

## 11. Responsive behavior

- Field and Community Officer workflows are designed for narrow Android screens first.
- Touch targets, form controls, camera actions, and progress indicators must be usable outdoors and with one hand where practical.
- Admin and M&E tables collapse to cards or focused detail views on small screens.
- Large review and scoring workflows prioritize tablet/desktop without becoming unusable on mobile.

## 12. Accessibility and language readiness

- Semantic landmarks and heading order
- Keyboard-accessible desktop workflows
- Visible focus and descriptive labels
- Error summaries plus field-level messages
- Sufficient contrast and non-color status cues
- Alternative text or captions for meaningful evidence where applicable
- UI text stored centrally so localization can be added without rewriting components
- Dates, units, and currency formatted consistently

## 13. Proposed frontend module organization

```text
src/
├── app/              application shell, providers, routing
├── auth/             login, session, guards
├── features/
│   ├── communities/
│   ├── baselines/
│   ├── submissions/
│   ├── evidence/
│   ├── verification/
│   ├── competitions/
│   ├── scoring/
│   ├── dashboards/
│   └── reports/
├── offline/          IndexedDB, queue, sync, conflicts
├── api/              generated/typed API client and errors
├── components/       shared accessible UI components
├── layouts/          public and role workspaces
└── utilities/        formatting and pure helpers
```

This is the intended implementation structure, not a request to reorganize the existing frontend before the architecture is approved.

## 14. Initial end-to-end journeys

The first implementation must automate/test:

1. Admin registers a community and assigns a Field Officer.
2. Field Officer completes a baseline offline and later synchronizes it.
3. M&E requests correction; Field Officer submits a new revision.
4. A different M&E/Admin verifies it; Admin grants eligibility.
5. Admin enrolls and activates the community in a competition.
6. Community Officer creates a waste report with evidence.
7. M&E approves the report and verified facts are created.
8. A score snapshot is calculated and later published.
9. The public leaderboard shows only the published result.
