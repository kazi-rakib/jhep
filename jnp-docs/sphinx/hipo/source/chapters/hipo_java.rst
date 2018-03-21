HIPO Utilities JAVA
********************

HIPO utilities are packed into a package called JAW, which provides the jar libraries
that can be used in the Eclipse or Netbeans project, as well as scripts to manipulate
HIPO files, i.e. merge, split skim and some statistical tools. The C++ library does not
have all the tools provided with JAVA version, so for simple tasks use the JAVA package
environment.

Installation
============

Current version of JAW package can be downloaded from:

.. code-block:: bash

    prompt> wget http://userweb.jlab.org/~gavalian/software/jaw-0.8.tar.gz
    prompt> tar -zxvf jaw-0.8.tar.gz
    prompt> cd jaw-0.8

This will bring entire library including the HIPO jar and JNP Physics jar
and JNP plotting package all packed into one big jar.

Getting Started
===============

Inside the distribution there is and executable "bin/hipo-utils.sh" that provides
various tool for interacting with HIPO files.
