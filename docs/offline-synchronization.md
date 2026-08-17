# Offline Synchronization Protocol

## 1. Scope

Offline support covers mobile field/community workflows:

- Authentication bootstrap/session continuity within safe limits
- Assigned communities and reference data
- Baseline drafts
- Operational reporting drafts
- GPS and evidence capture
- Submission/revision operations
- Synchronization status and conflict resolution

Admin configuration, M&E decisions, eligibility decisions, scoring publication, and user/role administration require connectivity in the MVP.

## 2. Local storage model

IndexedDB stores:

| Store | Purpose |
| --- | --- |
| `session_context` | Minimal current-user roles, assignments, and expiry metadata |
| `reference_data` | Versioned questionnaire, waste categories, units, schemas, and districts |
| `server_records` | Read-through cache of scoped server records |
| `drafts` | Locally editable baseline/submission aggregates |
| `evidence_blobs` | Pending local evidence bytes and metadata |
| `sync_operations` | Durable ordered mutation queue |
| `sync_conflicts` | Preserved local/server versions and resolution status |
| `sync_state` | Pull cursor, last success, and bootstrap version |

Sensitive local data must be minimized. Logout clears locally cached protected data after warning about unsynchronized work.

## 3. Identifiers and versions

- The client generates UUIDv4/UUIDv7-compatible unique IDs for offline-created aggregates, revisions, evidence metadata, and operations.
- The server accepts these IDs after authorization and validation.
- Every mutable aggregate has a monotonically changing server `rowVersion` and ETag.
- Each queued mutation records the base server version observed when editing began.
- Every operation has a unique `clientOperationId` and `Idempotency-Key`.

Client identifiers establish identity; they do not prove authorization or ownership.

## 4. Draft ownership

- A new draft records creator user and originating client.
- One user/client is the active editor by default.
- Synced drafts may be opened elsewhere, but version checks apply.
- Submitted revisions are locked locally and remotely.
- Corrections create a new draft revision with a new revision ID.

## 5. Operation queue

An operation contains:

```json
{
  "clientOperationId": "uuid",
  "idempotencyKey": "uuid",
  "aggregateType": "BASELINE_ASSESSMENT",
  "aggregateId": "uuid",
  "operationType": "SAVE_DRAFT",
  "baseVersion": 4,
  "dependsOn": [],
  "payload": {},
  "createdAt": "2026-08-16T18:00:00Z",
  "attemptCount": 0,
  "status": "PENDING"
}
```

Queue rules:

1. Operations persist before the UI reports “Saved offline.”
2. Dependencies enforce creation before updates and evidence completion before submission.
3. Operations for one aggregate remain ordered.
4. Independent aggregate queues may synchronize concurrently within safe limits.
5. Completed operations remain briefly for troubleshooting, then are compacted.
6. Repeated draft saves may be safely coalesced only before synchronization and only when no dependent transition operation exists.

## 6. Synchronization sequence

```text
Connectivity detected/manual retry
→ Validate session
→ Refresh scoped reference data
→ Upload pending evidence
→ Push ordered metadata operations
→ Resolve uncertain outcomes by operation ID
→ Pull permitted server changes
→ Update local versions/cursor
→ Report final queue state
```

The client must not equate network connection with successful synchronization.

## 7. Evidence synchronization

1. Capture evidence into local IndexedDB with metadata and checksum.
2. Queue an upload-session operation.
3. Receive evidence ID and upload target.
4. Upload bytes with progress and retry support where provider permits.
5. Confirm completion with the API.
6. Wait for validation/availability.
7. Queue the evidence link to the editable revision.
8. Permit submission only when required evidence dependencies are satisfied.

Failed evidence remains locally available until uploaded or explicitly removed from an editable draft. Storage-pressure warnings must appear before the browser risks eviction.

## 8. Idempotency

- The server stores each idempotency key, authenticated user, request fingerprint, processing status, and result for the retention window.
- Retrying an identical completed operation returns the original result.
- Reusing a key with a different fingerprint returns `409 IDEMPOTENCY_KEY_REUSED`.
- An uncertain client timeout is resolved with `GET /sync/operations/{clientOperationId}` before creating a new operation.
- Create and transition operations are always idempotent.

## 9. Conflict detection

A conflict occurs when:

