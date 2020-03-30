#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
java -cp $(for f in $DIR/target/*.jar; do echo -n $f:; done) spoon.smpl.CommandlineApplication $@
