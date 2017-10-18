# Contributing

## Issues

First, create a new issue describing the problem and potential solution(s): https://github.com/bazaarvoice/HostedUIResources/issues.

If you plan to submit a pull request, be sure to keep track of the issue number and use it later in a commit message and pull request summary.

## Fork and Branch

To submit changes, follow a fork and branch git workflow.

First, fork this repo via GitHub, then create a feature branch:

```bash
# clone the fork (assumes you're using an ssh key with GitHub)
git clone git@github.com:<your GitHub username>/HostedUIResources.git
cd ./HostedUIResources
# add upstream to allow for syncing later
git remote add upstream git@github.com:bazaarvoice/HostedUIResources.git
# create a feature branch using a short, descriptive name in lisp-case format
git checkout -b add-new-feature
# push the new branch to GitHub (--set-upstream is required for the very first push, only; it's sticky after that)
git push --set-upstream origin add-new-feature
```

After making changes, locally, commit with a descriptive commit message, using a close issue keyword like "fixed #nn" or "closed #nn" to define which issue was resolved. See [Closing issues using keywords](https://help.github.com/articles/closing-issues-using-keywords/) for more details.

```bash
# stage the changes
git add path/to/file
# commit staged changes
git commit --message "fixed #1 - description of changes"
# push to GitHub
git push
```

After committing and pushing, open a pull request on GitHub, again using a close issue keyword in the pull request's summary.

After the PR has been merged, sync the forked repo and clean up the feature branch:

```bash
# move back to master
git checkout master
# sync with upstream
git fetch upstream
git merge upstream/master
# push changes to the forked repo
git push origin master
# delete feature branch locally
git branch --delete add-new-feature
# delete feature branch on GitHub
git push origin :add-new-feature
```


## Handling Pull Requests

Maintainers should always **squash and merge** pull requests after code review.
