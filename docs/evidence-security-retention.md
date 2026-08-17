# Evidence, Security, Privacy, and Retention Policy

## 1. Scope

This policy covers accounts, community records, GPS, photographs, receipts, weighing records, financial documents, verification comments, audit history, exports, browser offline data, PostgreSQL, and Cloudflare R2 evidence objects.

It is an engineering baseline, not a substitute for organizational/legal approval. Final retention durations and consent wording must be approved by eWomen before production data collection.

## 2. Data classification

| Classification | Examples | Default access |
| --- | --- | --- |
| Public | Published community profile, leaderboard, awards, approved aggregate impact | Anyone |
| Internal | Baseline answers, operational summaries, non-sensitive review status | Authorized programme users |
| Restricted personal | Names, phone numbers, consent records, user assignments | Need-to-know authorized users |
| Restricted financial | Buyer/payment details, receipts, costs, profit, enterprise records | Admin, M&E, specifically authorized scoped users |
| Restricted evidence | Photos, documents, precise GPS, internal reviewer comments | Authorized and scoped users only |
| Security-sensitive | Password hashes, token/session records, signing keys, storage credentials | System/security administrators only |

Data is private/internal unless explicitly mapped into a public response and approved for publication.

## 3. Collection principles

- Collect only information required for programme operations, verification, scoring, safeguarding, or approved reporting.
- Avoid participant names when aggregate counts are sufficient.
- Do not collect government IDs in the MVP.
- Do not collect signatures until an approved need, consent process, and retention policy exist.
- Explain why contact details, GPS, and photographs are collected.
- Record focal-person and commitment-confirmer consent.
- Allow assessors to record that evidence could not be safely captured and route the exception for review.
- Never require a person to fabricate a quantity or response.

## 4. Authentication and account security

- Passwords use Spring Security's approved adaptive password encoder configuration.
- JWT access tokens expire after approximately 15 minutes.
- Access tokens remain in frontend application memory.
- Opaque refresh tokens are stored only in Secure, HttpOnly, SameSite cookies.
- PostgreSQL stores only hashed refresh-token/session material.
- Refresh rotation and reuse detection revoke compromised token families.
- Login, refresh, password recovery, and sensitive endpoints are rate limited.
- Role, assignment, password, or suspected-compromise events can revoke sessions.
- Administrative accounts should use stronger authentication controls; MFA is a production-readiness target.

## 5. Authorization

- The API enforces role, assignment, community, competition, workflow state, ownership, and separation of duties.
- Public endpoints use dedicated response models.
- Evidence access is authorized on every request.
- Exports apply the same or stricter permissions as interactive views.
- Admin access does not remove separation-of-duty rules.
- Sensitive access and high-impact decisions are audited.

## 6. Evidence storage architecture

- Production evidence resides in a private Cloudflare R2 bucket.
- Staging and production use separate buckets and credentials.
- R2 access keys are scoped to the minimum required bucket/actions.
- Object keys use non-guessable UUIDs and contain no unnecessary personal information.
- PostgreSQL stores object key, type, size, checksum, owner/context, sensitivity, validation, and retention status.
- Large evidence bytes are not stored in PostgreSQL.
- Files are accessed through short-lived signed authorization or a policy-controlled API stream.
- The bucket is never configured for general public listing/access.

## 7. Upload policy

Initial allowed formats:

- JPEG (`image/jpeg`)
- PNG (`image/png`)
- WebP (`image/webp`)
- PDF (`application/pdf`)

Initial limits, subject to field testing:

- Image after client compression: 5 MB maximum
- PDF/document: 10 MB maximum
- Maximum evidence items per ordinary submission: 20 unless form policy permits more

Validation includes:

- File signature/type validation, not filename alone
- Size limit
- Checksum
- Safe generated object name
- Image decoding/sanity check where supported
- Malware scanning or quarantine integration before production acceptance
- Metadata minimization; unnecessary EXIF may be stripped while required capture/GPS is stored deliberately in controlled fields

Rejected/quarantined files do not satisfy evidence requirements.

## 8. Evidence access and publication

