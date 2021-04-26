# Spoon website documentation for maintainers

This document contains everything you need to know about the Spoon website in order to maintain and update it. The website is currently hosted at https://spoon.gforge.inria.fr/.

## Building the website locally

To build the website locally, you only need to have Jekyll installed, which is a [Ruby gem](https://en.wikipedia.org/wiki/RubyGems). Typically, you would install this with the `gem` command, assuming that you have Ruby (2.4+ required) installed.

```bash
$ gem install jekyll
```

Pay attention to the output of `gem install`, it provides useful information such as warning when the installed `jekyll` binary is not on your `PATH`. When `jekyll` is installed, you can serve the website by going to the `doc` directory and executing `jekyll serve`.

```bash
$ jekyll serve
Configuration file: /path/to/spoon/doc/_config.yml
            Source: /path/to/spoon/doc
       Destination: /path/to/spoon/doc/_site
 Incremental build: disabled. Enable with --incremental
      Generating... 
                    done in 0.102 seconds.
 Auto-regeneration: enabled for '/path/to/spoon/doc'
    Server address: http://127.0.0.1:4000/
  Server running... press ctrl-c to stop.
```

The website will automatically regenerate as you make changes in the source files.

> **Note:** You may also need to separately install the gem `webrick` if there is a crash when running `jekyll serve`.

> **Note:** To get the proper index page, copy the root README to `doc/doc_homepage.md`.

## Deploying the website

The website is automatically deployed to the [SpoonLabs GitHub Pages repository](https://github.com/spoonlabs/spoonlabs.github.io). The deployment is handled by a GitHub Actions workflow in said repository, [see the README](https://github.com/SpoonLabs/SpoonLabs.github.io/blob/main/README.md) for details.
