# Community Baseline Questionnaire — Version 1.0

## 1. Purpose and status

This specification converts the approved initial baseline survey into an implementable questionnaire contract.

| Property | Value |
| --- | --- |
| Questionnaire code | `COMMUNITY_BASELINE` |
| Version | `1.0` |
| Initial status | Draft until formally published |
| Intended respondents | Authorized Admin, M&E, or assigned Field Officer |
| Primary purpose | Establish community conditions and support eligibility review |
| Competition scoring | Baseline answers do not directly award competition points |

Once published, version 1.0 is immutable. Any question, validation, option, or evidence-policy change creates another questionnaire version. Existing assessments remain tied to the exact version used.

## 2. Response types

| Type | Meaning |
| --- | --- |
| `TEXT` | Short single-line text |
| `LONG_TEXT` | Multi-line narrative |
| `INTEGER` | Whole number |
| `DECIMAL` | Fixed-precision number |
| `BOOLEAN` | Yes or No |
| `SINGLE_SELECT` | One configured option |
| `MULTI_SELECT` | Zero or more configured options |
| `RATING_1_5` | Integer from 1 through 5 with rubric |
| `DATE` | Calendar date |
| `PHONE` | Validated contact number stored as private data |
| `LOCATION` | Latitude, longitude, accuracy, and capture metadata |
| `MEASUREMENT` | Original decimal value/unit plus measurement method and normalized value where possible |

Evidence is represented through evidence links, not embedded inside answer values.

## 3. Global validation rules

1. Required visible questions must be answered before submission.
2. Hidden conditional questions are not required and must not retain stale answers when their controlling answer changes unless the user explicitly confirms preservation in a draft.
3. Counts and quantities cannot be negative.
4. Ratings must be whole numbers from 1 through 5.
5. Text is trimmed and subject to the specified maximum length.
6. GPS latitude is from -90 to 90 and longitude from -180 to 180.
7. GPS capture records accuracy in metres and whether the value was device-captured or manually positioned.
8. Manual location entry requires an assessor note.
9. “Other” options require an accompanying description.
10. Unknown values are represented explicitly; users must not enter fabricated zeroes.
11. Submission runs server-side validation against the questionnaire version, not only frontend validation.

## 4. Assessment metadata

These fields belong to the assessment aggregate rather than ordinary questionnaire answers.

| Code | Type | Required | Validation/behavior |
| --- | --- | --- | --- |
| `COMMUNITY_ID` | UUID reference | Yes | Community must exist and be within assessor scope |
| `QUESTIONNAIRE_VERSION_ID` | UUID reference | Yes | Must reference this published version |
| `ASSESSOR_ID` | Authenticated user | Yes | Derived by server; never accepted from user input |
| `ASSESSMENT_STARTED_AT` | Timestamp | Yes | Generated when draft begins |
| `ASSESSMENT_OCCURRED_AT` | Timestamp | Yes | Cannot be unreasonably in the future; offline capture allowed |
| `ASSESSMENT_LOCATION` | `LOCATION` | Yes | Device capture preferred; manual placement requires note |
| `ASSESSMENT_NOTES` | `LONG_TEXT` | No | Maximum 2,000 characters |

At least two general community photographs are required before submission. Evidence must include capture/upload metadata and be linked to the assessment revision with purpose `GENERAL_SITE_PHOTO`.

## 5. Section A — Community identity and contact

| Code | Prompt | Type | Required | Validation/behavior |
| --- | --- | --- | --- | --- |
| `COMMUNITY_NAME_CONFIRMED` | Is the registered community name correct? | `BOOLEAN` | Yes | If No, correction details are required |
| `COMMUNITY_NAME_CORRECTION` | Provide the correct community name | `TEXT` | Conditional | Visible when name is not confirmed; 2–150 characters |
| `COMMUNITY_BOUNDARY_DESCRIPTION` | Describe the community or its commonly understood boundaries | `LONG_TEXT` | Yes | 10–1,500 characters |
| `FOCAL_PERSON_NAME` | Community focal person | `TEXT` | Yes | 2–150 characters; private operational data |
| `FOCAL_PERSON_ROLE` | Focal person's role in the community | `TEXT` | Yes | 2–100 characters |
| `FOCAL_PERSON_PHONE` | Focal person's phone number | `PHONE` | Yes | Normalize where possible; private; never public |
| `FOCAL_PERSON_CONTACT_CONSENT` | Has the focal person agreed to this contact information being used for the programme? | `BOOLEAN` | Yes | Must be Yes for eligibility; consent time/assessor recorded |
| `ESTIMATED_POPULATION` | Estimated community population | `INTEGER` | No | 1–10,000,000; source required when supplied |
| `POPULATION_SOURCE` | Source of population estimate | `SINGLE_SELECT` | Conditional | Required when population supplied |
| `ESTIMATED_HOUSEHOLDS` | Estimated number of households | `INTEGER` | No | 1–2,000,000; source required when supplied |
| `HOUSEHOLDS_SOURCE` | Source of household estimate | `SINGLE_SELECT` | Conditional | Required when households supplied |

