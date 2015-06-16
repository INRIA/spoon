# Checklist for releases

- Uploads archives on Maven Central
- Uploads archives on INRIA's forge
- Updates Spoon's website
- Updates README of the GitHub project
- Retrieves all commits and tag from INRIA's repo to GitHub
    - `cd /home/groups/spoon/spoon.git/`
    - `git push origin master`
    - `git push origin spoon-core-X.X.X`
- Announces release on the mailing list (give credits to the contributors)
- Announces release on GitHub (if necessary)
- If necessary, removes all methods deprecated after the release!