# Iteration2 System Design - Version2 Development Plan

## 1. Document Purpose

This document defines a practical Version2 development plan based on:
- current implemented baseline in ta-webapp,
- project handout constraints (Servlet/JSP + text file storage),
- team MVP direction from Iteration1.

The plan focuses on two goals:
- complete missing core functions,
- improve maintainability through decoupled development.

Date: 2026-03-30

## 2. Current Gap Summary (From Version1 Baseline)

### 2.1 Functional gaps
1. TA cannot upload CV (PDF/DOC) yet.
2. TA profile cannot be edited after registration.
3. Hard business rules are incomplete:
   - max 3 applications per TA,
   - max 3 accepted jobs per TA.
4. MO application list is not restricted to MO-owned jobs.
5. MO cannot edit/close posted jobs.
6. Search/filter UI exists, but backend query logic is missing.
7. Admin workload view is only application-count based (not accepted-hours or overload warning).

### 2.2 Engineering gaps
1. Role checks are duplicated in many servlets.
2. Controller logic and validation rules are still relatively coupled.
3. Domain rules are scattered; lacking central policy methods.
4. File storage is usable, but lacks stronger abstraction boundary for future extension.

## 3. Version2 Scope and Objectives

### 3.1 Primary objective
Deliver a complete and testable recruitment workflow for TA/MO/Admin under coursework constraints.

### 3.2 Scope priority
- Must Have:
  1. CV upload and reference persistence.
  2. Apply-limit and accept-limit rules.
  3. MO-only application visibility for own jobs.
  4. Job edit/close capability.
  5. Profile edit for TA.
- Should Have:
  1. Job search and filtering (keyword, module code, skill match threshold).
  2. Admin overload alert view (basic rule-based).
  3. Better status transitions (including INTERVIEW operation).
- Could Have:
  1. Missing-skill hint for TA.
  2. Lightweight recommendation explanation text for transparency.

## 4. Functional Plan by Role

### 4.1 TA side
1. CV upload
   - Add file upload form field in TA profile area.
   - Store CV files in text-file compatible local folder (for example data/cv/).
   - Persist CV metadata/path in user record (CSV column expansion).
   - Validate extension and size (PDF/DOC/DOCX).

2. Profile edit
   - Add TA profile edit page and update endpoint.
   - Editable fields: name, email, year, major, skills.
   - Keep username immutable to reduce identity conflict.

3. Application constraints
   - Enforce max 3 submitted active applications per TA.
   - Enforce max 3 accepted assignments per TA.
   - Return clear messages to UI when blocked.

4. Search/filter
   - Backend support for keyword/module/skills filtering.
   - Keep current list page and add query parameters.

### 4.2 MO side
1. My jobs management
   - Add page: view my posted jobs.
   - Add edit action for title, year range, hours, required skills.
   - Add close/reopen action for job status.

2. Applications visibility control
   - Only show applications linked to jobs created by current MO.

3. Richer status operations
   - Add INTERVIEW action in application management.
   - Keep status transition checks in service layer.

### 4.3 Admin side
1. Workload model enhancement
   - Current metric: application count.
   - Version2 metric extension:
     - accepted job count,
     - accepted total hours,
     - overload warning flag (rule based).

2. Alert dashboard
   - Add simple threshold configuration in code/constants.
   - Show TA rows that exceed threshold.

## 5. Decoupled Development Strategy

## 5.1 Layer responsibilities (strict)
1. Controller layer
   - Only handle request parsing, response routing, session extraction.
   - No core business rule implementation.

2. Service layer
   - Own all domain policies and validation decisions.
   - Expose methods with clear input/output contracts.

3. Storage layer
   - Own data serialization/deserialization and file paths.
   - Avoid business decisions in storage utilities.

4. View layer
   - Render data and submit forms.
   - Avoid embedding business logic in JSP.

## 5.2 Decomposition by modules
Create service-focused modules to reduce coupling:
1. Auth module
   - login/logout/session role retrieval.
2. Profile module
   - TA profile read/update + CV metadata handling.
3. Job module
   - create/edit/close/search jobs.
4. Application module
   - submit/list/update status + constraints.
5. Admin analytics module
   - workload metrics and overload checks.

## 5.3 Shared policy abstraction
Introduce centralized policy classes (or methods) to avoid duplicate rules:
1. ApplicationPolicy
   - canApply(userId, jobId)
   - validateMaxApplications(userId)
2. AssignmentPolicy
   - validateMaxAccepted(userId)
3. FilePolicy
   - validateCvFile(name, size, type)
4. StatusPolicy
   - isTransitionAllowed(from, to)

## 5.4 Validation decoupling
1. Add dedicated validator helpers:
   - UserInputValidator
   - JobInputValidator
   - ApplicationInputValidator
2. Controllers call validators before service methods.
3. Service performs final domain-level re-check (defense in depth).

## 5.5 Access control decoupling
1. Add an AuthFilter (or role filter set) to centralize login/role checks.
2. Reduce repeated role-check code across servlets.
3. Keep route policy table in one place for maintainability.

## 5.6 Storage evolution without DB
Within coursework constraints, still decouple repository behavior:
1. Define repository-like interfaces in service boundary.
2. Keep CSV implementation as default adapter.
3. This allows future switch between CSV and JSON without rewriting services.

## 6. Suggested Implementation Order

### Phase A - Foundation refactor (low risk)
1. Introduce validators and policy helpers.
2. Introduce centralized auth/role filter.
3. Refactor repeated role/session checks in controllers.

### Phase B - Core missing features
1. TA profile edit + CV upload.
2. Apply-limit and accept-limit policies.
3. MO-owned application visibility restriction.

### Phase C - Role capability completion
1. MO edit/close jobs.
2. INTERVIEW status action.
3. Search/filter backend support.

### Phase D - Admin and quality
1. Workload metric extension and overload warning.
2. Regression testing of all role flows.
3. Update user manual screenshots and release notes.

## 7. Testing Plan for Version2

### 7.1 Functional tests
1. TA registration/login/profile edit/CV upload.
2. Job browsing and filtered search.
3. Apply success, duplicate-apply rejection, over-limit rejection.
4. MO create/edit/close jobs and process status transitions.
5. Admin workload and overload warning display.

### 7.2 Rule tests
1. Max 3 applications rule.
2. Max 3 accepted jobs rule.
3. MO can only manage own job applications.
4. Closed job cannot accept new applications.

### 7.3 Data integrity tests
1. CSV read/write consistency after updates.
2. ID generation continuity.
3. CV path persistence and file existence checks.

## 8. Deliverables for Version2

1. Updated source code (controllers/services/storage/jsp).
2. Updated CSV headers and migration notes.
3. Updated architecture and sequence diagrams.
4. Updated user manual screenshots by role.
5. Version2 release note and known limitations.

## 9. Risk and Mitigation

1. Risk: CSV schema change breaks old data.
   - Mitigation: add backward-compatible parser defaults.
2. Risk: File upload path issues on deployment.
   - Mitigation: configurable base path + startup checks.
3. Risk: Rule logic duplicated and inconsistent.
   - Mitigation: enforce policy class as single source of truth.
4. Risk: Refactor causes regression.
   - Mitigation: regression checklist and smoke tests per role before merge.

## 10. Out of Scope for Version2

1. Database integration.
2. Spring Boot migration.
3. Heavy AI model integration.
4. Full asynchronous notification infrastructure.

These items remain compatible with later iterations but are intentionally excluded from Version2 to protect delivery certainty.