Population/household source options:

- `OFFICIAL_RECORD`
- `COMMUNITY_REGISTER`
- `LOCAL_AUTHORITY_ESTIMATE`
- `COMMUNITY_LEADER_ESTIMATE`
- `ASSESSOR_ESTIMATE`
- `OTHER`

If `OTHER` is selected, a source-description field is required.

## 6. Section B — Waste conditions and infrastructure

| Code | Prompt | Type | Required | Validation/behavior |
| --- | --- | --- | --- | --- |
| `PLASTIC_PER_WEEK_KNOWN` | Is an amount of plastic generated or collected per week known? | `BOOLEAN` | Yes | Controls quantity fields |
| `PLASTIC_PER_WEEK` | Plastic per week | `MEASUREMENT` | Conditional | Required when known; value greater than zero |
| `PLASTIC_QUANTITY_BASIS` | What does this amount represent? | `SINGLE_SELECT` | Conditional | `GENERATED`, `COLLECTED`, or `BOTH_ESTIMATE` |
| `PLASTIC_MEASUREMENT_METHOD` | How was the amount determined? | `SINGLE_SELECT` | Conditional | `WEIGHED`, `ESTIMATED`, or `REPORTED` |
| `PLASTIC_QUANTITY_SOURCE_NOTE` | Explain the source or method | `LONG_TEXT` | Conditional | Required for Estimated/Reported; 5–500 characters |
| `DUMPING_SITE_COUNT` | Number of known dumping sites | `INTEGER` | Yes | 0–1,000 |
| `FUNCTIONING_COLLECTION_POINT_COUNT` | Number of functioning collection points | `INTEGER` | Yes | 0–1,000; “functioning” uses field guidance |
| `POLLUTION_SEVERITY` | Current pollution severity | `RATING_1_5` | Yes | Severity rubric in section 12 |
| `OBSERVED_WASTE_CATEGORIES` | Waste categories observed | `MULTI_SELECT` | Yes | From active waste-category reference data; at least one |
| `EXISTING_COLLECTION_METHOD` | Existing waste collection method | `MULTI_SELECT` | Yes | At least one option, including `NONE` |
| `SEGREGATION_PRACTICE` | Is waste currently segregated? | `SINGLE_SELECT` | Yes | `NONE`, `PARTIAL`, or `ESTABLISHED` |
| `WEIGHING_EQUIPMENT_AVAILABLE` | Is functioning weighing equipment available? | `BOOLEAN` | Yes | If Yes, equipment details required |
| `WEIGHING_EQUIPMENT_DETAILS` | Describe the weighing equipment and access | `LONG_TEXT` | Conditional | 5–500 characters |
| `COLLECTION_POINT_ACCESSIBILITY` | How accessible are collection points? | `SINGLE_SELECT` | Conditional | Required when collection-point count > 0 |

Accepted mass units for `PLASTIC_PER_WEEK`:

- `G`
- `KG`
- `MT`
- `BAG`

Grams, kilograms, and metric tonnes are normalized to kilograms. `BAG` is retained without official kilogram normalization unless an approved conversion is linked. Baseline values do not become official scored quantities.

Collection-method options:

- `NONE`
- `DOOR_TO_DOOR`
- `COMMUNAL_COLLECTION_POINT`
- `PRIVATE_COLLECTOR`
- `MUNICIPAL_COLLECTION`
- `COMMUNITY_VOLUNTEERS`
- `BURNING`
- `BURIAL`
- `OPEN_DUMPING`
- `OTHER`

Collection-point accessibility options:

- `NOT_ACCESSIBLE`
- `DIFFICULT`
- `PARTLY_ACCESSIBLE`
- `ACCESSIBLE`

## 7. Section C — Hotspots and environmental observations

