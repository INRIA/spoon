#!/bin/bash

workdir=$(realpath .)
resfile=$(realpath "./src/test/resources/C4JShouldVibrate.zip")
smplfile=$(realpath "./processor-tests/should_vibrate.smpl")
codedir=$(mktemp -d --suffix spoon-smpl)
outdir=$(mktemp -d --suffix spoon-smpl)

echo "Output dir: $outdir"

cd "$codedir"
unzip "$resfile"

cd "$workdir"

java -cp $(for f in target/*.jar; do echo -n $f:; done) spoon.smpl.SmPLProcessor \
     --with-diff-command "bash -c \"diff -U5 -u {a} {b}\""                       \
     --with-smpl-file "$smplfile"                                                \
     -i "$codedir"                                                               \
     -o "$outdir"                                                                \
     -p spoon.smpl.SmPLProcessor $@
