# Checklist for releases

## Before the release

- Push all critical bug fixes from stable to master branch

## After release

- Uploads archives on Maven Central
- Uploads archives on INRIA's forge
- Updates Spoon's website
    - News
    - Jar file link
    - Maven versions
- Updates README of the GitHub project
- Retrieves all commits and tag from INRIA's repo to GitHub
    - `cd /home/groups/spoon/spoon.git/`
    - `git push origin master`
    - `git push origin spoon-core-X.X.X`
- Updates stable branch with the new tag created:
    - `git checkout stable`
    - `git reset --hard last-commit-master` # We need two commits from previous release to deploy a hotfix.
    - `git push -f upstream stable`
- Announces release on the mailing list (give credits to the contributors)
- Announces release on GitHub (if necessary)
- If necessary, removes all methods deprecated after the release!
