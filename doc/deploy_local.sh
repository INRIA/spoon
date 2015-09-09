#!/bin/bash
#
# Copy all markdown files in the _jekyll directory
# to generate the website.

# Move all markdown files in the jekyll directory.
for file in *.md; do
    yes | cp -rf ${file} _jekyll
done
cd _jekyll

# Generate the website.
jekyll serve
if [ "$?" -ne 0 ]; then
    echo "Jekyll cannot build your site!"
    exit 1
fi