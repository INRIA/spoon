# Supply chain
## Attest build artifacts
The Spoon CI/CD pipeline attests all released artifacts by publishing attestations to the [sigstore/rekor](https://www.sigstore.dev/) public-good instance as well as storing them in the [Github's attestation registry](https://github.com/INRIA/spoon/attestations). Attestations are published using Github's [attest-build-provenance](https://github.com/actions/attest-build-provenance) action as a step in the [jreleaser job](https://github.com/ludvigch/spoon/blob/master/.github/workflows/jreleaser.yml). A list of the attestations created for a release can be found in the summary of a job and the sigstore/rekor links for each attestation can be found in the log of the jreleaser job.

## Finding attestations

Rekor is searchable with the hash of an attested artifact, for example attestation for spoon-core-11.1.1-beta-11-jar-with-dependencies.jar can be found at 
<https://search.sigstore.dev?hash=804c2ab449cc16052b467edc3ab1f7cf931f8e679685c0e16fab2fcc16ecfb41>

Github provides an [`attestations` tab](https://github.com/INRIA/spoon/attestations) for all repos and a [REST API Endpoint](https://docs.github.com/en/rest/users/attestations)

## Verifying attestations

The most straight-forward approach is to use GitHub CLI's [`gh attestation verify`](https://cli.github.com/manual/gh_attestation_verify) to verify the attestation of an artifact by running: 

`gh attestation verify <artifact-name>.jar -R INRIA/spoon`

For example, let's verify the [spoon-core-11.1.1-beta-11-jar-with-dependencies.jar](https://repo1.maven.org/maven2/fr/inria/gforge/spoon/spoon-core/11.1.1-beta-11/spoon-core-11.1.1-beta-11-jar-with-dependencies.jar) artifact.

### Alternative 1: Using GitHub API

Install `gh`, see doc at <https://cli.github.com/>

```
curl -O https://repo1.maven.org/maven2/fr/inria/gforge/spoon/spoon-core/11.1.1-beta-11/spoon-core-11.1.1-beta-11-jar-with-dependencies.jar
gh attestation verify spoon-core-11.1.1-beta-11-jar-with-dependencies.jar -R INRIA/spoon
```

Output:
```
Loaded digest sha256:804c2ab449cc16052b467edc3ab1f7cf931f8e679685c0e16fab2fcc16ecfb41 for file://spoon-core-11.1.1-beta-11-jar-with-dependencies.jar
Loaded 1 attestation from GitHub API
✓ Verification succeeded!

sha256:804c2ab449cc16052b467edc3ab1f7cf931f8e679685c0e16fab2fcc16ecfb41 was attested by:
REPO         PREDICATE_TYPE                  WORKFLOW                                         
INRIA/spoon  https://slsa.dev/provenance/v1  .github/workflows/jreleaser.yml@refs/heads/master

```

### Alternative 2: Using a downloaded attestation

[Dowload the attestation.](https://github.com/INRIA/spoon/attestations/2750640/download)

```
curl -o ./INRIA-spoon-attestation-2750640.sigstore.json https://github.com/INRIA/spoon/attestations/2750640/download
gh attestation verify spoon-core-11.1.1-beta-11-jar-with-dependencies.jar -R INRIA/spoon --bundle ./INRIA-spoon-attestation-2750640.sigstore.json
```

Output:
```
Loaded digest sha256:804c2ab449cc16052b467edc3ab1f7cf931f8e679685c0e16fab2fcc16ecfb41 for file://spoon-core-11.1.1-beta-11-jar-with-dependencies.jar
Loaded 1 attestation from INRIA-spoon-attestation-2750640.sigstore.json
✓ Verification succeeded!

sha256:804c2ab449cc16052b467edc3ab1f7cf931f8e679685c0e16fab2fcc16ecfb41 was attested by:
REPO         PREDICATE_TYPE                  WORKFLOW                                         
INRIA/spoon  https://slsa.dev/provenance/v1  .github/workflows/jreleaser.yml@refs/heads/master

```
