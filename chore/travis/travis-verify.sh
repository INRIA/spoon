#!/bin/bash

# This script intends to be run on TravisCI
# it runs verify and site maven goals
# and to check documentation links

jdk_switcher use oraclejdk9
pip install --user CommonMark requests pygithub
mvn -Djava.src.version=1.9 verify site install -DskipTests && python ./chore/check-links-in-doc.py