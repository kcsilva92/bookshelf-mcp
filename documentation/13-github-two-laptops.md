# Using GitHub between your work and personal laptops

## Your setup

Repository: [kcsilva92/bookshelf-mcp](https://github.com/kcsilva92/bookshelf-mcp).

- The repository is public and owned by `kcsilva92`.
- Your work laptop uses that account through GitHub CLI.
- Your personal account, `ketheleenbazan`, was invited with permission to push. Make sure you accept the invitation while signed into that account.
- The first project commit was pushed to the `main` branch.

You can continue working on the work laptop today. Before switching computers, save, commit, and push the latest changes. Your personal laptop can then download those changes. GitHub only receives committed changes that you push; unsaved editor changes and uncommitted files are not automatically synchronized.

## 1. Before leaving the work laptop

Save your files in VS Code. From the project's terminal, run:

```bash
git status
```

This shows files changed since the last commit. Review them, then stage the files you intend to share. For example, to include the application, tests, and learning documentation:

```bash
git add src documentation pom.xml .gitignore
```

Staging means selecting the contents for your next commit. It does not upload anything. Check the selection:

```bash
git --no-pager diff --cached --stat
```

To read the actual changes rather than just their filenames and sizes:

```bash
git --no-pager diff --cached
```

Then create a local commit with a message describing what changed:

```bash
git commit -m "Update bookshelf code and learning guides"
```

Upload it:

```bash
git push
```

Finally:

```bash
git status
```

After a successful push, expect a clean working tree and a message that your branch is up to date with `origin/main`. If there are no changes to commit, Git will say so; you do not need to create an empty commit.

If another computer pushed changes since this laptop last synchronized, your push may be rejected. Do not force-push. Stop and inspect the situation before integrating the other changes.

## 2. On your personal laptop: check the existing folder first

You said the project already exists locally. That folder might be a Git clone or just a copied folder. Before downloading changes, open its terminal and run:

```bash
git status
git remote -v
```

These checks answer two questions: are there local changes to preserve, and is this folder connected to the intended repository?

### If it is already a clone of this repository

The remote should point to either the HTTPS URL:

```text
https://github.com/kcsilva92/bookshelf-mcp.git
```

or the equivalent SSH URL:

```text
git@github.com:kcsilva92/bookshelf-mcp.git
```

If the working tree is clean and you are on `main`, download the latest commits:

```bash
git pull --ff-only
```

This updates your local branch when it can simply advance to the newer commits. `--ff-only` stops rather than automatically creating a merge if the two computers have different local commits.

If you have uncommitted changes, different local commits, a different branch, or another remote, stop and review them first. Share `git status` and `git remote -v` for guidance. Do not discard files or force an update to make the command work.

### If Git says “not a git repository”

The existing folder is probably a copy rather than a clone. Keep it as a backup. From its parent folder, clone into a new, separately named folder:

```bash
git clone https://github.com/kcsilva92/bookshelf-mcp.git bookshelf-mcp-github
cd bookshelf-mcp-github
```

Do not run those commands from inside the old project folder: that would put the new project inside the old one. Do not overwrite or delete the old copy before checking whether it contains changes you need.

Open the new clone in VS Code and use it for future work. Compare any valuable changes in the old folder with the clone before copying them across.

## 3. GitHub login and commit identity are separate

On your personal laptop, check:

```bash
gh auth status
```

Use `ketheleenbazan` there. If GitHub CLI is installed but no account is signed in, use:

```bash
gh auth login
```

Follow the prompts for GitHub.com and your personal account. For HTTPS Git operations, GitHub CLI can configure Git to use its credentials:

```bash
gh auth setup-git
```

A public repository can be cloned or pulled without collaborator permission. Pushing needs authentication and permission, which is why the personal account invitation matters.

Check the identity attached to commits separately:

```bash
git var GIT_AUTHOR_IDENT
```

This is the name and email recorded in commits, not proof of which GitHub account is authenticated. To set an identity only for this repository:

```bash
git config user.name "YOUR_PREFERRED_NAME"
git config user.email "YOUR_PREFERRED_EMAIL"
```

Replace the placeholders. For a private email address in public commits, use the exact GitHub-provided noreply address from [GitHub email settings](https://github.com/settings/emails). Changing this setting affects future commits, not previously published ones.

## 4. Check Java and Maven on the personal laptop

The repository contains code and configuration. It does not install Java, Maven, VS Code extensions, or your terminal settings.

Check:

```bash
java -version
mvn -version
```

This project targets Java 21, and Maven should also report Java 21. Once those tools are ready, verify the downloaded project:

```bash
mvn test
```

At the time of writing, there are 14 tests. The last verified run had no failures or errors. The number may grow as the project develops.

## 5. The routine for switching laptops

| When | What to do |
|---|---|
| Start work | Check `git status`, then `git pull --ff-only` on a clean `main` branch. |
| Finish work | Save files, review and stage changes, commit, then push. |
| Switch laptops | Repeat the start-work steps on the other laptop. |

Finish pushing from one computer before editing on the other whenever possible. That keeps the two copies easy to synchronize.

## 6. What each command means

| Command | Purpose |
|---|---|
| `git clone` | Download a repository into a new local folder, usually once per computer. |
| `git status` | Show the current branch and local changes. |
| `git remote -v` | Show the connected remote repository URLs. |
| `git add` | Select file contents for the next commit. |
| `git commit` | Save that selection in local Git history. |
| `git push` | Upload local commits to GitHub. |
| `git pull --ff-only` | Download and apply newer commits without an automatic merge. |
| `gh` | Use GitHub features, such as login, repository creation, and collaborator invitations. |

`.gitignore` itself is shared. Its rules exclude `target/`, local editor folders, and common environment files. It does not exclude every possible secret, and it does not automatically remove files already tracked by Git. Review new files before committing to this public repository.

## 7. If the terminal shows “(END)”

Git sometimes opens a pager to display long output. Press `q` to exit it. Using `git --no-pager ...` shows the output directly.

## Today's next action

Continue working on the work laptop. This guide is a new local file and must be committed and pushed along with any other updates before it appears on GitHub or your personal laptop. At the end of today's work, follow section 1. On the personal laptop, start with section 2.
