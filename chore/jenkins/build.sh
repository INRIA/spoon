#!/bin/bash
#
# Compiles an open-source project, spoons the project, spoons source code
# spooned and checks each step if there aren't errors. To execute this
# script, create a job in jenkins with the dockerfile given in the same
# repository and launch the jobs. The first part of this script isn't
# necessary if you use the plugin of git. You can retreives results of
# this script in the target/spoon-reports/result-spoon.xml file of the
# current open-source project.

# =============================================
# This part isn't necessary in the jenkins because git plugin make it better!
# =============================================
GIT_CLONE="https://github.com/Organization/Repository.git"
GIT_NAME="repository-jenkins"
JOB_NAME=$GIT_NAME

# Removes project if it exists yet.
if [ -d "$GIT_NAME" ]; then
	rm -rf $GIT_NAME
fi

# Clones the repository
git clone $GIT_CLONE $GIT_NAME
if [ "$?" -ne 0 ]; then
    echo "Git clone $GIT_CLONE unsuccessful!"
    exit 1
fi

# Enters in the repository.
cd $GIT_NAME/

echo " "
echo "-------------------------------------------------------"
echo "Initizalizes project"
echo "-------------------------------------------------------"
echo " "

# Gets some information from pom.xml.
ARTIFACT_ID=$(xmlstarlet sel -t -v "/_:project/_:artifactId" pom.xml)
VERSION=$(xmlstarlet sel -t -v "/_:project/_:version" pom.xml)
MODULES_JOB=$(cat pom.xml | grep "<modules>")
if [ -z "$MODULES_JOB" ]; then
	MODULES_JOB[0]="./"
else
	MODULES_JOB=$(xmlstarlet sel -T -t -m "/_:project/_:modules/_:module" -v "." -o "/" -n pom.xml)
fi

# Gets some information from git.
VERSION_ID=$(git rev-parse HEAD)

# Removes checkstyle plugin because spoon don't make beautiful code in output.
HAS_CHECKSTYLE=$(cat pom.xml | grep "maven-checkstyle-plugin")
if [ ! -z HAS_CHECKSTYLE ]; then
	xmlstarlet ed -d "/_:project/_:build/_:plugins/_:plugin[_:artifactId='maven-checkstyle-plugin']" pom.xml > pom.bak.xml
	mv pom.bak.xml pom.xml
fi

# A Java version can be parametrized in Job configuration.
if [ -z JAVA_VERSION ]; then
	JAVA_VERSION="8"
	JAVA_JVM="/usr/lib/jvm/java-8-oracle"
elif [ "$JAVA_VERSION" = "7" ]; then
	JAVA_VERSION="7"
	JAVA_JVM="/usr/lib/jvm/java-7-openjdk-amd64"
else
	JAVA_VERSION="8"
	JAVA_JVM="/usr/lib/jvm/java-8-oracle"
fi
export JAVA_HOME=$JAVA_JVM

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
		echo "- "${module}
	fi
done
if [ ! -z HAS_CHECKSTYLE ]; then
	echo "Has checkstyle: true"
else
	echo "Has checkstyle: false"
fi
echo "Git version id: $VERSION_ID"
echo "Java version: $JAVA_VERSION"
echo "Java home: $JAVA_HOME"

echo " "
echo "-------------------------------------------------------"
echo "Compiles project (at the root project if it's a multi module project) without spoon"
echo "-------------------------------------------------------"
echo " "

# Compiles project.
START_COMPILE_PROJECT=$(($(date +%s%N)/1000000))
mvn clean install -fn
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
		NB_TESTS=0
		NB_ERRORS=0
		NB_SKIPPED=0
		NB_FAILURES=0
		for i in `ls $REPORT_DIRECTORY/TEST-*.xml`; do
			tests=$(xmlstarlet sel -t -v "/testsuite/@tests" $i)
			if [ "$?" -eq 0 ]; then
				NB_TESTS=$((NB_TESTS+$tests))
			fi
			errors=$(xmlstarlet sel -t -v "/testsuite/@errors" $i)
			if [ "$?" -eq 0 ]; then
				NB_ERRORS=$((NB_ERRORS+$errors))
			fi
			skip=$(xmlstarlet sel -t -v "/testsuite/@skipped" $i)
			if [ "$?" -eq 0 ]; then
				NB_SKIPPED=$((NB_SKIPPED+$skip))
			fi
			failures=$(xmlstarlet sel -t -v "/testsuite/@failures" $i)
			if [ "$?" -eq 0 ]; then
				NB_FAILURES=$((NB_FAILURES+$failures))
			fi
		done
		echo -e "tests: $NB_TESTS\nerrors: $NB_ERRORS\nskipped: $NB_SKIPPED\nfailures: $NB_FAILURES" > result-spoon-tests-${module_name}.txt
	fi
done

# Displays results of the maven compile.
echo " "
echo "--- Displays results of the maven compile ---"
echo "Time to compile: $DIFF_COMPILE_PROJECT"
echo "Number of tests: $NB_TESTS"
echo "Number of tests skipped: $NB_SKIPPED"
echo "Number of failures in tests: $NB_FAILURES"
echo "Number of errors in tests: $NB_ERRORS"

echo " "
echo "-------------------------------------------------------"
echo "Inserts the maven plugin to spoon the project"
echo "-------------------------------------------------------"
echo " "