- `baseVersion`/`If-Match` is older than the current server version
- A workflow transition has already changed the record state
- Assignment or authorization changed while offline
- A questionnaire/schema version is no longer accepted for new drafts
- Another revision has superseded the editable revision

The server returns:

```json
{
  "error": {
    "code": "VERSION_CONFLICT",
    "message": "The record changed after this draft was opened.",
    "requestId": "uuid",
    "conflict": {
      "aggregateId": "uuid",
      "baseVersion": 4,
      "serverVersion": 6,
      "serverState": {},
      "conflictingFields": ["answers.COMMUNITY_COMMITMENTS"]
    }
  }
}
```

The client preserves:

- Local attempted version
- Server current version
- Base version where available
- Conflict metadata and timestamps

## 10. Conflict resolution

- Non-overlapping draft field changes may be merged only through an explicit user-confirmed resolution interface.
- Conflicting field values are shown side by side.
- Evidence lists are additive unless a user explicitly removes a link in an editable draft.
- Workflow-state conflicts cannot be auto-merged.
- Submitted/approved server revisions cannot be overwritten; the user may discard local changes or start an authorized correction revision.
- Lost authorization prevents push; local work remains exportable/recoverable according to policy until the user receives support.

No “last write wins” behavior is permitted for operational records.

## 11. Retry policy

Retryable:

- Network unavailable/interrupted
- Timeout with unresolved operation state
- `429` using server `Retry-After`
- Transient `5xx`
- Temporary storage-provider failure

Not automatically retryable:

- `400`, `403`, `404`, `409`, `412`, or `422`
- Expired/revoked session until reauthentication succeeds
- Rejected/quarantined evidence

Transient retries use capped exponential backoff with jitter. Manual “Retry now” is available without creating duplicate operations.

## 12. Bootstrap and pull synchronization

The bootstrap bundle contains only data authorized and needed for offline work:

- Current user/role/assignment summary
- Assigned community summaries
- Active questionnaire definitions needed for drafts
- Waste categories and units
- Submission schemas and evidence policies
- Competition participation context

Incremental pulls use an opaque server cursor. The server returns changes and tombstone/status events for records no longer available. A cursor is advanced only after the client transactionally applies the whole response.

## 13. Authentication and security offline

- Offline access never extends a server session indefinitely.
- The client records last successful authentication and enforces configured offline-duration limits.
- Reauthentication is required before pushing after expiry.
- Access tokens are held in application memory and are not stored in ordinary local storage. Rotating refresh tokens are held only in Secure, HttpOnly, SameSite cookies.
- Cached data is scoped to the authenticated user.
- Switching users clears or securely separates prior user data.
- Device loss and shared-device risks must be covered in deployment/training policy.

## 14. User experience

Global indicators:

- Offline
- Online, pending sync
- Synchronizing
- All changes synced
- Sync failed
- Conflict needs attention

Each draft/evidence item shows its own status. The UI gives actionable messages and never promises that a record is submitted or approved merely because it is saved locally.

Before logout, users with pending operations see the count and consequences. The application does not silently discard pending work.

## 15. Observability and support

The system records safe diagnostics:

- Client operation ID
- Request correlation ID
- Aggregate type/ID
- Attempt count and timestamps
- Result/error code
- Client application/schema version
- Queue age

Logs must not contain evidence bytes, passwords/tokens, full sensitive financial payloads, or unnecessary personal data.

## 16. Testing scenarios

Minimum automated/field scenarios:

1. Create a complete baseline without connectivity and synchronize later.
2. Interrupt evidence upload and resume/retry without duplication.
3. Retry a timed-out create whose server result was initially unknown.
4. Submit the same operation twice and receive one logical result.
5. Edit one draft on two devices and resolve the version conflict.
6. Lose community assignment while offline and receive a safe authorization failure.
7. Receive correction while another device has an old draft.
8. Run out of local storage during photo capture.
9. Close/reopen the browser with a pending queue.
10. Refresh reference data without corrupting existing questionnaire-version drafts.
11. Expire authentication before push and recover after login.
12. Synchronize on slow/intermittent mobile connectivity.

## 17. Open implementation decisions

- Final maximum offline authentication duration
- Maximum evidence size and client-side image compression policy
- Background sync support versus foreground-only fallback
- Queue concurrency and backoff limits
- Conflict-resolution ownership and support workflow
- Local-data retention after successful synchronization
- Browser/device support baseline
- Storage quota monitoring behavior
