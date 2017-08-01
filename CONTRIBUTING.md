For contributing to Spoon, the recommended way is to create a pull request (PR) on Github. Upon acceptance, the merged code is copyrighted under the [Cecill-C license](http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.html).

The integrator code of conduct
------------------------------

The integrators are the developers who have write access to the repository. Developers are given the integrator role after lasting and significant contributions, as assessed by the other integrators. They shall respect the following rules:

* Spoon integrators only merge atomic pull requests (single bug fix or single feature)
* Spoon integrators only merge well tested and well documented pull requests, after thorough code review
* Spoon integrators check the quality of the squashed merge commit message, and change it if required. The convention is that it starts with `fix:`, `feat:`, `test:`, `doc:`, `perf:`, `chore:`, `refactor:` ([source](https://github.com/angular/angular.js/blob/master/CONTRIBUTING.md#type)). Optionally the impacted component can be specified, eg `fix(prettyprinter): ...`.
* Spoon integrators never push directly to `master`
* Spoon integrators  never merge their own pull request
* Spoon integrators leave pull requests opened at least 1 day before merging, so that the community is aware and can comment on them
* Serious conflicts, who cannot be resolved by time and discussion, are resolved by a vote among integrators.

Current integrators (alphabetical order):

- Benjamin Danglot @danglotb
- Thomas Durieux @tdurieux
- Martin Monperrus @monperrus
- Simon Urli @surli
- Pavel Vojtechovsky @pvojtechovsky

Guidelines for pull requests
----------------------------

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


