#!/bin/bash
#
# Compiles an open-source project, spoons the project, runs the tests
# and checks at each step if there aren't errors. To execute this
# script, create a job in jenkins.
#
#
# Typical usage:
#
# $ cd my-maven-project-with-pom
# $ curl http://spoon.gforge.inria.fr/jenkins/build.sh | bash

# Allow to define some options to the maven command, such as debug or memory options
# -Drat.skip=true is for skipping license check which fails on some project incl. https://ci.inria.fr/sos/job/Commons-lang/
MAVEN_COMMAND="mvn $MVN_OPTS -Dmaven.javadoc.skip=true -Drat.skip=true -Denforcer.skip=true "

echo " "
echo "-------------------------------------------------------"
echo "Initializes project"
echo "-------------------------------------------------------"
echo " "

# required if workspace has not been cleaned
git reset --hard

# Gets some information from pom.xml.
ARTIFACT_ID=$(xmlstarlet sel -N x="http://maven.apache.org/POM/4.0.0" -t -v "/x:project/x:artifactId" pom.xml)
VERSION=$(xmlstarlet sel -N x="http://maven.apache.org/POM/4.0.0" -t -v "/x:project/x:version" pom.xml)
MODULES_JOB=$(cat pom.xml | grep "<modules>")

if [ -z "$MODULES_JOB" ]; then
	MODULES_JOB="./"
else
	MODULES_JOB=$(xmlstarlet sel -N x="http://maven.apache.org/POM/4.0.0" -T -t -m "/x:project/x:modules/x:module" -v "." -o "/" -n pom.xml)
fi
# Gets some information from git.
VERSION_ID=$(git rev-parse HEAD)

# Removes checkstyle plugin because spoon don't make beautiful code in output.
HAS_CHECKSTYLE=$(cat pom.xml | grep "maven-checkstyle-plugin")
if [ ! -z HAS_CHECKSTYLE ]; then
	xmlstarlet ed -N x="http://maven.apache.org/POM/4.0.0" -d "/x:project/x:build/x:plugins/x:plugin[x:artifactId='maven-checkstyle-plugin']" pom.xml > pom.bak.xml
	mv pom.bak.xml pom.xml
fi

# Removes enforcer plugin because we would like specify our personnal repository.
HAS_ENFORCER=$(cat pom.xml | grep "maven-enforcer-plugin")
if [ ! -z HAS_ENFORCER ]; then
	xmlstarlet ed -N x="http://maven.apache.org/POM/4.0.0" -d "/x:project/x:build/x:plugins/x:plugin[x:artifactId='maven-enforcer-plugin']" pom.xml > pom.bak.xml
	mv pom.bak.xml pom.xml
fi


