#!/bin/bash

# This script runs the spoon-smpl CLI application

java -cp $(for f in ./target/*.jar; do echo -n $f:; done) spoon.smpl.CommandlineApplication $@
