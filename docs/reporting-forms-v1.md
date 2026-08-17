# Operational Reporting Forms — Version 1.0

## 1. Purpose

This specification defines the MVP forms used after an eligible community is activated in a competition. It establishes stable submission types, field codes, validation, evidence requirements, and the verified facts that approved revisions may produce.

These forms collect evidence; they do not calculate points. The scoring engine consumes traceable verified facts according to the published competition ruleset.

## 2. General submission rules

Every submission contains these server/domain fields:

| Code | Type | Required | Rule |
| --- | --- | --- | --- |
| `SUBMISSION_ID` | UUID | Yes | Client-generated when created offline |
| `SUBMISSION_TYPE` | Enum | Yes | One type defined in this document |
| `PARTICIPATION_ID` | UUID | Yes | Must be active and in caller scope |
| `COMMUNITY_ID` | UUID | Yes | Derived from participation; client cannot override |
| `REVISION_ID` | UUID | Yes | Unique immutable revision identifier |
| `REVISION_NUMBER` | Integer | Yes | Server-controlled sequence starting at 1 |
| `CREATED_BY` | User | Yes | Derived from authenticated user |
| `REPORTING_PERIOD_ID` | UUID | Yes | Must belong to the competition and accept the activity date |
| `ACTIVITY_OCCURRED_AT` | Timestamp | Yes | Offline capture allowed; cannot be unreasonably future-dated |
| `ACTIVITY_LOCATION` | Location | Conditional | Required for field activities; exceptions require reason |
| `SUMMARY` | Text | Yes | 5–200 characters |
| `NOTES` | Long text | No | Maximum 2,000 characters |
| `CLIENT_CAPTURED_AT` | Timestamp | Yes | Original client capture time |
| `SOURCE_CLIENT_ID` | UUID | Yes | Sync/audit metadata, not authorization |

General rules:

1. Quantities cannot be negative.
2. Money uses fixed-precision decimals and `SLE` initially.
3. Waste mass retains original value/unit and normalized kilograms.
4. `G`, `KG`, and `MT` normalize directly. Non-standard units cannot produce official mass facts without an approved conversion.
5. Submitted revisions are immutable.
6. A correction creates another revision of the same logical submission.
7. Evidence links target an exact revision.
8. The reviewer must be different from creator and submitter.
9. Approval creates verified facts; rejection creates none.
10. Superseding approval invalidates the prior revision's current facts without deleting history.

## 3. Duplicate and linkage controls

Waste often moves through several stages. Collection, weighing, segregation, sale, recycling, and upcycling records must be linkable without counting the same material as newly diverted at every stage.

### Waste batch

A `WASTE_BATCH_ID` identifies a logical quantity of material. It may be created by a collection or weighing submission and referenced by later submissions.

```text
Collection
→ Weighing
→ Segregation
→ Sale / Recycling / Upcycling
```

Rules:

- A later process references existing batches where feasible.
- Combining batches creates a batch-composition record.
- Splitting a batch records child quantities whose sum cannot exceed the verified available parent quantity.
- A sale/processing record cannot claim more verified material than its linked available batches unless independently justified and reviewed.
- “Waste collected,” “waste diverted,” “waste processed,” and “waste sold” are distinct facts.
- Scoring rules choose the appropriate fact and never sum incompatible lifecycle stages as though each were new waste.

Potential duplicates are flagged using community, date/time, category, quantity, location, evidence checksum, and linked batch identity. A duplicate warning requires resolution before approval.

## 4. `CLEANUP_ACTIVITY`

Use for organized clean-up work, not ordinary recurring collection.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `CLEANUP_TYPE` | Single select | Yes | `PUBLIC_SPACE`, `DRAINAGE`, `MARKET`, `SCHOOL`, `WATERWAY`, `HOTSPOT`, `OTHER` |
| `LOCATION_DESCRIPTION` | Text | Yes | 5–250 characters |
| `LINKED_HOTSPOT_ID` | UUID | Conditional | Required when targeting a registered hotspot where available |
| `STARTED_AT` | Timestamp | Yes | Must be before completion |
| `COMPLETED_AT` | Timestamp | Yes | Same day unless explanation supplied |
| `PARTICIPANT_TOTAL` | Integer | Yes | At least 1 |
| `WOMEN_PARTICIPANTS` | Integer | Yes | 0 through total |
| `YOUTH_PARTICIPANTS` | Integer | Yes | 0 through total |
| `PARTICIPANTS_WITH_DISABILITIES` | Integer | No | 0 through total; sensitive aggregation only |
| `GROUPS_INVOLVED` | Repeatable reference/text | No | Avoid unnecessary personal details |
| `WASTE_COLLECTED` | Boolean | Yes | Controls waste/batch fields |
| `RESULT_DESCRIPTION` | Long text | Yes | 10–1,000 characters |

