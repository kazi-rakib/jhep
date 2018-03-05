Getting started
***************

Getting JAW distribution
========================

JAW is a command line Java Analysis Workstation, which provides 
PAW like interface to analyzing data from text files and additional
functionality for physics analysis directly on reconstructions files
in HIPO format. To obtain the latest version of the software, use
the command below.

.. code-block:: bash

   prompt> wget http://userweb.jlab.org/~gavalian/software/jaw-0.8.tar.gz
   prompt> tar -zxvf jaw-0.8.tar.gz
   prompt> cd jaw-0.8
   prompt> ./jaw.sh

Once distribution is downloaded a sample data file from CLAS12 2.2 GeV run
can be obtained:

.. code-block:: bash

   prompt> wget http://userweb.jlab.org/~gavalian/software/clasrun_2475.hipo.2

With the file downloaded, user can run examples provided in the package.

.. code-block:: bash

   prompt>./jaw.sh
   jaw> exec examples/analysis.kumac


   

