#!/bin/bash
#
# Deploys a new release of Spoon. This script uses the maven
# release plugin to deploy the project or rollback if we got any
# error during the deployment.
# Note that this script doesn't make the git clone. Think to do it by yourself
# or to execute this script in a Spoon repository.

USER_SERVER="spoon-bot"
SERVER="${USER_SERVER}@scm.gforge.inria.fr"
HOST_DESTINATION="/home/groups/spoon/"
FOLDER_DESTINATION="spoon.git"
DESTINATION="${HOST_DESTINATION}${FOLDER_DESTINATION}/"

# Determine the type.

MAJOR=0
MINOR=1
PATCH=2
TYPES=('major' 'minor' 'patch')
if [ -z "$TYPE" ]; then
	TYPE='minor'
fi
if ! [[ ${TYPES[*]} =~ "$TYPE" ]]; then
	echo "Error: type should be 'major', 'minor' or 'patch'!"
	exit 1
fi
if [[ ${TYPES[$PATCH]} = $TYPE ]]; then
	git checkout stable
fi

# Save the current release version.

RELEASE_TEXT=$(grep "^spoon_release:" doc/_jekyll/_config.yml | cut -d' ' -f2-)
REGEX="^\"([0-9]+).([0-9]+).([0-9]+)\"$"
if [[ $RELEASE_TEXT =~ $REGEX ]]; then
	RVERSIONS[0]="${BASH_REMATCH[1]}"
	RVERSIONS[1]="${BASH_REMATCH[2]}"
	RVERSIONS[2]="${BASH_REMATCH[3]}"
else
	echo "Error: Can't get the last release version from jekyll config file."
	exit 1
fi

# Save the next version.

if [[ ${TYPES[$MAJOR]} = $TYPE ]]; then
	NSVERSIONS[0]=$((RVERSIONS[0] + 1))
	NSVERSIONS[1]=1
	NSVERSIONS[2]=0
	NRVERSIONS[0]=$((RVERSIONS[0] + 1))
	NRVERSIONS[1]=0
	NRVERSIONS[2]=0
elif [[ ${TYPES[$MINOR]} = $TYPE ]]; then
	NSVERSIONS[0]=${RVERSIONS[0]}
	NSVERSIONS[1]=$((RVERSIONS[1] + 2))
	NSVERSIONS[2]=0
	NRVERSIONS[0]=${RVERSIONS[0]}
	NRVERSIONS[1]=$((RVERSIONS[1] + 1))
	NRVERSIONS[2]=0
elif [[ ${TYPES[$PATCH]} = $TYPE ]]; then
	NSVERSIONS[0]=${RVERSIONS[0]}
	NSVERSIONS[1]=$((RVERSIONS[1] + 1))
	NSVERSIONS[2]=0
	NRVERSIONS[0]=${RVERSIONS[0]}
	NRVERSIONS[1]=${RVERSIONS[1]}
	NRVERSIONS[2]=$((RVERSIONS[2] + 1))
fi

OLD_RELEASE="${RVERSIONS[0]}.${RVERSIONS[1]}.${RVERSIONS[2]}"
NEXT_SNAPSHOT="${NSVERSIONS[0]}.${NSVERSIONS[1]}.${NSVERSIONS[2]}-SNAPSHOT"
NEXT_RELEASE="${NRVERSIONS[0]}.${NRVERSIONS[1]}.${NRVERSIONS[2]}"
TAG="spoon-core-$NEXT_RELEASE"

echo "You'll create a $TYPE version $NEXT_RELEASE and the next snapshot will be $NEXT_SNAPSHOT"

# Release to Maven Central.

mvn release:clean
if [ "$?" -ne 0 ]; then
    echo "Can't clean the project for the release!"
    mvn release:rollback
    if [ "$?" -ne 0 ]; then
	    echo "Can't rollback at the clean step!"
	fi
    exit 1
fi

mvn release:prepare -DreleaseVersion=$NEXT_RELEASE -DdevelopmentVersion=$NEXT_SNAPSHOT -Dtag=$TAG
if [ "$?" -ne 0 ]; then
    echo "Can't prepare the project for the release!"
    mvn release:rollback
    if [ "$?" -ne 0 ]; then
	    echo "Can't rollback at the prepare step!"
	fi
    exit 1
fi

mvn release:perform -Dusername=$USER_SERVER
if [ "$?" -ne 0 ]; then
    echo "Can't perform the project for the release!"
    mvn release:rollback
    if [ "$?" -ne 0 ]; then
	    echo "Can't rollback at the perform step!"
	fi
    exit 1
fi

# Updates Jekyll documentation.

sed -i -re "s/^spoon_release: \"[0-9]+.[0-9]+.[0-9]+\"/spoon_release: \"$NEXT_RELEASE\"/;s/^sidebar_version: version [0-9]+.[0-9]+.[0-9]+/sidebar_version: version $NEXT_RELEASE/;s/^spoon_snapshot: \"[0-9]+.[0-9]+.[0-9]+-SNAPSHOT\"/spoon_snapshot: \"$NEXT_SNAPSHOT\"/" doc/_config.yml
if [ "$?" -ne 0 ]; then
	echo "Can't update new versions in the jekyll config file."
	echo "rollback at the previous state..."
	git checkout doc/_config.yml
	exit 1
fi

DATE=$(date +"%B %d, %Y: Spoon $NEXT_RELEASE is released.")
DATE="$(tr '[:lower:]' '[:upper:]' <<< ${DATE:0:1})${DATE:1}"
DATE="- $DATE"
awk -i inplace -v date="$DATE" '{print} /^<!-- .* Marker comment. -->$/ {print date}' doc/doc_homepage.md
if [ "$?" -ne 0 ]; then
	echo "Can't update news feed in the website."
	echo "rollback at the previous state..."
	git checkout doc/doc_homepage.md
	exit 1
fi

# Updates Readme.

sed -i -re "s/<version>$OLD_RELEASE<\/version>/<version>$NEXT_RELEASE<\/version>/;s/<version>[0-9]+.[0-9]+.[0-9]+-SNAPSHOT<\/version>/<version>$NEXT_SNAPSHOT<\/version>/" README.md
if [ "$?" -ne 0 ]; then
	echo "Can't update new versions in the README file."
	echo "rollback at the previous state..."
	git checkout README.md
	exit 1
fi

# Commit changes.

echo "What is changes?"

git diff
git add . --all
git commit -m "docs(version): Updates documentation."
if [[ ${TYPES[$PATCH]} = $TYPE ]]; then
    git push origin stable
else
    git push origin master
fi

# Updates stable branch.

if [[ ${TYPES[$PATCH]} != $TYPE ]]; then
    git checkout master
    git branch -D stable
    git checkout -b stable
    git push origin stable -f
fi

# Retrieves all commits and tag from GitHub repo to INRIA Forge.

ssh -A $SERVER "cd ${DESTINATION} && git fetch origin master:master"
if [ "$?" -ne 0 ]; then
    echo "Error when you fetch sources from GitHub for the master branch!"
    exit 1
fi

ssh -A $SERVER "cd ${DESTINATION} && git fetch origin stable:stable"
if [ "$?" -ne 0 ]; then
    echo "Error when you fetch sources from GitHub for the stable branch!"
    exit 1
fi

ssh -A $SERVER "cd ${DESTINATION} && git fetch --tags"
if [ "$?" -ne 0 ]; then
    echo "Error when you fetch tags!"
    exit 1
fi

echo RELEASE="$NEXT_RELEASE" > target/variables.properties
echo SNAPSHOT="$NEXT_SNAPSHOT" >> target/variables.properties
