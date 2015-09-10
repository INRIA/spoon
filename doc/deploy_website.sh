#!/bin/bash
#
# Deploys the current Spoon website to the website server.
# To test the site locally before deploying run `jekyll serve`
# in the website branch.

REPO="https://github.com/INRIA/spoon.git"
DIR=temp-spoon-clone
DIR_WEBSITE=${DIR}/doc/

USER_SERVER="paligot"
WEBSITE_SERVER="${USER_SERVER}@scm.gforge.inria.fr"
SOURCE="_site/"
HOST_DESTINATION="/home/groups/spoon/"
FOLDER_DESTINATION="htdocs"
DESTINATION="${HOST_DESTINATION}${FOLDER_DESTINATION}/"

# Delete any existing previous temps spoon clone.
rm -rf $DIR

# Clone the current repo into temp folder.
git clone $REPO $DIR

# Move working directory into temp folder.
cd $DIR_WEBSITE

# Move all markdown files in the jekyll directory.
for file in *.md; do
    cp ${file} _jekyll
done
cd _jekyll

# Generate the website.
jekyll build
if [ "$?" -ne 0 ]; then
    echo "Jekyll cannot build your site!"
    exit 1
fi

# Back up the old website and create the folder for the new one.
TIMESTAMP=$(date +%s)
BACKUP_DESTINATION="${HOST_DESTINATION}${FOLDER_DESTINATION}-${TIMESTAMP}"
ssh $WEBSITE_SERVER "mv ${DESTINATION} ${BACKUP_DESTINATION}"
if [ "$?" -ne 0 ]; then
    echo "Error when you tried to back up the old website!"
    exit 1
fi

ssh $WEBSITE_SERVER "mkdir ${DESTINATION}"
if [ "$?" -ne 0 ]; then
    echo "Error when you tried to create the folder of the new website!"
    exit 1
fi

# Copy the website on the server.
scp -r $SOURCE* $WEBSITE_SERVER:$DESTINATION
if [ "$?" -ne 0 ]; then
    echo "Error when you tried to copy the new version on the server!"
    exit 1
fi

# Retrieve repositories folder in the last backup.
ssh $WEBSITE_SERVER "cp -r ${BACKUP_DESTINATION}/repositories ${DESTINATION}"
if [ "$?" -ne 0 ]; then
    echo "Error when you tried to retrieve the repositories folder!"
    exit 1
fi

# Come back at the root of the temp project.
cd ../..

# Generate maven site and deploy it.
mvn site site:deploy
if [ "$?" -ne 0 ]; then
    echo "Error when you tried to build or deploy maven site!"
    exit 1
fi

# Delete our temp folder.
cd .. && rm -rf $DIR
