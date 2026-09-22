# Candidate Source Notice

The files below `staged-production/` are **not applied production changes** and are not
claimed to compile against a future repository revision merely because they are present
in this package.

They are implementation candidates prepared against repository main commit
`7e63abad6344f8c1d82e6bfd71590782af9c23a1`.

Rules for Codex:

1. Never copy the entire staged tree into `app/`.
2. Use only the files relevant to the currently active roadmap slice.
3. Re-read the current repository source before adapting a candidate.
4. Prefer the repository's existing abstractions and naming when they have evolved.
5. Do not replace working code merely to match a staged candidate.
6. Every production file introduced from this staging area must be compiled/tested in
   the active cycle and reviewed in the final diff.
7. If a candidate conflicts with the approved specification, roadmap, architecture,
   accessibility rules, or current code, the candidate loses.