This section repeats once for every dumping site when `DUMPING_SITE_COUNT` is greater than zero. The number of hotspot records should normally match the declared count; a discrepancy requires an explanation.

| Code | Prompt | Type | Required | Validation/behavior |
| --- | --- | --- | --- | --- |
| `HOTSPOT_NAME` | Hotspot name or local identifier | `TEXT` | Yes | 2–150 characters |
| `HOTSPOT_LOCATION` | Hotspot GPS location | `LOCATION` | Yes | Device capture preferred |
| `HOTSPOT_DESCRIPTION` | Describe the hotspot | `LONG_TEXT` | Yes | 10–1,000 characters |
| `HOTSPOT_SEVERITY` | Hotspot severity | `RATING_1_5` | Yes | Uses pollution-severity rubric |
| `HOTSPOT_PRIMARY_WASTE` | Main waste categories at this hotspot | `MULTI_SELECT` | Yes | At least one category |
| `HOTSPOT_RECURRENCE` | How often does dumping recur? | `SINGLE_SELECT` | Yes | `UNKNOWN`, `OCCASIONAL`, `MONTHLY`, `WEEKLY`, `DAILY` |
| `HOTSPOT_NEAR_SENSITIVE_SITE` | Is it near a drain, waterway, school, market, clinic, or residence? | `BOOLEAN` | Yes | Sensitive-site types required when Yes |
| `HOTSPOT_SENSITIVE_SITE_TYPES` | Nearby sensitive sites | `MULTI_SELECT` | Conditional | At least one when Yes |

Each hotspot requires at least one photograph linked with purpose `HOTSPOT_PHOTO`. When the assessor cannot safely reach or photograph a hotspot, an exception reason is required and the assessment remains subject to reviewer acceptance.

## 8. Section D — Participation, leadership, and inclusion

| Code | Prompt | Type | Required | Validation/behavior |
| --- | --- | --- | --- | --- |
| `LEADERSHIP_READINESS` | Community leadership readiness | `RATING_1_5` | Yes | Rubric and assessor comment required |
| `WOMEN_PARTICIPATION` | Current participation of women | `RATING_1_5` | Yes | Rubric and assessor comment required |
| `YOUTH_PARTICIPATION` | Current participation of youth | `RATING_1_5` | Yes | Rubric and assessor comment required |
| `DISABILITY_INCLUSION` | Current inclusion of persons with disabilities | `RATING_1_5` | Yes | Rubric and assessor comment required |
| `STAKEHOLDER_POTENTIAL` | Potential for stakeholder partnership | `RATING_1_5` | Yes | Rubric and assessor comment required |
| `PARTICIPATING_GROUPS` | Existing community groups that may participate | Repeatable group | No | Name and type; contact details optional/private |
| `KNOWN_PARTICIPATION_BARRIERS` | Known barriers to inclusive participation | `MULTI_SELECT` | Yes | `NONE_KNOWN` permitted |
| `PARTICIPATION_BARRIER_NOTES` | Explain identified barriers | `LONG_TEXT` | Conditional | Required unless None Known; 5–1,000 characters |

Barrier options initially include:

- `NONE_KNOWN`
- `TIME_AVAILABILITY`
- `SAFETY`
- `PHYSICAL_ACCESSIBILITY`
- `COMMUNICATION`
- `SOCIAL_EXCLUSION`
- `FINANCIAL_COST`
- `TRANSPORT`
- `OTHER`

## 9. Section E — Operational and digital readiness

| Code | Prompt | Type | Required | Validation/behavior |
| --- | --- | --- | --- | --- |
| `DIGITAL_READINESS` | Digital reporting readiness | `RATING_1_5` | Yes | Rubric and assessor comment required |
| `RECYCLING_POTENTIAL` | Recycling/upcycling potential | `RATING_1_5` | Yes | Rubric and assessor comment required |
| `CLEANUP_FREQUENCY` | Current community clean-up frequency | `SINGLE_SELECT` | Yes | Options below |
| `EXISTING_RECYCLING_ACTIVITY` | Does recycling currently take place? | `BOOLEAN` | Yes | Details required when Yes |
| `EXISTING_RECYCLING_DETAILS` | Describe current recycling | `LONG_TEXT` | Conditional | 10–1,000 characters |
| `EXISTING_UPCYCLING_ACTIVITY` | Does upcycling currently take place? | `BOOLEAN` | Yes | Details required when Yes |
| `EXISTING_UPCYCLING_DETAILS` | Describe current upcycling | `LONG_TEXT` | Conditional | 10–1,000 characters |
| `EXISTING_WASTE_ENTERPRISES` | Are there existing waste-based enterprises? | `BOOLEAN` | Yes | Count/details required when Yes |
| `EXISTING_WASTE_ENTERPRISE_COUNT` | Number of known waste-based enterprises | `INTEGER` | Conditional | 1–1,000 |
| `EXISTING_WASTE_ENTERPRISE_DETAILS` | Describe known enterprises | `LONG_TEXT` | Conditional | 10–1,500 characters; avoid unnecessary private financial data |
| `ECOTRIKE_SUITABILITY` | EcoTrike suitability | `RATING_1_5` | No | Future-use only; excluded from eligibility and MVP scoring |

