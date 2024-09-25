For contributing to Spoon, the recommended way is to create a pull request (PR) on Github. Upon acceptance, the merged code is copyrighted under the [Cecill-C license](http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.html).

The integrator code of conduct
------------------------------

The integrators are the developers who have write access to the repository. The integrators shall respect the following rules:

* Spoon integrators only merge atomic pull requests (single bug fix or single feature)
* Spoon integrators only merge well tested and well documented pull requests, after thorough code review
* Spoon integrators check the quality of the squashed merge commit message, and change it if required. The convention is that it starts with `fix:`, `feat:`, `test:`, `doc:`, `perf:`, `chore:`, `refactor:`, `checkstyle:` ([source](https://github.com/angular/angular.js/blob/master/CONTRIBUTING.md#type)). Optionally the impacted component can be specified, eg `fix(prettyprinter): ...`.
* Spoon integrators never push directly to `master`
* Spoon integrators  never merge their own pull request
* Spoon integrators leave pull requests opened at least 1 day before merging, so that the community is aware and can comment on them
* Serious conflicts, who cannot be resolved by time and discussion, are resolved by a vote among integrators.

How to become integrator? The integrators are the developers who have made significant contributions over the last 12 months (gliding window). Significance is assessed by the current set of integrators who co-opt the new ones.

Current integrators:

- Nicolas Harrand [@nharrand](https://github.com/nharrand/)
    - Email: nicolas.harrand@gmail.com
- Martin Monperrus [@monperrus](https://github.com/monperrus/)
    - Email: martin.monperrus@gnieh.org
    - GPG fingerprint: [AF7B251DA8126C30896FAFF77D398AD45AEEEC93](https://keyserver.ubuntu.com/pks/lookup?op=get&search=0xAF7B251DA8126C30896FAFF77D398AD45AEEEC93)
- Martin Wittlinger [@MartinWitt](https://github.com/MartinWitt)
    - Email: wittlinger.martin@gmail.com
- Hannes Greule [@SirYwell](https://github.com/SirYwell)
    - Email: hannesgreule@outlook.de
- [@I-Al-Istannen](https://github.com/I-Al-Istannen)
    - Email: me@ialistannen.de

Guidelines for pull requests
----------------------------

There are different kinds of pull-requests, in particular bug-fix, new features, refactoring and performance pull-requests.

Guidelines for all pull-requests:

* The pull request does a single thing (eg a single bug fix or a single feature). 
* The pull request must pass all continuous integration checks (incl. formatting rules).
* The pull request must have an explicit and clear explanation.
* If the pull request resolves an issue, simply add "fix #issueNumber" or "close #issueNumber" to the description, see [doc](https://docs.github.com/en/free-pro-team@latest/github/managing-your-work-on-github/linking-a-pull-request-to-an-issue) for details
* Pull-request title:
  * The pull-request title starts with a prefix stating its kind: "fix:", "feature:", "refactor:", "perf:", "checkstyle:"
  * Pull-requests that are in progress are prefixed by "WIP".
  * Pull-requests that are ready for review are prefixed by "review", or labeled as "[review](https://github.com/INRIA/spoon/labels/review)".
* **Avoid force-pushing** after you have marked your PR as ready for review or have received any form of feedback on your PR.
  * While a clean and clear commit history can help in reviewing a PR, it's confusing for reviewers to have previously reviewed commits disappear.
  * Note that PRs are squashed. Don't worry if the commit history becomes a bit convoluted as you iterate on the PR after receiving feedback.
  * It's OK to force-push to WIP PRs as long as you are not collaborating with anyone.
* **Your contribution is highly welcome**! If you have anything interesting, then we welcome your PR even if it is not perfect at the beginning. The Spoon community will help you to fix the remaining problems, if any.;-)
* **JUnit version:** 
  * [PR to migrate existing tests from JUnit4 to JUnit5 are welcome](https://github.com/INRIA/spoon/issues/3919).
  * New test classes must use JUnit5.
  * Adding test cases to existing JUnit4 test classes is OK, but try to stick to the very basics (if possible, only use the `@Test` annotation)
  
Guidelines for bug-fix pull-requests:

* The pull request must contain a test case highlighting the bug. 

Guidelines for feature pull-requests:

* The pull request must contain a set of test case to specify the expected behavior of the new feature. 
* The pull request must contain an update in the documentation folder (`doc`) to explain the new feature.
* The pull request must pass all architectural rules that are checked in [SpoonArchitectureEnforcerTest](https://github.com/INRIA/spoon/blob/master/src/test/java/spoon/test/architecture/SpoonArchitectureEnforcerTest.java) (eg new packages must be registered there)

Other kinds of pull-requests:

1. Pull requests with passing test cases only are welcome, they specify previously unspecified behavior and are prefixed by "test:".
2. Pull requests with failing test cases only are welcome, they reproduce bugs and are very useful for maintainers to fix them. You can prevent failing the CI with adding the annotation `@GitHubIssue(issueNumber = <your-issue-number>, fixed = false)`. If you fix a test case with such an annotation mark the test case as fixed with `@GitHubIssue(issueNumber = <your-issue-number>, fixed = true)`.
1. "Chore" pull-requests modify the CI setup.
1. If there is no activity on an issue or on a pull request for 3 months it's closed.

Public API
----------

The public API is composed of all public classes and methods, except those for which at least one of the following condition holds:

* annotated with @Internal
* located in a package called `internal`, including all subpackages, that is `**.internal.**`

Classes annotated with `@Experimental` are planned to in the public API in the future, but are still considered unstable and can change in non-backward compatble manner.

Submodule Policy
----------

By default, we favor adding new functionalities in spoon-core, in order to have the easiest possible integration for clients. We commit to maintain the code in spoon-core.

We create submodules when

1) the functionality would bloat the spoon-core binary too much

OR

2) the functionality is too experimental / too unstable to meet the high quality standard of spoon-core

OR

3) the integrator team welcomes the contribution, but cannot commit to maintaining it.

Submodules are not published to Maven central, and so need to be built from source.