# Displays variables used in the build.
echo ""
echo "--- Displays variables used in the build ---"
echo "Artifact id: $ARTIFACT_ID"
echo "Version: $VERSION"
echo "Modules job:"
for module in ${MODULES_JOB// / }; do
	if [ "$module" = "./" ]; then
		echo "- "${JOB_NAME}
	else
		echo "- "${module}q
	fi
done
if [ ! -z HAS_CHECKSTYLE ]; then
	echo "Has checkstyle: true"
else
	echo "Has checkstyle: false"
fi
if [ ! -z HAS_ENFORCER ]; then
	echo "Has enforcer: true"
else
	echo "Has enforcer: false"
fi
echo "Git version id: $VERSION_ID"
## maven uses the Java from JAVA_HOME, not from the home
echo "Java home: $JAVA_HOME"
JAVA_VERSION=`$JAVA_HOME/bin/java -version  2>&1`
echo "Java version: $JAVA_VERSION"

echo " "
echo "-------------------------------------------------------"
echo "Compiles project (at the root project if it's a multi module project) without spoon"
echo "-------------------------------------------------------"
echo " "

# Compiles project.
START_COMPILE_PROJECT=$(($(date +%s%N)/1000000))
$MAVEN_COMMAND clean install
if [ "$?" -ne 0 ]; then
    echo "Error: Maven compile original project unsuccessful!"
    exit 1
fi
END_COMPILE_PROJECT=$(($(date +%s%N)/1000000))
DIFF_COMPILE_PROJECT=$(echo "$END_COMPILE_PROJECT - $START_COMPILE_PROJECT" | bc)

# Saves the report at the root of the project.
for module in ${MODULES_JOB// / }; do
	REPORT_DIRECTORY=${module}"target/surefire-reports"
	module_name=$(echo ${module} | tr -d '/ ')

	if [ -d "$REPORT_DIRECTORY" ]; then
		NB_TESTS_COMPILE=0
		NB_ERRORS_COMPILE=0
		NB_SKIPPED_COMPILE=0
		NB_FAILURES_COMPILE=0
		for i in `ls $REPORT_DIRECTORY/TEST-*.xml`; do
			tests=$(xmlstarlet sel -t -v "/testsuite/@tests" $i)
			if [ "$?" -eq 0 ]; then
				NB_TESTS_COMPILE=$((NB_TESTS_COMPILE+$tests))
			fi
			errors=$(xmlstarlet sel -t -v "/testsuite/@errors" $i)
			if [ "$?" -eq 0 ]; then
				NB_ERRORS_COMPILE=$((NB_ERRORS_COMPILE+$errors))
			fi
			skip=$(xmlstarlet sel -t -v "/testsuite/@skipped" $i)
			if [ "$?" -eq 0 ]; then
				NB_SKIPPED_COMPILE=$((NB_SKIPPED_COMPILE+$skip))
			fi
			failures=$(xmlstarlet sel -t -v "/testsuite/@failures" $i)
			if [ "$?" -eq 0 ]; then
				NB_FAILURES_COMPILE=$((NB_FAILURES_COMPILE+$failures))
			fi
		done
		echo -e "tests: $NB_TESTS_COMPILE\nerrors: $NB_ERRORS_COMPILE\nskipped: $NB_SKIPPED_COMPILE\nfailures: $NB_FAILURES_COMPILE" > result-spoon-tests-${module_name}.txt
	fi
done

# Displays results of the maven compile.
echo " "
echo "--- Displays results of the maven compile ---"
echo "Time to compile: $DIFF_COMPILE_PROJECT"
echo "Number of tests: $NB_TESTS_COMPILE"
echo "Number of tests skipped: $NB_SKIPPED_COMPILE"
echo "Number of failures in tests: $NB_FAILURES_COMPILE"
echo "Number of errors in tests: $NB_ERRORS_COMPILE"

echo " "
echo "-------------------------------------------------------"
echo "Inserts the maven plugin to spoon the project"
echo "-------------------------------------------------------"
echo " "

# Edits pom xml to prepare project to spoon project.
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project" --type elem -n pluginRepositories -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:pluginRepositories" --type elem -n pluginRepository -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:pluginRepositories/x:pluginRepository[last()]" --type elem -n id -v "spoon-snapshot" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:pluginRepositories/x:pluginRepository[last()]" --type elem -n name -v "Maven repository for Spoon Snapshot" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:pluginRepositories/x:pluginRepository[last()]" --type elem -n url -v "https://oss.sonatype.org/content/repositories/snapshots/" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:pluginRepositories/x:pluginRepository[last()]" --type elem -n snapshots -v "" pom.xml

xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:pluginRepositories" --type elem -n pluginRepository -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:pluginRepositories/x:pluginRepository[last()]" --type elem -n id -v "spoon-beta" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:pluginRepositories/x:pluginRepository[last()]" --type elem -n name -v "Maven repository for Spoon beta" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:pluginRepositories/x:pluginRepository[last()]" --type elem -n url -v "https://repo1.maven.org/maven2/" pom.xml


xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins" --type elem -n plugin -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]" --type elem -n groupId -v "fr.inria.gforge.spoon" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]" --type elem -n artifactId -v "spoon-maven-plugin" pom.xml

# we depend on the latest version of spoon-maven-plugin) 
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]" --type elem -n version -v "3.8.0" pom.xml 

xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]" --type elem -n executions -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:executions" --type elem -n execution -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:executions/x:execution" --type elem -n phase -v "generate-sources" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:executions/x:execution" --type elem -n goals -v "" pom.xml
# we generate the sources separately
#xmlstarlet ed -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:executions/x:execution/x:goals" --type elem -n goal -v "generate" pom.bak8.xml > pom.bak9.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]" --type elem -n configuration -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:configuration" --type elem -n processors -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:configuration/x:processors" --type elem -n processor -v "spoon.processing.SpoonTagger" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]" --type elem -n dependencies -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:dependencies" --type elem -n dependency -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:dependencies/x:dependency[last()]" --type elem -n groupId -v "fr.inria.gforge.spoon" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:dependencies/x:dependency[last()]" --type elem -n artifactId -v "spoon-core" pom.xml

