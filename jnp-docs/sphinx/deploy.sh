#!/bin/sh
#---------------------------------------------------
# DEPLOY SCRIPT FOR DOCUMENTATIONS
#---------------------------------------------------
cd hipo; make html; cd -
cd jaw ; make html; cd -
scp -r hipo/build/html jlabl1:/userweb/gavalian/public_html/docs/sphinx/hipo/.
scp -r jaw/build/html  jlabl1:/userweb/gavalian/public_html/docs/sphinx/jaw/.