# Edits pom xml to prepare project to spoon project.
xmlstarlet ed -s "/_:project/_:build/_:plugins" --type elem -n plugin -v "" pom.xml > pom.bak.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]" --type elem -n groupId -v "fr.inria.gforge.spoon" pom.bak.xml > pom.bak2.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]" --type elem -n artifactId -v "spoon-maven-plugin" pom.bak2.xml > pom.bak3.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]" --type elem -n version -v "3.0-SNAPSHOT" pom.bak3.xml > pom.bak4.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]" --type elem -n executions -v "" pom.bak4.xml > pom.bak5.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:executions" --type elem -n execution -v "" pom.bak5.xml > pom.bak6.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:executions/_:execution" --type elem -n phase -v "generate-sources" pom.bak6.xml > pom.bak7.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:executions/_:execution" --type elem -n goals -v "" pom.bak7.xml > pom.bak8.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:executions/_:execution/_:goals" --type elem -n goal -v "generate" pom.bak8.xml > pom.bak9.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]" --type elem -n configuration -v "" pom.bak9.xml > pom.bak10.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:configuration" --type elem -n processors -v "" pom.bak10.xml > pom.bak11.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:configuration/_:processors" --type elem -n processor -v "fr.inria.gforge.spoon.processors.CountStatementProcessor" pom.bak11.xml > pom.bak12.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:configuration" --type elem -n compliance -v "${JAVA_VERSION}" pom.bak12.xml > pom.bak13.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]" --type elem -n dependencies -v "" pom.bak13.xml > pom.bak14.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:dependencies" --type elem -n dependency -v "" pom.bak14.xml > pom.bak15.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:dependencies/_:dependency" --type elem -n groupId -v "fr.inria.gforge.spoon" pom.bak15.xml > pom.bak16.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:dependencies/_:dependency" --type elem -n artifactId -v "spoon-processors" pom.bak16.xml > pom.bak17.xml
xmlstarlet ed -s "/_:project/_:build/_:plugins/_:plugin[last()]/_:dependencies/_:dependency" --type elem -n version -v "1.0-SNAPSHOT" pom.bak17.xml > pom.bak18.xml
mv pom.bak18.xml pom.xml
rm pom.bak*.xml

# Compiles project with spoon configuration.
START_COMPILE_WITH_SPOON=$(($(date +%s%N)/1000000))
mvn clean install -fn
if [ "$?" -ne 0 ]; then
    echo "Error: Maven compile with spoon unsuccessful!"
    exit 1
fi
END_COMPILE_WITH_SPOON=$(($(date +%s%N)/1000000))
DIFF_WITH_SPOON=$(echo "$END_COMPILE_WITH_SPOON - $START_COMPILE_WITH_SPOON" | bc)

# Saves the report at the root of the project.
for module in ${MODULES_JOB// / }; do
	REPORT_DIRECTORY=${module}"target/surefire-reports"
	module_name=$(echo ${module} | tr -d '/ ')

	if [ -d "$REPORT_DIRECTORY" ]; then
		NB_TESTS=0
		NB_ERRORS=0
		NB_SKIPPED=0
		NB_FAILURES=0
		for i in `ls $REPORT_DIRECTORY/TEST-*.xml`; do
			tests=$(xmlstarlet sel -t -v "/testsuite/@tests" $i)
			if [ "$?" -eq 0 ]; then
				NB_TESTS=$((NB_TESTS+$tests))
			fi
			errors=$(xmlstarlet sel -t -v "/testsuite/@errors" $i)
			if [ "$?" -eq 0 ]; then
				NB_ERRORS=$((NB_ERRORS+$errors))
			fi
			skip=$(xmlstarlet sel -t -v "/testsuite/@skipped" $i)
			if [ "$?" -eq 0 ]; then
				NB_SKIPPED=$((NB_SKIPPED+$skip))
			fi
			failures=$(xmlstarlet sel -t -v "/testsuite/@failures" $i)
			if [ "$?" -eq 0 ]; then
				NB_FAILURES=$((NB_FAILURES+$failures))
			fi
		done
		echo -e "tests: $NB_TESTS\nerrors: $NB_ERRORS\nskipped: $NB_SKIPPED\nfailures: $NB_FAILURES" > result-spoon-tests-spooned-${module_name}.txt
	fi
done

# Displays results of the maven compile.
echo " "
echo "--- Displays results of the maven compile with spoon ---"
echo "Time to compile with spoon: $DIFF_WITH_SPOON"
echo "Number of tests: $NB_TESTS"
echo "Number of tests skipped: $NB_SKIPPED"
echo "Number of failures in tests: $NB_FAILURES"
echo "Number of errors in tests: $NB_ERRORS"

echo " "
echo "-------------------------------------------------------"
echo "Overwrites source generated by spoon in the source folder"
echo "-------------------------------------------------------"
echo " "

# Overwrites source folder.
for module in ${MODULES_JOB// / }; do
	GENERATED_DIRECTORY=${module}"target/generated-sources/spoon/"
	if [ -d "$GENERATED_DIRECTORY" ]; then
		cp -Rf ${GENERATED_DIRECTORY}* ${module}src/main/java
	fi
done

# Compiles project with source spooned.
START_COMPILE_SPOON_SPOON=$(($(date +%s%N)/1000000))
mvn clean install -fn
if [ "$?" -ne 0 ]; then
    echo "Error: Maven compile with spoon(spoon) unsuccessful!"
    exit 1
fi
END_COMPILE_SPOON_SPOON=$(($(date +%s%N)/1000000))
DIFF_SPOON_SPOON=$(echo "$END_COMPILE_SPOON_SPOON - $START_COMPILE_SPOON_SPOON" | bc)

# Displays results of the maven compile.
echo " "
echo "--- Displays results of the maven compile with spoon of spoon ---"
echo "Time to compile with spoon of spoon: $DIFF_SPOON_SPOON"

echo " "
echo "-------------------------------------------------------"
echo "Retreives all results files in the project (in all sub modules if the project is a multi module project)"
echo "-------------------------------------------------------"
echo " "

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
	mkdir target/spoon-reports
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

exit 0