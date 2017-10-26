#!/bin/bash

# This script intends to be run on TravisCI
# it runs verify and site maven goals
# and to check documentation links

mvn -Djava.src.version=$JDK_VERSION verify site install -DskipTests && python check-links-in-doc.py