Evidence:

- At least one before photo
- At least one after photo
- GPS location
- Participant support record where required by competition policy
- Waste weighing evidence only when a verified mass is claimed

Potential verified facts:

- Completed clean-up activity count
- Clean-up duration
- Participant totals by approved aggregate categories
- Hotspot clean-up occurrence
- Waste-collected mass only through a linked approved weighing/batch record

## 5. `WASTE_COLLECTION`

Use when waste is collected from households, public points, clean-ups, or other sources.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `COLLECTION_SOURCE` | Single select | Yes | `HOUSEHOLDS`, `COLLECTION_POINT`, `CLEANUP`, `MARKET`, `BUSINESS`, `HOTSPOT`, `OTHER` |
| `SOURCE_DESCRIPTION` | Text | Conditional | Required for Other or when source needs identification |
| `LINKED_CLEANUP_SUBMISSION_ID` | UUID | No | Prevents duplicate activity interpretation |
| `COLLECTION_METHOD` | Single select | Yes | Configured reference option |
| `DESTINATION_TYPE` | Single select | Yes | `SORTING_POINT`, `STORAGE`, `BUYER`, `RECYCLER`, `COMPOST`, `DISPOSAL`, `OTHER` |
| `DESTINATION_NAME` | Text | Yes | 2–200 characters |
| `MATERIAL_ENTRIES` | Repeatable measurement | Yes | At least one waste category and quantity |
| `QUANTITY_METHOD` | Single select | Yes | `WEIGHED`, `ESTIMATED`, `COUNTED_CONTAINER`, `REPORTED` |
| `TRANSPORT_NOTES` | Long text | No | Maximum 500 characters |

Each material entry stores waste category, original value/unit, normalized kg where valid, and a `WASTE_BATCH_ID`.

Evidence:

- Collection photo
- GPS
- Weighing evidence when method is Weighed
- Destination evidence when configured

Potential verified facts:

- Waste collected by category and normalized kg when evidence supports it
- Collection occurrence and source/destination
- Estimated quantities remain explicitly estimated and do not become official mass facts

## 6. `WASTE_WEIGHING`

Use to establish official mass for one or more existing/new waste batches.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `WEIGHING_LOCATION` | Text | Yes | 2–200 characters |
| `SCALE_TYPE` | Single select | Yes | `DIGITAL`, `MECHANICAL`, `VEHICLE_SCALE`, `OTHER` |
| `SCALE_IDENTIFIER` | Text | No | Equipment ID or description |
| `SCALE_ZERO_CONFIRMED` | Boolean | Yes | Must be Yes unless exception reviewed |
| `WEIGHING_ENTRIES` | Repeatable | Yes | At least one entry |
| `WEIGHED_BY_NAME` | Text | No | Operational/private when recorded |
| `WITNESS_NAME` | Text | No | Operational/private when recorded |

Each entry includes batch ID, waste category, gross kg where applicable, tare kg where applicable, net kg, and container description. The server validates `net = gross - tare` within configured rounding tolerance.

Evidence:

- Clear scale-reading photo for each entry or approved grouped record
- Weighing record/document when available
- GPS and capture timestamp

Potential verified facts:

- Official verified waste mass in kg by batch and category
- Evidence-complete weighing occurrence

## 7. `WASTE_SEGREGATION`

Use for sorting a linked mixed batch into categorized child batches.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `SOURCE_BATCH_IDS` | UUID list | Yes | At least one available batch |
| `SORTING_LOCATION` | Text | Yes | 2–200 characters |
| `OUTPUT_ENTRIES` | Repeatable measurement | Yes | At least two categories where source is mixed |
| `RESIDUAL_ENTRY` | Measurement | No | Non-recoverable/residual waste |
| `SORTING_METHOD` | Text | Yes | 2–500 characters |
| `PARTICIPANT_TOTAL` | Integer | No | Non-negative |

Output plus residual normalized mass cannot materially exceed verified input mass. Any loss, moisture change, or measurement variance beyond tolerance requires explanation and reviewer acceptance.

Evidence:

- Before and after/sorted-material photos
- Weighing evidence for official output mass
- GPS

Potential verified facts:

- Segregated mass by category
- Segregation rate when both verified input and output exist
- Residual mass and sorting occurrence

## 8. `WASTE_SALE`

