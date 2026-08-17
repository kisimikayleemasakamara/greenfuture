# Competition Scoring Specification — Initial Design

## 1. Purpose

This document defines the scoring framework and calculation contract. Exact numeric thresholds and normalization targets remain configurable and must be approved with the project's M&E team before production scoring.

## 2. Governing principles

1. Only current, approved verified facts count.
2. Scoring is competition-specific and ruleset-versioned.
3. Published rulesets and historical score snapshots are immutable.
4. Each awarded point is traceable to an indicator rule and source verified facts.
5. Communities are compared over the same configured reporting period.
6. Missing data is not automatically zero unless the published indicator rule explicitly defines it as zero/non-compliance.
7. No unverified estimate contributes to official quantity scoring.
8. Category totals cannot exceed their published allocations.

## 3. Category allocation

| Code | Category | Maximum points |
| --- | --- | ---: |
| CLEAN | Cleanliness and sanitation | 25 |
| WASTE | Waste collection and segregation | 15 |
| W2W | Waste-to-Wealth and business creation | 25 |
| INCLUSION | Community participation and inclusion | 15 |
| SUSTAIN | Environmental sustainability | 10 |
| DIGITAL | Digital reporting and evidence | 10 |
|  | **Total** | **100** |

Indicator weights within a category must sum to that category's maximum before a ruleset can be published.

## 4. Indicator rule types

### Binary/compliance

Used for requirements with an objectively verified yes/no result.

```text
points = indicator_weight when compliant
points = 0 when non-compliant
```

An indicator may define partial compliance only by using a rubric rule instead.

### Quantitative normalized

Used for mass, frequency, revenue, jobs, participation, and similar measures.

Supported normalization methods include:

- Target attainment: `min(value / target, 1) × weight`
- Per-capita target attainment
- Percentage/rate attainment
- Peer percentile or relative-to-best comparison
- Improvement from verified baseline

Every quantitative rule defines measurement unit, aggregation method, period, cap, missing-data behavior, and normalization method.

Relative comparisons must handle very small samples and outliers. Production rules should prefer stable targets or improvement measures when credible targets/baselines exist.

### Qualitative rubric

Used for field-observed quality or sustainability assessments.

```text
points = (verified rubric level / maximum rubric level) × weight
```

Each rubric level requires explicit assessment criteria and evidence. A generic 1–5 opinion without guidance is insufficient for official scoring.

## 5. Proposed initial indicator structure

The following is a design starting point, not final production weights.

### CLEAN — 25 points

| Indicator | Suggested rule | Draft weight |
| --- | --- | ---: |
| Verified public-space cleanliness assessment | Rubric | 8 |
| Reduction of documented illegal-dumping hotspots | Baseline improvement | 6 |
| Verified clean-up activity completion | Target attainment | 5 |
| Drainage/sanitation condition | Rubric | 3 |
| Maintenance of cleaned areas | Rubric/continuity | 3 |

### WASTE — 15 points

| Indicator | Suggested rule | Draft weight |
| --- | --- | ---: |
| Verified waste collected by mass | Normalized kg or kg/capita | 5 |
| Verified segregation rate | Percentage | 4 |
| Regularity of collection | Target attainment | 2 |
| Functioning collection/sorting points | Rubric/compliance | 2 |
| Completeness of weighing evidence | Percentage/compliance | 2 |

### W2W — 25 points

| Indicator | Suggested rule | Draft weight |
| --- | --- | ---: |
| Verified waste sold/recycled/upcycled | Normalized kg | 6 |
| Verified products produced and sold | Quantitative | 4 |
| Verified aggregated revenue | Quantitative | 4 |
| Active waste-based enterprises | Quantitative/rubric | 3 |
| Verified green jobs | Quantitative | 3 |
| Women/youth-led enterprise participation | Percentage/rubric | 3 |
| Verified innovation | Rubric | 2 |

### INCLUSION — 15 points

