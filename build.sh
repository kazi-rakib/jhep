#!/bin/bash

while [ "$1" != "" ]; do
    case $1 in
	-c)
	    CLEAN_COMMAND="1"
	    shift 1
	    continue
	  ;;
	-b)
	    BUILD_COMMAND="1"
	    shift 1
	    continue
	  ;;
	-d) 
	    DOCS_COMMAND="1"
	    shift 1
	    continue
	    ;;
        *) 
            break
    esac
done

if [[ ! -z $CLEAN_COMMAND ]]
then
  echo 'Cleaning the distribution'
  cd jnp-utils  ; mvn clean; cd -
  cd jnp-cli   ; mvn clean; cd -
  cd jnp-hipo  ; mvn clean; cd -
  cd jnp-math  ; mvn clean; cd -
  cd jnp-physics  ; mvn clean; cd -
fi

if [[ ! -z $BUILD_COMMAND ]]
then
    echo 'building the distribution'
    rm -rf ~/.m2/repository/org/jlab/jnp
    cd jnp-utils ; mvn install; mvn deploy; cd -
    cd jnp-cli   ; mvn install; mvn deploy; cd -
    cd jnp-hipo  ; mvn install; mvn deploy; cd -
    cd jnp-math  ; mvn install; mvn deploy; cd -
    cd jnp-physics  ; mvn install; mvn deploy; cd -
fi

if [[ ! -z $DOCS_COMMAND ]]
then
  echo 'documenting the distribution'
  javadoc -d javadoc/jnp-utils -sourcepath jnp-utils/src/main/java/ -subpackages org
  scp -r javadoc clas12@ifarm1402:/group/clas/www/clasweb/html/jnp/docs/.
fi


echo ''; echo 'all done.....'; echo ''