Use when waste material is sold to a buyer. Product sales use the Product/Financial Outcome forms.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `SOURCE_BATCH_IDS` | UUID list | Yes | Available, compatible batches |
| `WASTE_CATEGORY_ID` | UUID | Yes | Must match linked material |
| `QUANTITY_SOLD` | Measurement | Yes | Positive; verified mass requires weighing evidence |
| `BUYER_NAME` | Text | Yes | Private operational data; 2–200 characters |
| `BUYER_TYPE` | Single select | Yes | `INDIVIDUAL`, `BUSINESS`, `RECYCLER`, `AGGREGATOR`, `OTHER` |
| `PRICE_PER_UNIT` | Money | No | Required when total is derived from unit price |
| `TOTAL_SALE_VALUE` | Money | Yes | Positive SLE value |
| `PAYMENT_STATUS` | Single select | Yes | `PENDING`, `PARTIAL`, `PAID` |
| `PAYMENT_METHOD` | Single select | No | Sensitive; configured options |
| `RECEIPT_REFERENCE` | Text | No | Private reference |

Evidence:

- Receipt, buyer confirmation, or approved sales record
- Quantity/weighing evidence
- Material photo where required

Potential verified facts:

- Waste sold kg by category
- Verified waste-sale revenue in SLE
- Buyer type in private analytical form

Public output may include aggregated verified revenue but never buyer/payment details.

## 9. `RECYCLING`

Use when waste is processed into reusable material through a recycling process.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `SOURCE_BATCH_IDS` | UUID list | Yes | Available input material |
| `PROCESSING_METHOD` | Text | Yes | 5–500 characters |
| `INPUT_ENTRIES` | Repeatable measurement | Yes | Linked to source batches |
| `OUTPUT_MATERIAL` | Text | Yes | 2–200 characters |
| `OUTPUT_QUANTITY` | Measurement | Yes | Positive |
| `RESIDUAL_QUANTITY` | Measurement | No | Non-negative |
| `PROCESSING_LOCATION` | Text | Yes | 2–200 characters |
| `PARTICIPANT_TOTAL` | Integer | No | Non-negative |
| `JOBS_SUPPORTED` | Integer | No | Must follow job-definition policy |

Input/output balance beyond configured tolerance requires an explanation.

Evidence:

- Input, process, and output photos
- Input/output weighing evidence for official mass
- GPS
- Supporting production record where available

Potential verified facts:

- Waste recycled kg
- Recycled output kg
- Recycling activity count
- Jobs are not verified solely from this field; they require linked job records

## 10. `UPCYCLING`

Use when waste is transformed into a product with retained or increased utility/value.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `SOURCE_BATCH_IDS` | UUID list | Yes | Available input material |
| `PROCESS_DESCRIPTION` | Long text | Yes | 10–1,000 characters |
| `INPUT_ENTRIES` | Repeatable measurement | Yes | Linked to source batches |
| `PRODUCT_ID` | UUID | Yes | Registered product |
| `QUANTITY_PRODUCED` | Decimal | Yes | Positive with product unit |
| `PRODUCTION_COST` | Money | No | Private financial data |
| `PRODUCTION_LOCATION` | Text | Yes | 2–200 characters |
| `PARTICIPANT_TOTAL` | Integer | No | Non-negative |

Evidence:

- Waste input, process, and finished-product photos
- Input weighing evidence for official diverted mass
- Production record where configured
- GPS

Potential verified facts:

- Waste upcycled kg
- Product units produced
- Upcycling activity count
- Production cost remains private and does not become revenue

## 11. `PRODUCT`

Use to register a reusable product definition. Production quantities belong to Upcycling/Recycling submissions; sales belong to Financial Outcome records.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `PRODUCT_NAME` | Text | Yes | Unique enough within community; 2–200 characters |
| `PRODUCT_DESCRIPTION` | Long text | Yes | 10–1,000 characters |
| `PRODUCT_CATEGORY` | Single select | Yes | Configurable reference |
| `PRIMARY_WASTE_CATEGORIES` | UUID list | Yes | At least one |
| `UNIT_OF_OUTPUT` | Text/reference | Yes | e.g. item, pack, kg |
| `ACTIVE_STATUS` | Boolean | Yes | Lifecycle control |
| `INNOVATION_LINK_ID` | UUID | No | Optional link to innovation submission |

Evidence:

- At least one representative product photograph

Potential verified facts:

- Active registered product count after reviewer approval

## 12. `ENTERPRISE`

