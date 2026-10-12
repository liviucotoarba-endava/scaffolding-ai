"""Run with: python3 -B -m unittest discover -s .codex/hooks -p 'test_*.py'."""

import contextlib
import io
import json
import unittest
from unittest.mock import patch

import commit_guard as guard


class CommitGuardTest(unittest.TestCase):
    def run_hook(self, command):
        event = {"tool_name": "Bash", "tool_input": {"command": command}, "cwd": "/repo"}
        output = io.StringIO()
        with patch("sys.stdin", io.StringIO(json.dumps(event))), contextlib.redirect_stdout(output):
            try:
                guard.main()
            except SystemExit as error:
                self.assertEqual(error.code, 0)
        return json.loads(output.getvalue()) if output.getvalue() else None

    def test_valid_and_breaking_commit_subjects_pass(self):
        for subject in ("feat(users): add search", "feat(users)!: change response"):
            with self.subTest(subject=subject):
                self.assertIsNone(self.run_hook(f'git commit -m "{subject}"'))

    def test_invalid_subject_is_denied(self):
        result = self.run_hook('git commit -m "invalid message"')
        self.assertEqual(result["hookSpecificOutput"]["permissionDecision"], "deny")

    def test_compound_commands_cannot_supply_another_commands_message(self):
        for separator in (" && ", ";", "\n", " | "):
            with self.subTest(separator=separator):
                self.assertIsNotNone(self.run_hook('git commit' + separator + 'echo -m "feat(users): add search"'))

    def test_directory_changes_and_git_global_options_are_denied(self):
        for command in ('cd /other && git push', 'git -C /other push', 'git -c push.default=matching push'):
            with self.subTest(command=command):
                self.assertIsNotNone(self.run_hook(command))

    def test_read_only_commands_and_quoted_message_punctuation_pass(self):
        for command in ('git status && git diff', 'echo git commit', 'git commit -m "fix(users): handle a; b && c"'):
            with self.subTest(command=command):
                self.assertIsNone(self.run_hook(command))

    def test_amend_requires_no_edit_to_check_the_existing_subject(self):
        with patch.object(guard, "git", return_value="fix(users): handle missing user"):
            self.assertIsNotNone(self.run_hook('git commit --amend'))
            self.assertIsNone(self.run_hook('git commit --amend --no-edit'))

    def test_reused_or_file_messages_are_denied(self):
        for option in ('-F /tmp/message', '-F/tmp/message', '-C HEAD', '--reuse-message=HEAD'):
            with self.subTest(option=option):
                self.assertIsNotNone(self.run_hook(f'git commit -m "fix(users): handle missing user" {option}'))

    def test_message_editing_and_fixup_subjects_are_denied(self):
        for option in ('--edit', '-e', '--fixup=HEAD', '--squash=HEAD'):
            with self.subTest(option=option):
                self.assertIsNotNone(self.run_hook(f'git commit -m "fix(users): handle missing user" {option}'))

    def fake_git(self, cwd, *args):
        if args[0] == "remote":
            return "origin\nbackup"
        if args[0] == "symbolic-ref":
            return "main"
        if args[0] == "rev-parse":
            return "source-commit"
        if args[0] == "for-each-ref":
            return "refs/remotes/origin/main"
        if args[0] == "log":
            return "feat(users): add search"
        return None

    def test_push_checks_explicit_sources_against_the_selected_remote(self):
        with patch.object(guard, "git", side_effect=self.fake_git) as git:
            self.assertIsNone(guard.check_push("/repo", ["-u", "backup", "other:release", "+second:target"]))
            git.assert_any_call("/repo", "log", "--format=%s", "other", "second", "--not", "--remotes=backup")

    def test_default_push_checks_head(self):
        with patch.object(guard, "git", side_effect=self.fake_git) as git:
            self.assertIsNone(guard.check_push("/repo", []))
            git.assert_any_call("/repo", "log", "--format=%s", "HEAD", "--not", "--remotes=origin")

    def test_push_rejects_invalid_outgoing_subjects(self):
        def fake_git(cwd, *args):
            return "invalid message" if args[0] == "log" else self.fake_git(cwd, *args)
        with patch.object(guard, "git", side_effect=fake_git):
            self.assertIn("Push blocked", guard.check_push("/repo", ["origin", "other"]))

    def test_push_does_not_guess_ambiguous_sources(self):
        with patch.object(guard, "git", side_effect=self.fake_git):
            for args in (["--all"], ["--mirror"], ["origin", ":deleted"], ["origin", "refs/heads/*:refs/heads/*"], ["https://example.com/repo", "main"]):
                with self.subTest(args=args):
                    self.assertIsNotNone(guard.check_push("/repo", args))

    def test_default_push_rejects_configured_matching_or_custom_refspecs(self):
        for setting in ("push.default", "remote.origin.push"):
            def fake_git(cwd, *args):
                if args[0] == "config" and args[-1] == setting:
                    return "matching" if setting == "push.default" else "other:release"
                return self.fake_git(cwd, *args)
            with self.subTest(setting=setting), patch.object(guard, "git", side_effect=fake_git):
                self.assertIsNotNone(guard.check_push("/repo", []))

    def test_push_requires_local_tracking_refs(self):
        def fake_git(cwd, *args):
            return "" if args[0] == "for-each-ref" else self.fake_git(cwd, *args)
        with patch.object(guard, "git", side_effect=fake_git):
            self.assertIn("no local remote-tracking refs", guard.check_push("/repo", ["origin", "main"]))

    def test_mirror_remote_is_denied_even_with_an_explicit_source(self):
        def fake_git(cwd, *args):
            if args[0] == "config" and args[-1] == "remote.origin.mirror":
                return "true"
            return self.fake_git(cwd, *args)
        with patch.object(guard, "git", side_effect=fake_git):
            self.assertIn("Mirror pushes", guard.check_push("/repo", ["origin", "main"]))


if __name__ == "__main__":
    unittest.main()