- Internal evidence is not public merely because its submission was approved.
- Public photographs require an explicit publication decision and appropriate consent/safeguarding review.
- Precise hotspot/community GPS is generalized or withheld publicly when disclosure creates risk.
- Signed URLs have short expirations and are not stored as permanent application data.
- Sensitive downloads record actor, evidence, context, time, and purpose/reason where policy requires it.
- Public response caching must never cache authenticated evidence responses.

## 9. Privacy and safeguarding

- Avoid photographing identifiable children unless necessary and covered by approved safeguarding/consent procedures.
- Avoid publishing faces, phone numbers, payment information, or exact private locations by default.
- Assessor instructions must include safe photography and respectful data collection.
- Reports of unsafe or sensitive content are restricted and escalated.
- Free-text fields warn users not to enter unnecessary personal information.
- Data subject correction requests preserve audit history while correcting the operational representation.

## 10. Retention schedule framework

Final durations require organizational approval. The initial proposed schedule is:

| Data | Proposed retention trigger/duration | Action |
| --- | --- | --- |
| Unsubmitted local drafts | 30 days after last edit, with user warning | Delete locally unless pending support/recovery |
| Successfully synced local evidence | 7 days after confirmed upload, configurable | Remove local bytes after integrity confirmation |
| Abandoned server drafts | 90 days after last activity | Notify/expire under controlled process |
| Rejected/quarantined upload bytes | 30 days after final decision | Delete object; retain safe audit metadata |
| Baselines and operational evidence | Competition/project life plus approved audit period | Archive, anonymize, or delete according to policy |
| Financial evidence | Approved finance/donor retention period | Restricted archive then secure deletion |
| Published score snapshots | Long-term programme record | Retain with traceability |
| Audit events | Approved audit/security period | Archive with integrity protection |
| Refresh sessions | Expiry/revocation plus short security window | Delete/token-minimize; retain safe security event |
| Generated exports | 24 hours by default | Delete generated file automatically |
| Application logs | 30–90 days depending environment | Rotate/delete; no sensitive payloads |

Until approved, production collection must not represent these proposed durations as final policy.

## 11. Deletion and anonymization

- Operational records with audit significance are not hard-deleted through ordinary UI actions.
- Retention processing identifies exact database records and R2 objects before deletion.
- Deletion is idempotent and produces an audit result.
- Failed object deletion stops final retention completion and raises an operational alert.
- Where history must remain, personal fields may be anonymized while non-personal decision history is retained.
- Database backup expiry is considered; deletion cannot promise immediate removal from already protected backups.

## 12. Encryption and transport

- All browser, API, PostgreSQL, and R2 network communication uses TLS.
- Managed provider encryption at rest is enabled where available.
- Application secrets are stored in provider secret management/environment configuration, never source control.
- Signing keys and storage credentials are rotated under an operational procedure.
- Local offline data is minimized because browser storage cannot be treated as equivalent to managed encrypted server storage.

## 13. Logging and audit safety

Never log:

- Passwords or reset secrets
- Access/refresh tokens
- R2 secret keys or signed URL query strings
- Evidence bytes
- Full financial/payment payloads
- Phone numbers unless masked and operationally essential
- Sensitive free-text contents by default

Audit events identify actor, action, target, time, reason, state transition, and request correlation ID without duplicating sensitive payloads.

## 14. Backups and recovery

- Render PostgreSQL backup and point-in-time recovery capabilities must be configured for the selected production plan.
- R2 evidence durability does not replace metadata/link recovery planning.
- Database and object-inventory reconciliation is tested.
- Restore tests occur before launch and periodically thereafter.
- Recovery access is restricted and audited.
- Staging restore tests use sanitized data when practical.

## 15. Incident response minimum

The operational owner must be able to:

1. Revoke a user or all user sessions.
2. Rotate JWT/storage/database credentials.
3. Disable evidence access or public publication.
4. Identify affected records through audit/correlation IDs.
5. Preserve necessary incident evidence safely.
6. Notify organizational decision-makers.
7. Document containment, recovery, and corrective action.

## 16. Approval items before production

- Final consent wording and safeguarding procedure
- Data controller/operational owner
- Final retention durations
- Donor, financial, and legal record requirements
- Data-location requirements
- MFA requirement and rollout
- Malware-scanning mechanism
- Production backup plan and recovery objectives
- Incident contacts and escalation path
- Account ownership for Render and Cloudflare