## note: before we used ranges [10.0.O,), but this does not work in plugins for some obscure reason
# so we have to inject the latest version  instead
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:build/x:plugins/x:plugin[last()]/x:dependencies/x:dependency[last()]" --type elem -n version -v $(curl -L "https://www.monperrus.net/martin/last-version-maven.py?groupId=fr.inria.gforge.spoon&artifactId=spoon-core") pom.xml

# we add the spoon-core dependency to the project, resolve the ranges and remove it. The resolve ranges plugin only works for dependencies of the project, but not for plugins. But the rewrite engine of the plugin also updates the plugin version.
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:dependencies" -t elem -n dependency -v "" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:dependencies/x:dependency[last()]" --type elem -n groupId -v "fr.inria.gforge.spoon" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:dependencies/x:dependency[last()]" --type elem -n artifactId -v "spoon-core" pom.xml
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -s "/x:project/x:dependencies/x:dependency[last()]" --type elem -n version -v "[7.0.0-SNAPSHOT,)" pom.xml
$MAVEN_COMMAND versions:resolve-ranges -DallowSnapshots=true
# remove the dependency again
xmlstarlet ed -L -N x="http://maven.apache.org/POM/4.0.0" -d "/x:project/x:dependencies/x:dependency[last()]" pom.xml
# Purge the project from snapshots
# Avoid to use an old snapshot of Spoon and force the resolution
$MAVEN_COMMAND dependency:purge-local-repository -DmanualInclude="fr.inria.gforge.spoon:spoon-core" -DsnapshotsOnly=true

# Compiles project with spoon configuration.
START_COMPILE_WITH_SPOON=$(($(date +%s%N)/1000000))
$MAVEN_COMMAND clean test -DskipTests
if [ "$?" -ne 0 ]; then
    echo "Error: Maven compile with spoon unsuccessful!"
    exit 1
fi
END_COMPILE_WITH_SPOON=$(($(date +%s%N)/1000000))
DIFF_WITH_SPOON=$(echo "$END_COMPILE_WITH_SPOON - $START_COMPILE_WITH_SPOON" | bc)

