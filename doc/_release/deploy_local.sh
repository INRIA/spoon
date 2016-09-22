#!/bin/bash
#
# Copy all markdown files in the _jekyll directory
# to generate the website.

# Move all markdown files in the jekyll directory.
cp `dirname $0`/../*.md `dirname $0`/../_jekyll/

# Generate the website.
jekyll serve --watch
if [ "$?" -ne 0 ]; then
    echo "Jekyll cannot build your site!"
    exit 1
fi
