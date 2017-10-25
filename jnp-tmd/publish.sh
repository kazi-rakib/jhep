#!/bin/sh

echo "----> copying the distribution..."
rm -rf /tmp/tmd-soft-1.0
rm -rf /tmp/tmd-soft-1.0.tar*

mkdir /tmp/tmd-soft-1.0
mkdir /tmp/tmd-soft-1.0/bin
mkdir /tmp/tmd-soft-1.0/etc
mkdir /tmp/tmd-soft-1.0/lib
cp target/*with*.jar /tmp/tmd-soft-1.0/lib/.
cp etc/exec/sidis-generator /tmp/tmd-soft-1.0/bin/.
cp -r etc/data /tmp/tmd-soft-1.0/etc/.
cd /tmp
echo "----> creating tarball"
tar -cf tmd-soft-1.0.tar tmd-soft-1.0
echo "----> gzipping the tarball"
gzip tmd-soft-1.0.tar
cd -
echo "----> deploying the package to : http://userweb.jlab.org"
scp -r /tmp/tmd-soft-1.0.tar.gz jlabl1:/userweb/gavalian/public_html/software/tmd/.
echo "----> done...."
echo ""
