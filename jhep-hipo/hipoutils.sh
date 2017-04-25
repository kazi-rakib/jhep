#!/bin/sh
#*********************************************************
#---------------------------------------------------------
# JHEP math CLI interface.
#---------------------------------------------------------
java -cp "target/jhep-hipo-1.0-SNAPSHOT-jar-with-dependencies.jar" org.jlab.jhep.hipo.utils.HipoUtilities $*
