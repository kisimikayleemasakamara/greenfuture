# Roles and Permission Matrix

## 1. Authorization principles

1. A user may hold multiple roles; effective access is the union of allowed permissions unless a record-level rule denies the action.
2. The backend enforces all permissions and scope checks.
3. Admin and M&E have global community visibility by default.
4. Field Officers are limited to assigned communities unless explicitly granted broader access.
5. Community Officers are limited to their assigned community and active competition context.
6. Public access is read-only and limited to explicitly published data.
7. A creator or submitter cannot perform final verification on the same baseline or submission revision.
8. Suspended or disabled users cannot perform operational actions regardless of assigned roles.
9. Permission checks include role, community scope, competition scope, workflow state, ownership, and separation of duties.

## 2. Role matrix

Legend:

- **All** — allowed across all communities/competitions
- **Assigned** — allowed only for assigned communities
- **Own** — allowed only for the user's community or records
- **Conditional** — allowed when workflow and separation-of-duty rules pass
- **No** — not allowed

| Capability | Admin | M&E | Field Officer | Community Officer | Public |
| --- | --- | --- | --- | --- | --- |
| View internal communities | All | All | Assigned | Own | No |
| View published communities | All | All | Assigned | Own | Published only |
| Register community | All | All | Assigned/authorized | No | No |
| Edit community profile | All | All | Assigned | Limited own fields | No |
| Assign officers | All | No | No | No | No |
| Suspend/inactivate community | All | Recommend only | No | No | No |
| Create baseline draft | All | All | Assigned | No | No |
| Edit baseline draft | All | Own/authorized | Own/assigned | No | No |
| Submit baseline | All | Own/authorized | Own/assigned | No | No |
| Review baseline | Conditional | Conditional | No | No | No |
| Verify/reject baseline | Conditional | Conditional | No | No | No |
| Decide eligibility | Conditional | Recommend | No | No | No |
| Configure competitions | All | Read | No | No | Published read only |
| Enroll/activate community | All | Recommend | No | No | No |
| Create reporting draft | All | All | Assigned | Own | No |
| Edit reporting draft | All | Own/authorized | Own/assigned | Own | No |
| Submit report | All | Own/authorized | Own/assigned | Own | No |
| Review report/evidence | Conditional | Conditional | No | No | No |
| Approve/reject/request correction | Conditional | Conditional | No | No | No |
| View internal evidence | All | All | Assigned | Own submissions | No |
| Download sensitive evidence | Conditional | Conditional | Conditional | Conditional own | No |
| Configure waste categories | All | Read | Read | Read | No |
| Configure indicators/rules | All | Propose/review | No | No | Published read only |
| Publish scoring rules | All | No | No | No | No |
| Recalculate draft scores | All | All | No | No | No |
| Publish scores/leaderboard | All | Recommend | No | No | View only |
| View detailed scores | All | All | Assigned | Own | Published summary |
| View private financial details | All | All | Assigned when required | Own authorized | No |
| Export internal reports | All | All | Assigned | Own summary | No |
| Manage users and roles | All | No | No | No | No |
| View audit history | All | All operational | Own/assigned relevant | Own relevant | No |

## 3. Record-level rules

### Community Officers

- Must have one active Community Officer assignment for normal reporting.
- The API derives community context from the authenticated assignment.
- Cannot submit against another community by altering request data.
- May see activation status while their community is not active, but cannot use competition reporting tools.

### Field Officers

- May access only communities covered by an active assignment.
- May conduct and submit baselines for assigned communities.
- Cannot verify baselines or reports in the MVP.
- May create field reports and evidence for active assigned communities.

### M&E

- May see all communities, submissions, and competition performance.
- May review and decide baselines and submissions they did not create or submit.
- May propose scoring configurations but cannot publish a ruleset unless also holding Admin.
- May recommend eligibility, activation, suspension, and publication decisions.

### Admin

- Has global operational scope but remains subject to separation of duties.
- An Admin who created or submitted a baseline cannot verify that revision.
- Publishing scoring rules, leaderboard results, role changes, and community suspension requires an audit reason.

## 4. Sensitive-data controls

Sensitive fields include personal contact details, buyer details, payment evidence, costs, detailed profits, internal comments, and unpublished evidence.

- List endpoints must omit sensitive fields unless explicitly needed.
- Evidence downloads require a separate authorization check and short-lived access.
- Exports apply the same permissions as the corresponding screen/API.
- Public APIs use dedicated response models and never serialize internal entities directly.

## 5. Multiple-role resolution

Multiple roles grant the union of capabilities but do not bypass constraints. Examples:

- A Field Officer who is also M&E may review across communities, but cannot verify a baseline they created as Field Officer.
- A Community Officer who is also Field Officer may work in assigned communities only through Field Officer workflows; Community Officer reporting remains locked to their home community.
- An M&E user who is also Admin may publish scoring rules, but publication is recorded as an Admin action.

## 6. Audit requirements

Audit reasons are mandatory for:

- Role or assignment changes
- Verification, rejection, or correction decisions
- Eligibility decisions
- Competition enrollment, activation, suspension, or withdrawal
- Ruleset publication
- Manual score recalculation or publication
- Public/private visibility changes
- Sensitive evidence access where policy requires it
