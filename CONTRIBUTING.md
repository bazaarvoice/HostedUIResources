# Contributing

## Fork and Branch

We follow a fork+branch git workflow. First, fork this repo via GitHub, then create a branch, like this:

```bash
# get the repo (assumes you're using an ssh key with GitHub)
git clone git@github.com:<username>/HostedUIResources.git
cd HostedUIResources
# add upstream to allow for catch-up (see below)
git remote add upstream git@github.com:bazaarvoice/HostedUIResources.git
# create a feature branch (Bazaarvoice employees should name their branch after a ticket number)
git checkout -b BV-0000
# create your branch on GitHub (you need --set-upstream for the very first push, only; it's sticky after that)
git push --set-upstream origin BV-0000
```

After making your changes, locally, commit with a descriptive commit message.

```bash
# stage your changes
git add path/to/file
# commit staged changes
git commit -m "description of changes"
# push to GitHub
git push
```

After committing and pushing, open a pull request on GitHub. Bazaarvoice employees should always include a ticket number in the PR title.


## Handling Pull Requests

Repo admins should always **squash and merge** PRs after code review.
