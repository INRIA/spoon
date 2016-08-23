#!/bin/bash

# This is a simple example of how to configue a Spoon development workflow with a bash scrpt.

# This script organizes the process of compiling a custom processor and running it on
# a specific java project.

# CONFIG:
BIN="bin"
SPOON_JAR="lib/spoon.jar"

# INPUT: 
### 1) the fully qualified name of the processor to use (ex: com.example.spoonprocessors.MyProcessor)
### 2) the location of the source code to be spooned (ex: tester_project/src)
### 3) the destination for the spooned output source code (ex: spooned)
### 4) [OPTIONAL AND ONLY USED IN OSX CONFIGURATION] the path to the processor source code (ex: src/com/example/spoonprocessors/MyProcessor.java)
processor="$1"
input="$2"
output="$3"
processor_src="src/${processor//./\/}.java"
# The following is a configuration targeted toward osx or other operating systems that don't handle the escape character backslash above well
processor_src="$4"

# STEP 1: COMPILE THE PROCESSOR

echo "1. Building processor $processor..."
echo

# Attempts to compile the processor, terminates script if an error is thrown
if ! javac -classpath "${SPOON_JAR}" -d "${BIN}" "${processor_src}"; then
echo;
echo "Error compiling, Spoon aborted.";
exit;
fi

# STEP 2: RUN SPOON USING THE PROCESSOR

echo "2. Running spoon using $processor"
echo


java \
-classpath "${BIN}:${SPOON_JAR}" \
spoon.Launcher \
-i "${input}" \
-d "${output}" \
-p "${processor}"

# NOTE: You can easily add to this script to do things like running your processor on multiple projects at once

# otherinput = "path/to/src"
# java \
# -classpath "${BIN}:${SPOON_JAR}" \
# spoon.Launcher \
# -i "${otherinput}" \
# -d "${output}" \
# -p "${processor}"

# [Optional] Insures that the output files are properly owned by the user even if this script is run with sudo somehow.
# chown -R $USER: ${output}
