#!/usr/bin/env bash

# If anything fails we are done for
set -e

build() {
  mvn -f spoon-pom clean package -DskipTests -Dmaven.javadoc.skip > /dev/null
}

compare_files() {
  sudo docker run --rm -t -w "$(pwd)" -v "$(pwd):$(pwd):ro" \
        registry.salsa.debian.org/reproducible-builds/diffoscope "$1" "$2"
}


# Build for the first time
build

# Save artifacts
mkdir -p saved_artifacts
cp target/spoon-core-*.jar saved_artifacts
cp spoon-javadoc/target/spoon-javadoc*.jar saved_artifacts

# Build again, will overwrite target jars
build

# Do not fail the script before both jars were compared and the results printed
set +e

# Comparison will drill down as deep as possible and print results
compare_files target/spoon-core-*[^dependencies].jar saved_artifacts/spoon-core-*[^dependencies].jar
CORE_EXIT="$?"

compare_files target/spoon-core-*dependencies.jar saved_artifacts/spoon-core-*dependencies.jar
DEPS_EXIT="$?"

compare_files spoon-javadoc/target/spoon-javadoc*.jar saved_artifacts/spoon-javadoc*.jar
JAVADOC_EXIT="$?"

if [[ "$CORE_EXIT" == 0 && "$DEPS_EXIT" == 0 && "$JAVADOC_EXIT" == 0 ]]; then
  echo -e "\033[1;32mThe jars were reproducible!\033[0m"
  exit 0
fi

# Print a pretty error message

echo -e "\n\033[1;31mThe jars were not reproducible\033[0m"

if [[ "$DEPS_EXIT" != 0 ]]; then
  echo -e "  \033[31mspoon-core-VERSION-with-dependencies.jar was not reproducible!\033[0m"
fi
if [[ "$CORE_EXIT" != 0 ]]; then
  echo -e "  \033[31mspoon-core-VERSION.jar was not reproducible!\033[0m"
fi
if [[ "$JAVADOC_EXIT" != 0 ]]; then
  echo -e "  \033[31mspoon-javadoc-VERSION.jar was not reproducible!\033[0m"
fi


exit 1
