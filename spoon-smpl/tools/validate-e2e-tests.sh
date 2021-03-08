#!/bin/bash

# This script validates end-to-end tests by checking that the input classes compile
# 
# Example usage: ./tools/validate-e2e-tests.sh src/test/resources/endtoend

# TODO: document the "intentionally-does-not-compile" tag
# TODO: document the asterisk "*OK" output

mkdir -p /tmp/validate-e2e-tests

for f in $(find "$1" | grep -P "\.txt$" | sort); do
    if [ -f "$f" ]; then
        code=$(cat "$f" | grep -Pzo '(?s)\[input\].+?(?=\n\[|\$)' | tr -d '\0' | grep -v '\[input\]')
        marked_invalid=$(cat "$f" | grep -Pio "intentionally-does-not-compile")

        if [ "$marked_invalid" == "" ]; then
            class=$(echo "$code" | grep -Po 'class \w+' | cut -d ' ' -f2)
            rm -f /tmp/validate-e2e-tests/*.java
            echo "$code" > /tmp/validate-e2e-tests/"$class".java
            if ! javac -d /tmp/validate-e2e-tests /tmp/validate-e2e-tests/"$class".java; then
                echo "$f" failed to validate, stop.
                exit
            fi
        else
            echo -n "*"
        fi
        echo OK: "$f"
    fi
done