# creating the sources in target/generated-sources
mvn fr.inria.gforge.spoon::spoon-maven-plugin:generate
for module in ${MODULES_JOB// / }; do
        if [ ! -f ${module}"target/generated-sources/spoon/spoon/Spoon.java" ]; then 
            echo "ERROR: no tag class, spoon has failed"
            exit -1
        fi
done

# Overwrites source folder.
for module in ${MODULES_JOB// / }; do
	GENERATED_DIRECTORY=${module}"target/generated-sources/spoon/"
	if [ -d "${module}src/main/java" ]; then
		echo normal maven case
		cp -Rf ${GENERATED_DIRECTORY}* ${module}src/main/java
		git diff src | head -100
	else	
		if [ -d "${module}src" ]; then
			cp -Rf ${GENERATED_DIRECTORY}* ${module}src
		fi
	fi
done

# Compiles project with source spooned.
START_COMPILE_SPOON_SPOON=$(($(date +%s%N)/1000000))
$MAVEN_COMMAND clean install
if [ "$?" -ne 0 ]; then
    echo "Error: Maven compile with spoon unsuccessful!"
    exit 1
fi
END_COMPILE_SPOON_SPOON=$(($(date +%s%N)/1000000))
DIFF_SPOON_SPOON=$(echo "$END_COMPILE_SPOON_SPOON - $START_COMPILE_SPOON_SPOON" | bc)

# Displays results of the maven compile.
echo " "
echo "--- Displays results of the maven compile with spoon of spoon ---"
echo "Time to compile with spoon of spoon: $DIFF_SPOON_SPOON"

# Saves the report at the root of the project.
for module in ${MODULES_JOB// / }; do
	REPORT_DIRECTORY=${module}"target/surefire-reports"
	module_name=$(echo ${module} | tr -d '/ ')

	if [ -d "$REPORT_DIRECTORY" ]; then
		NB_TESTS_SPOON=0
		NB_ERRORS_SPOON=0
		NB_SKIPPED_SPOON=0
		NB_FAILURES_SPOON=0
		for i in `ls $REPORT_DIRECTORY/TEST-*.xml`; do
			tests=$(xmlstarlet sel -t -v "/testsuite/@tests" $i)
			if [ "$?" -eq 0 ]; then
				NB_TESTS_SPOON=$((NB_TESTS_SPOON+$tests))
			fi
			errors=$(xmlstarlet sel -t -v "/testsuite/@errors" $i)
			if [ "$?" -eq 0 ]; then
				NB_ERRORS_SPOON=$((NB_ERRORS_SPOON+$errors))
			fi
			skip=$(xmlstarlet sel -t -v "/testsuite/@skipped" $i)
			if [ "$?" -eq 0 ]; then
				NB_SKIPPED_SPOON=$((NB_SKIPPED_SPOON+$skip))
			fi
			failures=$(xmlstarlet sel -t -v "/testsuite/@failures" $i)
			if [ "$?" -eq 0 ]; then
				NB_FAILURES_SPOON=$((NB_FAILURES_SPOON+$failures))
			fi
		done
		echo -e "tests: $NB_TESTS_SPOON\nerrors: $NB_ERRORS_SPOON\nskipped: $NB_SKIPPED_SPOON\nfailures: $NB_FAILURES_SPOON" > result-spoon-tests-spooned-${module_name}.txt
	fi
done

# Displays results of the maven compile.
echo " "
echo "--- Displays results of the maven compile with spoon ---"
echo "Time to compile with spoon: $DIFF_WITH_SPOON"
echo "Number of tests: $NB_TESTS_SPOON"
echo "Number of tests skipped: $NB_SKIPPED_SPOON"
echo "Number of failures in tests: $NB_FAILURES_SPOON"
echo "Number of errors in tests: $NB_ERRORS_SPOON"

if [ ! "$NB_TESTS_SPOON" -eq "$NB_TESTS_COMPILE" ]; then
    echo "Error: Tests aren't equals between original compile and spoon compile!"
    exit 1
fi

if [ ! "$NB_SKIPPED_SPOON" -eq "$NB_SKIPPED_COMPILE" ]; then
    echo "Error: Tests skipped aren't equals between original compile and spoon compile!"
    exit 1
fi

if [ ! "$NB_FAILURES_SPOON" -eq "$NB_FAILURES_COMPILE" ]; then
    echo "Error: Tests failures aren't equals between original compile and spoon compile!"
    exit 1
fi

if [ ! "$NB_ERRORS_SPOON" -eq "$NB_ERRORS_COMPILE" ]; then
    echo "Error: Tests errors aren't equals between original compile and spoon compile!"
    exit 1
fi


echo " "
echo "-------------------------------------------------------"
echo "Retrieves all results files in the project (in all sub modules if the project is a multi module project)"
echo "-------------------------------------------------------"
echo " "

find . -name "result-spoon-tests-*"

# Moves all results file in target directory.
for module in ${MODULES_JOB// / }; do
	module_name=$(echo ${module} | tr -d '/ ')
	OUTPUT_DIRECTORY="${module}target/spoon-reports"
	if [ -f "result-spoon-tests-${module_name}.txt" ]; then 
		mkdir $OUTPUT_DIRECTORY
		mv result-spoon-tests-${module_name}.txt $OUTPUT_DIRECTORY/result-spoon-tests.txt
		mv result-spoon-tests-spooned-${module_name}.txt $OUTPUT_DIRECTORY/result-spoon-tests-spooned.txt
	fi
done

if [ ! -d "target/spoon-reports" ]; then
	mkdir -p target/spoon-reports
fi

# Creates result file.
echo -e "<?xml version=\"1.0\"?>\n<section name=\"\"/>" > result-spoon.xml
xmlstarlet ed -s "/section" --type elem -n table -v "" result-spoon.xml > result-spoon.bak.xml
xmlstarlet ed -s "/section/table" --type elem -n tr -v "" result-spoon.bak.xml > result-spoon.bak2.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Project" result-spoon.bak2.xml > result-spoon.bak3.xml
xmlstarlet ed -s "/section/table/tr/td" --type attr -n fontattribute -v "bold" result-spoon.bak3.xml > result-spoon.bak4.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Commit id version" result-spoon.bak4.xml > result-spoon.bak5.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak5.xml > result-spoon.bak6.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Project compiles" result-spoon.bak6.xml > result-spoon.bak7.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak7.xml > result-spoon.bak8.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Project tests run" result-spoon.bak8.xml > result-spoon.bak9.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak9.xml > result-spoon.bak10.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Project spooned compiles" result-spoon.bak10.xml > result-spoon.bak11.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak11.xml > result-spoon.bak12.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Project spooned tests run" result-spoon.bak12.xml > result-spoon.bak13.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak13.xml > result-spoon.bak14.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Time to spoon" result-spoon.bak14.xml > result-spoon.bak15.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak15.xml > result-spoon.bak16.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Number of CtStatment" result-spoon.bak16.xml > result-spoon.bak17.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak17.xml > result-spoon.bak18.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Time to compile project" result-spoon.bak18.xml > result-spoon.bak19.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak19.xml > result-spoon.bak20.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Time to compile project spooned" result-spoon.bak20.xml > result-spoon.bak21.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak21.xml > result-spoon.bak22.xml
xmlstarlet ed -s "/section/table/tr" --type elem -n td -v "Spoon(Spoon(x))=Spoon(x)" result-spoon.bak22.xml > result-spoon.bak23.xml
xmlstarlet ed -s "/section/table/tr/td[last()]" --type attr -n fontattribute -v "bold" result-spoon.bak23.xml > result-spoon.bak24.xml
mv result-spoon.bak24.xml result-spoon.xml
rm result-spoon.bak*.xml

# Saves results.
cp result-spoon.xml result-spoon.bak0.xml
counter=1
for module in ${MODULES_JOB// / }; do
	result_file="$(find "${module}target/spoon-maven-plugin" -maxdepth 1 -type f -name "result-spoon-*.xml")"
	if [ -z "$result_file" ]; then
        continue
	fi
	module_name=$(echo ${module} | tr -d '/ ')
	xmlstarlet ed -s "/section/table" --type elem -n tr -v "" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	counter=$((counter+1))
	if [ "$module" = "./" ]; then
		xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "${JOB_NAME}" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	else
		xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "${module_name}" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	fi
	counter=$((counter+1))
	xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "$VERSION_ID" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	counter=$((counter+1))
	xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "OK" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	counter=$((counter+1))
	if [ -f "${module}target/spoon-reports/result-spoon-tests.txt" ]; then
		xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "$(cat ${module}target/spoon-reports/result-spoon-tests.txt)" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	else
		xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "N/A" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	fi
	counter=$((counter+1))
	xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "OK" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	counter=$((counter+1))
	if [ -f "${module}target/spoon-reports/result-spoon-tests-spooned.txt" ]; then
		xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "$(cat ${module}target/spoon-reports/result-spoon-tests-spooned.txt)" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	else
		xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "N/A" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	fi
	counter=$((counter+1))
	xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "$(xmlstarlet sel -t -v "/project/performance" ${result_file})" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	counter=$((counter+1))
	if [ -f "target/spoon-maven-plugin/spoon-nb-statement.txt" ]; then
		# Computes number of CtStatement.
		NB_CTSTATEMENT=0
		for line in $(cat target/spoon-maven-plugin/spoon-nb-statement.txt); do
			NB_CTSTATEMENT=$(($NB_CTSTATEMENT+$line))
		done
		xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "$NB_CTSTATEMENT" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	else
		xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "N/A" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	fi
	counter=$((counter+1))
	xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "$DIFF_COMPILE_PROJECT" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	counter=$((counter+1))
	xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "$DIFF_WITH_SPOON" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	counter=$((counter+1))
	xmlstarlet ed -s "/section/table/tr[last()]" --type elem -n td -v "$DIFF_SPOON_SPOON" result-spoon.bak$((counter-1)).xml > result-spoon.bak$counter.xml
	counter=$((counter+1))
done
mv result-spoon.bak$((counter-1)).xml result-spoon.xml
rm result-spoon.bak*.xml
mv result-spoon.xml target/spoon-reports/result-spoon.xml
cat target/spoon-reports/result-spoon.xml

exit 0
