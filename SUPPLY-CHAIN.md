# Supply chain
## Attest build artifacts
The Spoon CI/CD pipeline attests all released artifacts by publishing attestations to the [sigstore/rekor](https://www.sigstore.dev/) public-good instance as well as storing them in the [Spoon repository](https://github.com/INRIA/spoon/attestations). Attestations are published using Github's [attest-build-provenance](https://github.com/actions/attest-build-provenance) action as a step in the [jreleaser job](https://github.com/ludvigch/spoon/blob/master/.github/workflows/jreleaser.yml). A list of the attestations created in a release can be found in the summary of the job and links sigstore/rekor for each attestation can be found in the log of the jreleaser job.

## Verifying attestations
There are multiple ways to verify the integrity of an artifact using the attestations.

### Using [gh attestation verify](https://cli.github.com/manual/gh_attestation_verify)
The most straight-forward approach is to use GitHub CLI to verify the attestation of an artifact by running: 

`gh attestation verify <artifact-name>.jar -R INRIA/spoon`

For example:

`gh attestation verify spoon-core-11.1.1-beta-11-jar-with-dependencies.jar -R INRIA/spoon`

Output:
```
Loaded digest sha256:804c2ab449cc16052b467edc3ab1f7cf931f8e679685c0e16fab2fcc16ecfb41 for file://spoon-core-11.1.1-beta-11-jar-with-dependencies.jar
Loaded 1 attestation from GitHub API
✓ Verification succeeded!

sha256:804c2ab449cc16052b467edc3ab1f7cf931f8e679685c0e16fab2fcc16ecfb41 was attested by:
REPO         PREDICATE_TYPE                  WORKFLOW                                         
INRIA/spoon  https://slsa.dev/provenance/v1  .github/workflows/jreleaser.yml@refs/heads/master

```

### Manually searching sigstore/rekor
Sigstore/rekor is searchable using the sha256 hash of an attested artifact. This allows fetching the attestation for an artifact and manually verifying it. Note that this approach is time-consuming and requires the user to be familiar with verifying signatures using openssl.

Get the sha256sum for an artifact:
`sha256sum spoon-core-11.1.1-beta-11-jar-with-dependencies.jar`

Get the sigstore/rekor attestation:
[https://search.sigstore.dev/?hash=804c2ab449cc16052b467edc3ab1f7cf931f8e679685c0e16fab2fcc16ecfb41](https://search.sigstore.dev/?hash=804c2ab449cc16052b467edc3ab1f7cf931f8e679685c0e16fab2fcc16ecfb41)

Manually verifying using openssl: Sigstore docs provide a [tutorial](https://docs.sigstore.dev/logging/verify-release/)
