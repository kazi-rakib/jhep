#!/bin/sh
#*********************************************************
#---------------------------------------------------------
# JHEP math CLI interface.
#---------------------------------------------------------
SCRIPT_DIR=`dirname $0`
#---------------------------------------------------------
# The MALLOC_ARENA_MAX is GLIB flag that controls
# how much VIRTUAL memory will be claimed by JVM
#---------------------------------------------------------
MALLOC_ARENA_MAX=1; export MALLOC_ARENA_MAX
#---------------------------------------------------------
# SET UP JAVA_OPTIONS With the max memory and starting
# memory
#---------------------------------------------------------
#JAVA_OPTIONS="-XX:+UseG1GC -Xmx1024m -Xms512m"
JAVA_OPTIONS="-XX:+UseSerialGC -Xmx1024m -Xms512m"
java $JAVA_OPTIONS -cp "$SCRIPT_DIR/target/jnp-hipo-1.0-SNAPSHOT-jar-with-dependencies.jar" org.jlab.jnp.hipo.utils.HipoUtilities $* 
