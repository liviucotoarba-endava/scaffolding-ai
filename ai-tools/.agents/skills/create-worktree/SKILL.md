---
name: create-worktree
description: Skill for creating the necessary Worktree setup for future multi-repo implementations
---

# Create Worktree

Skill for creating the necessary Worktree setup for future multi-repo implementations

## Context

You are useful for creating the necessary Worktree setup for future multi-repo implementations.
Your role is to make the actual Worktree folders with all the required repos, but no actual code changes needed.

## General Requirements

- Follow in detail all the steps provided below, in the indicated order. If you get any error at any step, then stop and
  ask for help
- Use across entire session the same JIRA ticket id for all changes (e.g. `JIRA-1234`)
- This is the main AI Tools repo, all other repos are at the same dir level.
- The workable parent folder for all implementations is under the current repo, in the folder `/codex`

## Steps

1. You must fetch from the user the following: JIRA ticket ID + JIRA ticket description + all the needed repo names
   that are part of this set-up. List repo dirs to find exact repo names. Ask the user about any other optional ticket
   clarifications + implementation suggestions + additional requirements such as: Confluence, Docs, etc. that should be
   taken into account for the implementation
2. Create a subfolder under `/codex` folder with the JIRA ticket id (e.g. `/JIRA-1234`) and use only that one for this
   session
3. Create a `workspace.code-workspace` file with all involved repos as folders
4. Create a file `requirements.md` with the full copy-pasted JIRA ticket ID and description and all provided details
5. Create the `plan.md` file with the orchestration plan for this ticket. Include in the plan the following: all
   involved repos where changes are needed, a short summary per each repo of needed high level changes, and the order in
   which each repo should be worked on, considering all interdependencies between repos. Don't go into details here,
   maximum 20 lines in the plan.
6. Per each repo do a `git checkout` and `git pull` on `main` branch to make sure you are up to date
7. Create a new worktree under `/JIRA-1234` per each involved repos
8. Per each worktree repo create a new branch `JIRA-1234`
9. Create the `.codex/AGENTS.md` file. This is useful so that future Codex sessions will know this
   small context overview at each startup. Mention all the followings:
    - Mention that this is a Worktree setup for multi-repo implementation (list repos)
    - Link the `requirements.md` file
    - Add JIRA ID and title (one-liner)
    - Mention that if later is needed to create/update a plan for the implementation / investigation, then create/update
      a `plan.md` file in current dir.
10. Provide the final summary of all changes & tests across all repos to the user.
