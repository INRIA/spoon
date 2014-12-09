#!/bin/bash
#
# Deploys the current Spoon website to the website server.
# To test the site locally before deploying run `jekyll serve`
# in the website branch.

REPO="https://github.com/INRIA/spoon.git"
DIR=temp-spoon-clone

WEBSITE_SERVER="scm.gforge.inria.fr"
SOURCE="_site/"
DESTINATION="/home/groups/spoon/htdocs/"

# Delete any existing previous temps spoon clone.
rm -rf $DIR

# Clone the current repo into temp folder.
git clone $REPO $DIR

# Move working directory into temp folder.
cd $DIR

# Checkout website branch.
git checkout -t origin/website

# Generate the website.
jekyll build
if [ "$?" -ne 0 ]; then
    echo "Jekyll cannot build your site!"
    exit 1
fi

# Delete existing website on the server.
ssh $WEBSITE_SERVER 'rm -rf /home/groups/spoon/htdocs/*'
if [ "$?" -ne 0 ]; then
    echo "Error when you tried to remove the old version of the website!"
    exit 1
fi

# Copy the website on the server.
scp -r $SOURCE* $WEBSITE_SERVER:$DESTINATION
if [ "$?" -ne 0 ]; then
    echo "Error when you tried to copy the new version on the server!"
    exit 1
fi

# Checkout master branch.
git checkout master

# Generate maven site and deploy it.
mvn site site:deploy
if [ "$?" -ne 0 ]; then
    echo "Error when you tried to build or deploy maven site!"
    exit 1
fi

# Delete our temp folder.
cd .. && rm -rf $DIR
