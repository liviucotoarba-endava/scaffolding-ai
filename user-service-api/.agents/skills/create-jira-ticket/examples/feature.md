# Summary
Filter a list of items by status

# Issue Type
Story

# Description

## Context
People need to narrow a list to items with a selected status.

## Goal
Allow an optional status filter while preserving the current behavior when no filter is supplied.

## Acceptance Criteria
- [ ] A supported status filter returns only matching items.
- [ ] Omitting the filter preserves the existing list behavior.
- [ ] An unsupported status produces a clear validation error.
- [ ] Automated checks cover matching items, no matches, and invalid input.
