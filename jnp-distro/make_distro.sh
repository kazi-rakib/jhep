#!/bin/sh
#**********************************************************
# SCRIPT for creating JAW distribution 
#**********************************************************
VERSION=0.8
#**********************************************************
rm -rf jaw-$VERSION.tar.gz
rm -rf jaw-$VERSION
#**********************************************************
mkdir -p jaw-$VERSION/lib
mkdir -p jaw-$VERSION/bin
#**********************************************************
cp ../jnp-math/target/jnp-math-1.0-SNAPSHOT-jar-with-dependencies.jar jaw-$VERSION/lib/jaw-$VERSION.jar
cp bin/* jaw-$VERSION/bin/.
cp -r examples jaw-$VERSION/.
mv jaw-$VERSION/bin/jaw.sh jaw-$VERSION/
tar -cf jaw-$VERSION.tar jaw-$VERSION
gzip jaw-$VERSION.tar
#**********************************************************
# IF PROJECT ENVIRONMENT is SET COPY DISTRIBUTION
#**********************************************************
if [[ ! -z $PROJECT ]]
then
cp -r jaw-$VERSION $PROJECT/.
echo 'Copied the distribution'
fi