Clean-up frequency options:

- `NEVER`
- `IRREGULAR`
- `QUARTERLY`
- `MONTHLY`
- `FORTNIGHTLY`
- `WEEKLY`
- `MORE_THAN_WEEKLY`
- `UNKNOWN`

## 10. Section F — Willingness and commitments

| Code | Prompt | Type | Required | Validation/behavior |
| --- | --- | --- | --- | --- |
| `COMMUNITY_WILLING` | Is the community willing to participate in the programme? | `BOOLEAN` | Yes | Must be Yes for eligibility, but No does not prevent assessment submission |
| `WILLINGNESS_EXPLANATION` | Explain the community's response | `LONG_TEXT` | Yes | 10–1,000 characters |
| `COMMUNITY_COMMITMENTS` | What commitments has the community agreed to make? | `LONG_TEXT` | Conditional | Required when willing; 20–2,000 characters |
| `COMMITMENT_CONFIRMED_BY` | Name of person confirming commitments | `TEXT` | Conditional | Required when willing; private operational data |
| `COMMITMENT_CONFIRMER_ROLE` | Role of person confirming commitments | `TEXT` | Conditional | Required when willing |
| `COMMITMENT_CONFIRMED_AT` | Date commitments were confirmed | `DATE` | Conditional | Required when willing; not in future |
| `COMMITMENT_CONFIRMATION_CONSENT` | Has the confirmer agreed that this confirmation may be recorded? | `BOOLEAN` | Conditional | Must be Yes for eligibility |

No signature image is required in version 1.0. If the programme later requires signatures, that change must be reviewed for consent, security, accessibility, and retention before a new questionnaire version is published.

## 11. Section G — Assessor review

| Code | Prompt | Type | Required | Validation/behavior |
| --- | --- | --- | --- | --- |
| `ASSESSOR_IDENTITY_CONFIRMED` | Community identity and location have been checked | `BOOLEAN` | Yes | Must be Yes to submit |
| `ASSESSOR_EVIDENCE_COMPLETE` | Required photographs, GPS, and conditional evidence are attached | `BOOLEAN` | Yes | Server also validates evidence independently |
| `ASSESSOR_INFORMATION_ACCURATE` | Information is accurate to the assessor's knowledge | `BOOLEAN` | Yes | Must be Yes to submit |
| `ASSESSOR_RECOMMENDATION` | Assessor's recommendation | `SINGLE_SELECT` | Yes | `PROCEED_TO_VERIFICATION`, `CORRECTION_BEFORE_REVIEW`, `NOT_READY` |
| `ASSESSOR_RECOMMENDATION_NOTES` | Explain the recommendation | `LONG_TEXT` | Yes | 10–1,000 characters |

The assessor recommendation does not determine eligibility and does not bind the independent reviewer.

## 12. Rating rubrics

Every rating requires an assessor comment. These descriptions provide the minimum consistent interpretation for version 1.0.

### Pollution severity

| Rating | Guidance |
| ---: | --- |
| 1 | Minimal visible waste; no significant persistent hotspot observed |
| 2 | Limited scattered waste or small localized concern |
| 3 | Moderate recurring waste across one or more locations |
| 4 | Severe widespread or persistent waste with clear environmental/health concern |
| 5 | Critical widespread accumulation or immediate serious environmental/health risk |

### Positive-capacity ratings

Leadership, participation, inclusion, stakeholder potential, digital readiness, recycling potential, and optional EcoTrike suitability use this direction:

| Rating | Guidance |
| ---: | --- |
| 1 | None or very poor; no credible current capacity observed |
| 2 | Limited or weak; early interest but substantial gaps |
| 3 | Moderate/basic; some capacity exists but support is required |
| 4 | Good/established; clear evidence of capacity and participation |
| 5 | Strong/advanced; sustained, organized, and well-supported capacity |

