#!/bin/bash

# validate e2e tests by checking that the input classes compile

mkdir -p /tmp/validate_tests-e2e

for f in $(find "$1"); do
    if [ -f "$f" ]; then
        code=$(cat "$f" | grep -Pzo '(?s)\[input\].+?(?=\n\[|\$)' | tr -d '\0' | grep -v '\[input\]')
        class=$(echo "$code" | grep -Po 'class \w+' | cut -d ' ' -f2)
        rm -f /tmp/validate_tests-e2e/*.java
        echo "$code" > /tmp/validate_tests-e2e/"$class".java
        if ! javac -d /tmp/validate_tests-e2e /tmp/validate_tests-e2e/"$class".java; then
            echo "$f" failed to validate, stop.
            exit
        fi
        echo OK: "$f"
    fi
done
