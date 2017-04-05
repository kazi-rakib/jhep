#!/bin/sh
#=================================================================
# BUILDING SCRIPT for JHEP PROJECT (first maven build)
# then the documentatoin is build from the sources and commited
# to the documents page
#=================================================================
# Maven Build

while getopts dm name
do
    case $name in
       d) dopt=1;; # -d option is for generating Javadoc 
       m) mopt=1;; # -s option is for compiling the code
       *) echo "Invalid arg : "; echo "\t use : build.sh -m -d" ; echo "" ;exit;
    esac
done

if [[ ! -z $mopt ]]
then
    rm -rf ~/.m2/repository/org/jlab/jhep
    cd jhep-utils ; mvn install; mvn deploy; cd -
    cd jhep-cli   ; mvn install; mvn deploy; cd -
    cd jhep-hipo  ; mvn install; mvn deploy; cd -
    cd jhep-math  ; mvn install; mvn deploy; cd -
fi
#=================================================================
# Documentation build
if [[ ! -z $dopt ]]
then
    echo "---> Building documentation ...."
    javadoc -d javadoc/jhep-utils -sourcepath jhep-utils/src/main/java/ -subpackages org
    scp -r javadoc clas12@ifarm1402:/group/clas/www/clasweb/html/jhep/docs/.
fi

#=================================================================
# Finishing touches
echo ""
echo "--> Done building....."
echo ""
echo " \t Usage : build.sh -d -m"
echo ""