Question-specific prompts must guide the assessor to consider:

- Leadership: recognized coordination, accountability, and willingness to organize
- Women/youth participation: actual current participation, not only stated intention
- Disability inclusion: accessibility, representation, and meaningful participation
- Stakeholder potential: credible local authority, NGO, business, school, market, or group relationships
- Digital readiness: device access, connectivity, digital literacy, and evidence/reporting ability
- Recycling potential: available materials, skills, markets/partners, space, and demonstrated interest

## 13. Evidence policy

| Evidence purpose | Required when | Minimum |
| --- | --- | ---: |
| `GENERAL_SITE_PHOTO` | Every baseline | 2 photos |
| `ASSESSMENT_LOCATION` | Every baseline | 1 GPS capture |
| `HOTSPOT_PHOTO` | Every reported hotspot | 1 photo per hotspot |
| `HOTSPOT_LOCATION` | Every reported hotspot | 1 GPS capture per hotspot |
| `WEIGHING_EVIDENCE` | Plastic amount marked Weighed | 1 record/photo/document |
| `COLLECTION_POINT_PHOTO` | Collection-point count greater than zero | 1 representative photo |
| `OTHER_SUPPORTING_DOCUMENT` | Optional | Configurable |

Accepted initial evidence formats are JPEG, PNG, WebP, and PDF, subject to the final upload-size and validation policy. Evidence requirements may be waived only through a recorded exception reviewed by M&E/Admin; the waiver never fabricates evidence.

## 14. Submission completeness rules

The API permits submission only when:

1. All required visible answers pass validation.
2. Required assessment metadata is present.
3. Required evidence uploads are available and linked to this revision, or a permitted exception is recorded.
4. Repeatable hotspot records satisfy conditional rules.
5. Assessor declarations are complete.
6. The assessor remains authorized for the community.
7. The draft uses an accepted published questionnaire version.

Submission locks the revision. Corrections create a new revision; no submitted answers or evidence links are overwritten.

## 15. Independent verification checklist

The reviewer records each item as Pass, Fail, or Not Applicable with comments where required:

- Community identity confirmed
- Location/GPS credible
- Assessor authorized
- Required questions complete
- General photographs credible and relevant
- Declared hotspot count reconciled with hotspot records
- Hotspot GPS/photos credible or exception accepted
- Quantity method and units credible
- Weighing evidence present when quantity is marked Weighed
- Participation/readiness ratings supported by comments
- Willingness and commitments recorded
- Contact/confirmation consent recorded
- No obvious duplicate or conflicting active baseline
- Corrections from the previous review resolved

The reviewer must differ from the assessor and submitter.

## 16. Eligibility evaluation

After a baseline revision is verified, Admin may grant eligibility when:

- Identity and location checks passed
- Required evidence and any exceptions were accepted
- Community willingness is Yes
- Community commitments are complete
- Focal-person contact consent is Yes
- Commitment-confirmation consent is Yes
- No unresolved correction exists
- No active disqualifying integrity or participation issue exists

The following do not automatically make a community ineligible:

- High pollution severity
- Low digital readiness
- Low recycling potential
- No existing collection points
- No existing enterprise
- Unknown or estimated plastic quantity
- Low participation/readiness ratings

These describe the starting condition and may inform support plans.

## 17. Privacy classification

| Classification | Examples |
| --- | --- |
| Public only after approval | Community name/profile, general non-sensitive baseline summary |
| Internal operational | Assessment answers, ratings, assessor notes, hotspot details |
| Restricted personal | Focal person/confirmer names, phone number, consent records |
| Restricted evidence | Photographs, documents, precise GPS where publication could create risk |

Baseline data is internal by default. Public display requires explicit approved mapping to a public response model.

## 18. Version 1.0 items for field validation

The pilot should validate:

- Whether two general photos are sufficient
- Whether population/household estimates are useful and reliable
- Whether hotspot-per-record capture is practical
- Whether the rating guidance produces consistent assessments
- Whether clean-up frequency options match local practice
- Whether bag quantities should be retained or removed
- Whether disability-inclusion questions require more contextual guidance
- Whether focal-person consent can be captured reliably without signatures
- Average completion time, offline storage use, and evidence volume

Findings should produce version 1.1 or 2.0 rather than changing published version 1.0 in place.
