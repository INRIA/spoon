#!/bin/bash
#
# Cleans files deployed in local.

for file in *.md; do
    rm _jekyll/${file}
done