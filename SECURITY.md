# Security Policy

## Supported Versions

The only supported version is the latest major version. Security fixes are not
backported to earlier releases.

## Reporting a Vulnerability

If you find a vulnerability in Spoon, there are two preferred ways to report
it.

> If none of these ways work for you, please reach out to us by email **without
> disclosing the vulnerability** to discuss alternatives. Integrator emails are
> listed in [CONTRIBUTING.md](CONTRIBUTING.md).

### Report a vulnerability using GitHub Security Advisories (if you have a GitHub account)

Send an email to any of the integrators listed in
[CONTRIBUTING.md](CONTRIBUTING.md), and say that you've found a vulnerability.
**Do not** disclose any details. The integrator will create a
[Security Advisory](https://docs.github.com/en/code-security/security-advisories/about-github-security-advisories)
and invite you to it, where you can then safely disclose the vulnerability.

### Report a vulnerability by encrypted email (if you don't have a GitHub account)

Some integrators have public PGP keys that you can use to encrypt a
message, for example using `gpg`. See [CONTRIBUTING.md](CONTRIBUTING.md)
for a list of integrator key fingerprints. If you write down the vulnerability
in a file `vuln.txt`, you can encrypt it with `gpg` like so:

```bash
$ gpg --recv-keys <key_fingerprint> # fetch the key
$ gpg --encrypt --armor -r <key_fingerprints> vuln.txt # encrypt the message
```

This creates a file `vuln.txt.asc` that you can then attach to an email.