Use to register/update a waste-based enterprise. Periodic financial and job results are reported separately.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `ENTERPRISE_NAME` | Text | Yes | 2–200 characters |
| `BUSINESS_TYPE` | Single select | Yes | Configurable waste-business types |
| `OWNER_GROUP_NAME` | Text | Yes | Private/internal unless approved public |
| `START_DATE` | Date | No | Not in future unless planned status |
| `BUSINESS_STATUS` | Single select | Yes | `PLANNED`, `STARTUP`, `ACTIVE`, `PAUSED`, `CLOSED` |
| `MEMBER_TOTAL` | Integer | Yes | Non-negative |
| `WOMEN_MEMBER_TOTAL` | Integer | Yes | 0 through member total |
| `YOUTH_MEMBER_TOTAL` | Integer | Yes | 0 through member total |
| `WOMEN_LED` | Boolean | Yes | Uses programme-approved definition |
| `YOUTH_LED` | Boolean | Yes | Uses programme-approved definition |
| `WASTE_CATEGORIES` | UUID list | Yes | At least one |
| `OPERATING_LOCATION` | Location/text | Yes | Precise location private by default |

Evidence:

- Enterprise/activity photograph
- Registration/supporting record where available
- Ownership/leadership confirmation for leadership claims

Potential verified facts:

- Active verified enterprise count
- Women-led/youth-led enterprise count
- Member aggregates

## 13. `FINANCIAL_OUTCOME`

Use for periodic enterprise/product revenue and cost reporting that is not already fully represented by a single waste sale.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `ENTERPRISE_ID` | UUID | Conditional | Enterprise or product context required |
| `PRODUCT_ID` | UUID | Conditional | Enterprise or product context required |
| `PERIOD_START` | Date | Yes | Before/equal period end |
| `PERIOD_END` | Date | Yes | Within competition/reporting rules |
| `REVENUE_TOTAL` | Money | Yes | Non-negative SLE |
| `COST_TOTAL` | Money | No | Non-negative SLE; private |
| `UNITS_SOLD` | Decimal | No | Non-negative; product unit required |
| `PROFIT_CALCULATION_CONFIRMED` | Boolean | Conditional | Required when both revenue and cost provided |
| `FINANCIAL_NOTES` | Long text | No | Maximum 1,000 characters; private |

The server may calculate estimated profit as revenue minus cost but stores source amounts and calculation version. Overlapping periods for the same enterprise/product trigger duplicate review.

Evidence:

- Sales summary, receipts, ledger, payment confirmations, or approved equivalent
- Evidence policy may require samples plus summary rather than exposing all customer data

Potential verified facts:

- Verified revenue in SLE
- Verified costs in SLE, private
- Calculated profit in SLE, private by default
- Product units sold

Waste-sale revenue linked into a financial period must be marked as included so it is not counted twice in aggregate revenue.

## 14. `JOBS_AND_PARTICIPATION`

Use to report periodic employment and participation outcomes linked to activities or enterprises.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `ENTERPRISE_ID` | UUID | No | Required for enterprise jobs |
| `PERIOD_START` | Date | Yes | Valid reporting period |
| `PERIOD_END` | Date | Yes | On/after start |
| `JOB_RECORDS` | Repeatable aggregate | No | At least jobs or participation required |
| `PARTICIPATION_RECORDS` | Repeatable aggregate | No | At least jobs or participation required |
| `COUNTING_METHOD` | Single select | Yes | `REGISTER`, `ATTENDANCE`, `PAYROLL`, `ESTIMATE`, `OTHER` |

Job aggregates distinguish:

- New versus existing/supported jobs
- Full-time, part-time, temporary, or self-employment
- Women and youth counts
- Reporting-period active status

Participation aggregates distinguish activities and avoid adding the same person repeatedly where a unique-participant metric is required. The system stores aggregate counts by default; names are collected only when an approved evidence policy requires a private register.

Evidence:

- Payroll, roster, signed/verified register, attendance summary, contract, or approved confirmation
- Estimated counts remain estimates and do not become verified job facts unless the indicator permits them

Potential verified facts:

- Green jobs created/supported by type
- Women/youth job aggregates
- Activity participation and unique participation where supported

