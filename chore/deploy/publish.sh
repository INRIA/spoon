#!/bin/bash
# Deploy a beta versions according to the Maven convention: major.minor.patch-beta-number

echo "Deploying ..."

# Install dependencies
sudo apt-get install -y xmlstarlet

# Fetch SpoonBot GPG key
# made with symmetric key encryption algorithm AES256
# reference: https://docs.github.com/en/actions/security-guides/encrypted-secrets#limits-for-secrets
gpg --quiet --batch --yes --decrypt --passphrase="$SPOONBOT_PASSPHRASE" --output spoonbot.gpg chore/deploy/spoonbot.gpg.enc
gpg --fast-import spoonbot.gpg
KEY=`gpg --list-keys --with-colons | grep pub | cut -f5 -d: | tail -1`


function quick_fix_pom() {
# quickfix
cd spoon-pom

# see https://github.com/joel-costigliola/assertj-core/issues/1403#issuecomment-500100254
JAVADOC_PLUGIN="/_:project/_:profiles/_:profile[./_:id='release']/_:build/_:plugins/_:plugin[./_:artifactId='maven-javadoc-plugin']"
xmlstarlet sel -t -v $JAVADOC_PLUGIN  pom.xml
# xmlstarlet ed -L -s $JAVADOC_PLUGIN --type elem -n configuration pom.xml
xmlstarlet ed -L -s $JAVADOC_PLUGIN/_:configuration --type elem -n source -v 1.8 pom.xml
cd ..
}



# we do a normal release at the last bump commit
# this works the first time and will fail after
git reset --hard # clean
LAST_BUMP_COMMIT=`git --no-pager log --format=format:%H  -L 21,21:pom.xml | head -1`
echo LAST_BUMP_COMMIT $LAST_BUMP_COMMIT
git checkout $LAST_BUMP_COMMIT^1 # checking out the commit just before the bump
quick_fix_pom
xmlstarlet edit -L --update '/_:project/_:description' --value `git rev-parse HEAD` pom.xml
CURRENT_VERSION=`xmlstarlet sel -t -v '/_:project/_:version' pom.xml`
CURRENT_VERSION_NO_SNAPSHOT=`echo $CURRENT_VERSION | sed -e 's/-SNAPSHOT//'`
echo CURRENT_VERSION_NO_SNAPSHOT $CURRENT_VERSION_NO_SNAPSHOT
xmlstarlet edit -L --update '/_:project/_:version' --value $CURRENT_VERSION_NO_SNAPSHOT pom.xml
mvn -q clean deploy -DskipTests -Prelease -Dgpg.keyname=$KEY -DadditionalJOption=-Xdoclint:none
if [ $? -eq 0 ]; then
    echo pushing tag on github
    git checkout -b $CURRENT_VERSION_NO_SNAPSHOT
    git tag spoon-core-$CURRENT_VERSION_NO_SNAPSHOT
    git push origin

    # TODO create github release
fi

# now we release a beta version
git reset --hard # clean
git checkout master
quick_fix_pom


# adding a link to the commit
xmlstarlet edit -L --update '/_:project/_:description' --value `git rev-parse HEAD` pom.xml

CURRENT_VERSION=`xmlstarlet sel -t -v '/_:project/_:version' pom.xml`
CURRENT_VERSION_NO_SNAPSHOT=`echo $CURRENT_VERSION | sed -e 's/-SNAPSHOT//'`
echo CURRENT_VERSION_NO_SNAPSHOT $CURRENT_VERSION_NO_SNAPSHOT

curl "http://search.maven.org/solrsearch/select?q=a:spoon-core+g:fr.inria.gforge.spoon&rows=40&wt=json&core=gav" | jq -r ".response.docs | map(select(.v | match(\"sddf-beta\"))) | .[0] | .v"

LAST_BETA=`curl "http://search.maven.org/solrsearch/select?q=a:spoon-core+g:fr.inria.gforge.spoon&rows=40&wt=json&core=gav" | jq -r ".response.docs | map(select(.v | match(\"$CURRENT_VERSION_NO_SNAPSHOT-beta\"))) | .[0] | .v"`
echo $LAST_BETA

# better version, provides a default "1" is the last version if not a beta
LAST_BETA_NUMBER=`curl "http://search.maven.org/solrsearch/select?q=a:spoon-core+g:fr.inria.gforge.spoon&rows=40&wt=json&core=gav" | jq -r ".response.docs | map(.v) | map((match(\"$CURRENT_VERSION_NO_SNAPSHOT-beta-(.*)\") | .captures[0].string) // \"0\") | .[0]"`
echo LAST_BETA_NUMBER $LAST_BETA_NUMBER

NEW_BETA_NUMBER=$((LAST_BETA_NUMBER+1))
echo NEW_BETA_NUMBER $NEW_BETA_NUMBER

# we push a beta
PUSHED_VERSION=$CURRENT_VERSION_NO_SNAPSHOT-beta-$NEW_BETA_NUMBER
echo deploying $PUSHED_VERSION
xmlstarlet edit -L --update '/_:project/_:version' --value $PUSHED_VERSION pom.xml
mvn -q clean deploy -DskipTests -Prelease --settings chore/deploy/settings.xml -Dgpg.keyname=$KEY -DadditionalJOption=-Xdoclint:none
echo "Well deployed!"
