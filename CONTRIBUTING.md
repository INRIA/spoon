For contributing to Spoon, the recommended way is to create a pull request (PR) on Github. Upon acceptance, the merged code is copyrighted under the [Cecill-C license](http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.html).

Guidelines for pull requests:

**G0**: The pull request contains a single bug fix or a single feature. 

**G1**: The pull request must contain a test case specifying the new feature or highlighting the bug. 

**G2**: The pull request must comply with Spoon's formatting rules.

**G3**: The pull request's commit must have an explicit and clear commit message.

**G4**: To achieve a clean commit history after a pull request discussion, commits should be squashed as much as possible.

**G5**: If your pull request contains a critical bug fix of the current release, you must create a pull request to the stable branch.


Notes:

1. Refactoring and documentation PRs are exceptions to G1.
1. Pull requests with test cases only are welcome.
1. Commit messages start with a verb: "adds support", "fixes bug", ...
1. We encourage long commit messages.
1. G2 is specified in the checkstyle rules.
1. If there is no activity on an issue or on a pull request for 3 months it's closed.
