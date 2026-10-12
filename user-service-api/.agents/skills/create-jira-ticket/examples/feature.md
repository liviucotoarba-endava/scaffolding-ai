# Summary
Let users update their name and email

# Issue Type
Story

# Description

## Context
Users need to keep their profile details current.

## Goal
Allow an existing user's name and email to be updated through the REST API.

## Acceptance Criteria
- [ ] A valid update changes the user's name and email.
- [ ] The response returns the updated user.
- [ ] Invalid requests return `400 Bad Request` and do not change stored data.