## 15. `INNOVATION`

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `INNOVATION_NAME` | Text | Yes | 2–200 characters |
| `INNOVATION_TYPE` | Single select | Yes | `PRODUCT`, `COLLECTION_MODEL`, `RECYCLING_CENTRE`, `COMPOST`, `AGGREGATION`, `CONSTRUCTION_MATERIAL`, `PROCESS`, `OTHER` |
| `PROBLEM_ADDRESSED` | Long text | Yes | 10–1,000 characters |
| `SOLUTION_DESCRIPTION` | Long text | Yes | 20–2,000 characters |
| `IMPLEMENTATION_STATUS` | Single select | Yes | `IDEA`, `PILOT`, `ACTIVE`, `SCALED` |
| `IMPLEMENTED_AT` | Date | Conditional | Required for Pilot/Active/Scaled |
| `BENEFICIARY_DESCRIPTION` | Long text | No | Avoid unnecessary personal data |
| `RESULTS_TO_DATE` | Long text | Conditional | Required beyond Idea |

Evidence:

- Representative photos/documents
- Demonstration or implementation evidence beyond Idea status

Potential verified facts:

- Innovation count by verified implementation status
- Rubric input; a reviewer rubric is required before innovation points are awarded

## 16. `SUSTAINABILITY_ACTIVITY`

Use for maintenance and continuity actions not better represented by another form.

| Code | Type | Required | Validation |
| --- | --- | --- | --- |
| `ACTIVITY_TYPE` | Single select | Yes | `MAINTENANCE`, `MONITORING`, `EDUCATION`, `HOTSPOT_FOLLOWUP`, `INFRASTRUCTURE`, `OTHER` |
| `LINKED_PRIOR_SUBMISSION_ID` | UUID | No | Supports continuity traceability |
| `LOCATION_DESCRIPTION` | Text | Yes | 2–250 characters |
| `ACTION_DESCRIPTION` | Long text | Yes | 10–1,000 characters |
| `CONDITION_BEFORE` | Long text | No | Maximum 1,000 characters |
| `CONDITION_AFTER` | Long text | Yes | 10–1,000 characters |
| `NEXT_ACTION_DUE` | Date | No | Future follow-up date |

Evidence:

- At least one relevant photograph
- Before/after evidence when claiming condition improvement
- GPS for location-specific activity

Potential verified facts:

- Sustainability/maintenance activity occurrence
- Continuity across reporting periods
- Hotspot follow-up occurrence
- Qualitative rubric input after M&E assessment

## 17. Evidence purpose codes

Initial stable purposes:

- `ACTIVITY_BEFORE_PHOTO`
- `ACTIVITY_AFTER_PHOTO`
- `COLLECTION_PHOTO`
- `SORTED_MATERIAL_PHOTO`
- `SCALE_READING_PHOTO`
- `WEIGHING_DOCUMENT`
- `RECEIPT`
- `BUYER_CONFIRMATION`
- `PAYMENT_CONFIRMATION`
- `PROCESS_PHOTO`
- `PRODUCT_PHOTO`
- `ENTERPRISE_PHOTO`
- `REGISTRATION_DOCUMENT`
- `PARTICIPATION_REGISTER`
- `JOB_SUPPORT_DOCUMENT`
- `FINANCIAL_SUMMARY`
- `INNOVATION_DOCUMENT`
- `OTHER_SUPPORTING_DOCUMENT`

Evidence policies are versioned with form schemas. Sensitive purposes never become public automatically.

## 18. Verification checklist common to all forms

Reviewers assess:

- Submitter and community scope valid
- Activity occurred within the competition/reporting period
- Required fields complete
- Required evidence present and credible
- GPS/time metadata reasonable where required
- Quantities and units internally consistent
- Links to batches, submissions, products, or enterprises valid
- Duplicate warning resolved
- Participant subtotals do not exceed totals
- Money calculations internally consistent
- Sensitive data appropriately classified
- Previous correction items resolved
- Claimed facts supported by evidence

Type-specific verification adds the checks described in each section.

## 19. Public/private treatment

Public responses may include approved aggregates such as:

- Verified waste collected/diverted
- Clean-up count
- Participation aggregates
- Product/enterprise count
- Aggregated verified revenue
- Green-job aggregates
- Published innovations

Private by default:

- Buyer identity and payment details
- Receipts and financial documents
- Costs and detailed profit
- Personal phone/contact data
- Participant/job registers containing names
- Exact sensitive locations
- Internal reviewer notes

## 20. Items to validate during pilot

- Whether separate collection and weighing forms are practical in the field
- Ability to maintain waste-batch linkage
- Appropriate mass-balance tolerance
- Evidence burden on low-connectivity devices
- Reliable definition of unique participants and green jobs
- Whether financial reporting periods should be monthly or competition-defined
- Whether participant registers can be collected safely and consistently
- Which fields need Krio/local-language guidance
- Average completion time per submission type
- Duplicate-detection false positives

Pilot findings create a new form-schema version rather than altering approved historical revisions.
