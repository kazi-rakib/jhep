#!/bin/sh

VERSION=0.8

mkdir -p jaw-$VERSION/lib
cp target/jnp-math-1.0-SNAPSHOT-jar-with-dependencies.jar jaw-$VERSION/lib/jaw-$VERSION.jar
cp jaw.sh jaw-$VERSION/.
cp -r examples jaw-$VERSION/.
tar -cf jaw-$VERSION.tar jaw-$VERSION
gzip jaw-$VERSION.tar
