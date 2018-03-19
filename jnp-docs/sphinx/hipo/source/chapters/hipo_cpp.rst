HIPO Utilities
**************

Description of HIPO utilities for C++ API.

Installation
============

Current version (3.1) of HIPO C++ library is installed on CUE machines.
The library is compiled using LZ4 and Z library on the system. The location
of the package is:

.. code-block:: bash

  /group/clas12/packages/hipo-io

The dependency package LZ4 is also installed in the same directory for clas12
packages, when writing a Makefile, be aware that you will need to specify the
location of LZ4 library installed at:

.. code-block:: bash

  /group/clas12/packages/lz4

To use the HIPO library on different machine, just copy the directory and recompile.
Make sure that you have LZ4 and Z (gzip) libraries installed on the system, otherwise
compressed HIPO files will be unreadable.

The HIPO library can be compiled with the compression disabled if desired, in that case
one could write out un-compressed HIPO file from existing one using JAVA utilities
and use it with the library.

Getting started
===============

There are example programs included in the package in directory "examples". There
is also a utility program called "hipo" located in "bin" directory which will help
user to browse a HIPO file and create code for reading the file.
In order to create a reader program one needs to know the branches and bank names
from the file dictionary to be able to auto-generate code. To get the list of banks
in particular file use:

.. code-block:: bash

  /group/clas12/packages/hipo-io/bin/hipo --list myfile.hipo

This will list all available banks defined in the dictionary of the file. A typical output
for CLAS12 decoded file will look like:

.. code-block:: bash

      Schema BMT::adc                  :     20111
      Schema BST::adc                  :     20211
      Schema CND::adc                  :     20311
      Schema CTOF::adc                 :     20411
      Schema ECAL::adc                 :     20711
      Schema FMT::adc                  :     20811
      Schema FTCAL::adc                :     20911
      Schema FTHODO::adc               :     21011
      Schema FTOF::adc                 :     21111
      Schema FTTRK::adc                :     21311
      Schema HTCC::adc                 :     21511
      Schema LTCC::adc                 :     21611
      Schema RAW::adc                  :     20011
      Schema RF::adc                   :     21711
      Schema RICH::adc                 :     21811
      Schema RTPC::adc                 :     21911

Where on the left side is the name of the banks and on the right side the group IDs
(the group IDs can also be used to access data if there is no dictionary present in the file).
The full list of banks with the leafs with their types and IDs can also be printed out
on the screen from command line (for debugging purposes):

.. code-block:: bash

  /group/clas12/packages/hipo-io/bin/hipo --dump myfile.hipo


This will print out entire dictionary.

Code Generation
===============

To go over the file for analysis one could generate a code that will load branches
automatically inside of the loop for user. To generate C++ code, use command:

.. code-block:: bash

   /group/clas12/packages/hipo-io/bin/hipo --code myfile.hipo LTCC::adc

This will create a file with reader and leafs (branches) defined as part of the reader,
the reader loop will load the branches for each event until the end of the file. User
can modify the code inside of the loop to suit his/her purpose. The generated code looks
like this:

.. code-block:: c++

      #include <cstdlib>
      #include <iostream>

      #include "reader.h"
      #include "node.h"

      int main(int argc, char** argv) {
         std::cout << " reading file example program (HIPO) " << std::endl;
         char inputFile[256];

         if(argc>1) {
            sprintf(inputFile,"%s",argv[1]);
         } else {
            std::cout << " *** please provide a file name..." << std::endl;
           exit(0);
         }

         hipo::reader  reader;
         reader.open(inputFile);


         hipo::node<int32_t>         *LTCC__adc_ADC = reader.getBranch<int32_t>("LTCC::adc","ADC");
         hipo::node<int16_t>   *LTCC__adc_component = reader.getBranch<int16_t>("LTCC::adc","component");
         hipo::node<int8_t>        *LTCC__adc_layer = reader.getBranch<int8_t>("LTCC::adc","layer");
         hipo::node<int8_t>        *LTCC__adc_order = reader.getBranch<int8_t>("LTCC::adc","order");
         hipo::node<int16_t>         *LTCC__adc_ped = reader.getBranch<int16_t>("LTCC::adc","ped");
         hipo::node<int8_t>       *LTCC__adc_sector = reader.getBranch<int8_t>("LTCC::adc","sector");
         hipo::node<float>          *LTCC__adc_time = reader.getBranch<float>("LTCC::adc","time");

         //----------------------------------------------------
         //--  Main LOOP running through events and printing
         //--  values of the first decalred branch
         //----------------------------------------------------
         int entry = 0;
         while(reader.next()==true){
            entry++;
            std::cout << "event # " << entry << std::endl;
            int n_LTCC__adc_ADC = LTCC__adc_ADC->getLength();
            for(int b = 0; b < n_LTCC__adc_ADC; b++){
               std::cout << LTCC__adc_ADC->getValue(b) << " " ;
             }
            std::cout << std::endl;
         }
         //----------------------------------------------------
      }

The generated file is saved as runFileLoop.cc, user is responsible for renaming it to
avoid overwriting it with next generation of code. The code generator will also generate
an "SConstruct" file for compilation. The include paths and library paths are set to
compile the code on JLAB CUE machines. If you move the code to another machine, you must
modify the paths to match your installation of HIPO library. Here is a sample session to
generate a file and run through events:

.. code-block:: bash

  ifarm1401> /group/clas12/packages/hipo-io/bin/hipo --code my_decoded_file.hipo LTCC::adc
  ifarm1401> scons
  ifarm1401> ./runFileLoop my_decoded_file.hipo

Here is a sample printout what user should see if the LTCC::adc bank is present in the file.

.. code-block:: bash

      event # 13263
      0 0 215
      event # 13264
      475
      event # 13265
      0 11636 626 147 166 321 115
      event # 13266
      .....
      .....
      event # 13293
      5967
      event # 13294
      0 311 892 96 107
      event # 13295
      0 166 239
      event # 13296

This code will only load branches associated with LTCC::adc bank. If all banks are needed to analysis
one could generate code for all the banks by:

.. code-block:: bash

  ifarm1401> /group/clas12/packages/hipo-io/bin/hipo --code my_decoded_file.hipo all

All the banks and all branches will be included in the code and will be accessible inside
the main loop.
