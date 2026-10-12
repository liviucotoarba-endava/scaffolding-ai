#!/usr/bin/env python3
"""Check simple Git commit/push commands using local remote-tracking refs."""

import json
import os
import re
import shlex
import subprocess
import sys

SUBJECT = re.compile(r"^[a-z][a-z0-9-]*(?:\([a-z0-9][a-z0-9._/-]*\))?!?: \S(?:.*\S)?$")
VALUE_OPTIONS = {"-C", "-c", "--git-dir", "--work-tree", "--namespace"}
SHELL_SEPARATORS = {";", "&", "|", "(", ")", "\n"}


def git(cwd, *args):
    try:
        result = subprocess.run(
            ["git", *args], cwd=cwd, text=True, capture_output=True, timeout=5
        )
    except (OSError, subprocess.TimeoutExpired):
        return None
    return result.stdout.strip() if result.returncode == 0 else None


def deny(reason):
    print(json.dumps({
        "hookSpecificOutput": {
            "hookEventName": "PreToolUse",
            "permissionDecision": "deny",
            "permissionDecisionReason": reason,
        }
    }))
    sys.exit(0)


def git_commands(tokens):
    commands = [[]]
    for token in tokens:
        if token and set(token) <= SHELL_SEPARATORS:
            commands.append([])
        else:
            commands[-1].append(token)
    for command in commands:
        if not command or os.path.basename(command[0]) != "git":
            continue
        index = 1
        while index < len(command):
            option = command[index]
            if option in VALUE_OPTIONS:
                index += 2
            elif option.startswith(("--git-dir=", "--work-tree=", "--namespace=", "-C", "-c")):
                index += 1
            elif option.startswith("-"):
                index += 1
            else:
                break
        if index < len(command) and command[index] in {"commit", "push"}:
            if len(commands) != 1 or index != 1:
                deny("Use one standalone git commit or git push command in the repository directory, without Git global options.")
            yield command[index], command[index + 1:]


def commit_subject(cwd, args):
    messages = []
    amend = False
    index = 0
    while index < len(args):
        arg = args[index]
        if arg == "--":
            break
        if arg in {"-m", "--message"} and index + 1 < len(args):
            messages.append(args[index + 1])
            index += 2
        elif arg.startswith("--message="):
            messages.append(arg.partition("=")[2])
            index += 1
        elif arg.startswith("-m") and len(arg) > 2:
            messages.append(arg[2:])
            index += 1
        elif arg in {"-e", "--edit", "--fixup", "--squash"} or arg.startswith(("--fixup=", "--squash=")):
            return None, "Do not edit or rewrite the checked subject; use -m or --message."
        elif (
            arg in {"-F", "--file", "-C", "--reuse-message", "-c", "--reedit-message"}
            or arg.startswith(("--file=", "--reuse-message=", "--reedit-message=", "-F", "-C", "-c"))
        ):
            return None, "Use -m or --message so the commit subject can be checked."
        else:
            amend = amend or arg == "--amend"
            index += 1
    if messages:
        return messages[0].splitlines()[0] if messages[0].splitlines() else "", None
    if amend and "--no-edit" in args:
        subject = git(cwd, "log", "-1", "--format=%s")
        return subject, None if subject else "Cannot read the commit message being amended."
    return None, "Pass the commit subject with -m or --message so it can be checked."


def check_subject(subject):
    if not subject or not SUBJECT.fullmatch(subject):
        return "Use Conventional Commits, for example: feat(users): add search. Use ! for breaking changes."
    return None


def check_push(cwd, args):
    positional = []
    options = {"-u", "--set-upstream", "-n", "--dry-run", "-f", "--force", "--force-with-lease"}
    for arg in args:
        if arg in options or arg.startswith("--force-with-lease="):
            continue
        if arg.startswith("-"):
            return "Use git push [--set-upstream] [remote [branch[:branch]]] so pushed commits can be checked."
        positional.append(arg)

    branch = git(cwd, "symbolic-ref", "--quiet", "--short", "HEAD")
    remote = positional[0] if positional else (
        (git(cwd, "config", "--get", f"branch.{branch}.pushRemote") if branch else None)
        or git(cwd, "config", "--get", "remote.pushDefault")
        or (git(cwd, "config", "--get", f"branch.{branch}.remote") if branch else None)
        or "origin"
    )
    if remote not in (git(cwd, "remote") or "").splitlines():
        return "Use a configured remote name so its local remote-tracking refs can be checked."
    if git(cwd, "config", "--bool", "--get", f"remote.{remote}.mirror") == "true":
        return "Mirror pushes cannot be checked by this hook. Use a remote without mirror mode."

    refspecs = positional[1:]
    if not refspecs:
        if (
            git(cwd, "config", "--get-all", f"remote.{remote}.push")
            or git(cwd, "config", "--get", "push.default") == "matching"
        ):
            return "Name the branch explicitly so pushed commits can be checked."
        refspecs = ["HEAD"]
    sources = []
    for refspec in refspecs:
        source = refspec.lstrip("+").partition(":")[0]
        if (
            not source or "*" in refspec
            or not git(cwd, "rev-parse", "--verify", f"{source}^{{commit}}")
        ):
            return "Use an existing branch or commit as the explicit push source; matching and deletion refspecs are not supported."
        sources.append(source)

    if not git(cwd, "for-each-ref", "--count=1", "--format=%(refname)", f"refs/remotes/{remote}/"):
        return "Cannot check pushed commits: no local remote-tracking refs are available for this remote."
    subjects = git(cwd, "log", "--format=%s", *sources, "--not", f"--remotes={remote}")
    if subjects is None:
        return "Cannot compare pushed commits with the local remote-tracking refs."
    for subject in subjects.splitlines():
        if error := check_subject(subject):
            return f"Push blocked: '{subject}' is not a Conventional Commit. {error}"
    return None


def main():
    try:
        event = json.load(sys.stdin)
    except (json.JSONDecodeError, TypeError):
        return
    if event.get("tool_name") != "Bash":
        return
    command = (event.get("tool_input") or {}).get("command", "")
    if not isinstance(command, str):
        return
    try:
        lexer = shlex.shlex(command, posix=True, punctuation_chars=";&|()\n")
        lexer.whitespace = " \t\r"
        tokens = list(lexer)
    except ValueError:
        if re.search(r"\bgit\s+(commit|push)\b", command):
            deny("Could not inspect this Git command. Use a simple git commit or git push command.")
        return

    cwd = event.get("cwd") or os.getcwd()
    for action, args in git_commands(tokens):
        if action == "commit":
            subject, error = commit_subject(cwd, args)
            if error:
                deny(error)
            if error := check_subject(subject):
                deny(error)
        elif error := check_push(cwd, args):
            deny(error)


if __name__ == "__main__":
    main()
