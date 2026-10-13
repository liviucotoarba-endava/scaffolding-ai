# AI Tools Guidance

This is a shared Codex toolkit and presentation reference, collected from the three sibling demo applications.

- Keep collection work inside `ai-tools`; do not synchronize changes into the source demos unless explicitly requested.
- Use `docs/catalog.md` to identify each tool's source and purpose.
- Keep complete skill folders and their supporting files. Give different workflows distinct folder and frontmatter
  names; update copied references when renaming.
- Toolkit instructions belong here at the `ai-tools` level. Implementation agents require the matching application's context and tests.
- Keep the README and usage docs short and suitable for presentation preparation.
- Check skill metadata, TOML/JSON syntax, local links, and hook path/behavior after relevant edits. Application tests
  are needed only when application behavior changes.
- Never expose credentials. Remote tools default to read-only access; perform writes only when explicitly authorized.
- Do not commit or push unless the user explicitly requests it.
