#!/bin/bash
# runs the doc server locally

# Generate the website.
jekyll serve --watch
if [ "$?" -ne 0 ]; then
    echo "Jekyll cannot build your site!"
    exit 1
fi