| Indicator | Suggested rule | Draft weight |
| --- | --- | ---: |
| Verified community participation | Rate/target attainment | 5 |
| Women's participation | Percentage/target attainment | 3 |
| Youth participation | Percentage/target attainment | 3 |
| Disability-inclusive participation | Rubric/verified count | 2 |
| Community groups involved | Quantitative/rubric | 2 |

### SUSTAIN — 10 points

| Indicator | Suggested rule | Draft weight |
| --- | --- | ---: |
| Continuity of activities across periods | Compliance/quantitative | 4 |
| Maintenance of infrastructure/practices | Rubric | 3 |
| Sustained reduction in recurring dumping | Baseline improvement | 3 |

### DIGITAL — 10 points

| Indicator | Suggested rule | Draft weight |
| --- | --- | ---: |
| On-time reporting | Percentage | 3 |
| Reporting consistency | Percentage | 2 |
| Evidence completeness | Percentage | 3 |
| Submission accuracy/correction rate | Percentage | 2 |

The proposed weights total 100 and preserve the approved category allocations. M&E review must define exact formulas, targets, and rubrics before publication.

## 6. Aggregation and validity

- Each verified fact has a fact date/time and validity period.
- Rules define whether values sum, average, take the latest observation, or count distinct records.
- Superseded or invalidated facts are excluded from new calculations.
- Duplicate facts derived from the same logical submission are deduplicated by source identity/revision.
- Mass is normalized to kilograms before aggregation.
- Money is normalized to SLE using only an approved conversion policy if other currencies are later supported.
- Per-capita indicators require a verified population denominator and record which denominator/version was used.

## 7. Calculation output

Every score snapshot records:

- Competition and participation
- Reporting/calculation period
- Ruleset ID and version
- Indicator raw values
- Normalized values
- Indicator points
- Category subtotals
- Total score
- Verified-fact references
- Calculation timestamp and checksum
- Publication status and timestamps

Display rounding occurs after calculation. Internal calculations retain configured decimal precision. Ranking uses unrounded totals.

## 8. Minimum requirements and missing data

A ruleset may define minimum requirements such as:

- Active competition participation
- Minimum reporting coverage
- Required verified cleanliness assessment
- Mandatory weighing evidence for mass indicators
- No unresolved disqualifying compliance issue

Failure may make a community unranked rather than silently assigning an arbitrary score. The leaderboard must distinguish zero points, missing data, and not eligible/not ranked.

## 9. Ranking and tie-breaking

Communities are ordered by unrounded total score, then:

1. Higher unrounded W2W category score
2. Higher unrounded WASTE category score
3. Higher unrounded CLEAN category score
4. Higher verified waste diverted in normalized kilograms
5. Equal rank if still tied

Equal-ranked communities receive the same displayed rank; the next rank uses competition ranking semantics (for example, `1, 1, 3`).

## 10. Awards

Awards use published, verified score components or separately configured verified metrics. Each award records its rule version, calculation inputs, winner(s), approval, and publication status.

No award may use private unverified financial data. Public award descriptions expose only approved aggregate values.

## 11. Rule governance

Ruleset lifecycle:

```text
DRAFT → UNDER_REVIEW → PUBLISHED → RETIRED
```

- Admin creates and publishes rulesets.
- M&E reviews and recommends approval.
- Published rulesets are immutable.
- Material changes create a new version with an effective period.
- Retroactive recalculation creates new snapshots and never rewrites historical published results.
- Publication requires validation that category and total weights are correct and every indicator has a complete formula/evidence policy.

## 12. Items requiring M&E approval

- Final indicator list and weights
- Target values and normalization approach
- Population/per-capita use
- Exact 1–5 rubrics
- Reporting/calculation periods
- Missing-data and minimum-coverage rules
- Treatment of late approvals and corrections
- Outlier policy
- Award formulas
- Decimal precision and public rounding
