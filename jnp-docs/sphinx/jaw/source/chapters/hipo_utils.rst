HIPO Utilities
**************

This section gives an introduction to file utilities included 
in coatjava to read reconstruction data an do analysis.

Filtering HIPO Files
====================

The reconstruction files contain many banks, mostly debugging that 
are not needed for generic user. In order to have smaller file to 
transfer to personal computer, user can filter the banks keeping
only relevant structures for analysis. The utility is included inside 
of the coatjava package as well as in the JAW package.

To filter banks use the command line:

.. code-block:: bash

    ./hipo-utils -filter -e 331 -l 331:330 -o myFilteredFile.hipo clasrun_002475.1.hipo clasrun_002475.2.hipo

This command will merge two files given as input file and create one file with two banks in each
event. The banks to keep are given by their ID, to avoid lengthly command line. The "-e" option
indicates, that only events, where the bank 331 exists, will be written to output. And the "-l"
option indicates the list of banks to write to output file (if they exist). In order to find out
the bank ID numbers to be used in the command line, one could use:

.. code-block:: bash

       ./hipo-utils -info clasrun_002475.1.hipo

Which will print a table with bank names and associated IDs, something like this one:

.. code-block:: bash

   RECHB::ForwardTagger :      314 :       15
    RECHB::Scintillator :      315 :       17
           RECHB::Track :      316 :       16
      RECHB::TrackCross :      317 :       12
             REC::Event :      330 :       13
          REC::Particle :      331 :       11
       REC::Calorimeter :      332 :       25
         REC::Cherenkov :      333 :       16
     REC::ForwardTagger :      334 :       15
      REC::Scintillator :      335 :       17

As can be seen from the example above the bank 330 and 331 correspond to "REC::Event" and
"REC::Particle" banks respectively. The "REC::Event" contains run dependent information 
about the runs, such as run number, event number torus current and so on. The "REC::Particle"
bank contains reconstructed particle information.
This is a printout from hipodump program showing the structure and content of the banks:

.. code-block:: bash

    Choose (n=next,p=previous, q=quit), Type Bank Name or id : 0
    ------------------------+---------------------------+
    >>>> GROUP (group=   330) (name=REC::Event):
    ------------------------+---------------------------+
            NRUN (     INT) :       2475
          NEVENT (     INT) :   17006972
         EVNTime (   FLOAT) :     0.0000
            TYPE (    BYTE) :          0
           EvCAT (   SHORT) :          0
            NPGP (   SHORT) :          0
             TRG (    LONG) :         33
             BCG (   FLOAT) :     0.0000
              LT (  DOUBLE) :     0.0000
          STTime (   FLOAT) :   550.7249
          RFTime (   FLOAT) :    53.7328
           Helic (    BYTE) :          0
           PTIME (   FLOAT) :     0.0000
    ------------------------+---------------------------+

    Choose (n=next,p=previous, q=quit), Type Bank Name or id : 1
    ------------------------+---------------------------+
    >>>> GROUP (group=   331) (name=REC::Particle):
    ------------------------+---------------------------+
             pid (     INT) :         11        22         0         0
              px (   FLOAT) :    -0.1183   -0.0282    0.1499   -0.0633
              py (   FLOAT) :    -0.1499   -0.0620    0.7535    0.9659
              pz (   FLOAT) :     1.3320    0.4869    0.6401   -0.2511
              vx (   FLOAT) :     0.2630    0.0000    0.0000    0.0000
              vy (   FLOAT) :    -0.1439    0.0000    0.0000    0.0000
              vz (   FLOAT) :     0.7597    0.0000    0.0000    0.0000
          charge (    BYTE) :         -1         0         0         0
            beta (   FLOAT) :     1.0000    0.0000    0.0000    0.0000
         chi2pid (   FLOAT) :     0.0000    0.0000    0.0000    0.0000
          status (   SHORT) :          1         1         1         1
    ------------------------+---------------------------+


The printout shows all columns (and their type) for each of the banks.


Reading HIPO Files
==================

The filtering is not a neccessary step for analyzing the data, it just 
makes the data smaller for faster processing. The following script can be 
used on any file (cooked or un-cooked). Here is an example script (in GROOVY)
how to read a file (event by event) and plot quantities from given bank:

.. code-block:: bash

    //*************************************************
    // SCRIPT WILL READ HIPO FILES
    //*************************************************
    import org.jlab.io.hipo.*;
    import org.jlab.groot.data.*;
    import org.jlab.groot.ui.*;

    //run with
    //$COATJAVA/bin/run-groovy reader.groovy

    filename = args[0];

    H1F h = new H1F("ADC",200,0.0,10000.0);

    HipoDataSource reader = new HipoDataSource();
    reader.open(filename);
    int nevents = reader.getSize();

    int counter = 0;
    for(int i = 0; i < nevents; i++){
      HipoDataEvent  event = (HipoDataEvent) reader.gotoEvent(i);
      event.show();
      if(event.hasBank("ECAL::adc")==true){
        System.out.println(" FOUND ADC BANK FOR ECAL");
        HipoDataBank    bank = (HipoDataBank) event.getBank("ECAL::adc");
        bank.show();
        System.out.print(" VALUES = ");
       for(int k = 0; k < bank.rows(); k++){
           int adc = bank.getInt("ADC",k);
           h.fill(adc);
           System.out.print(" " + adc);
        }
        System.out.println("");
      }
      counter++;
    }

    TCanvas c1 = new TCanvas("c1",500,500);
    c1.draw(h);
    System.out.println("  procecessed " + counter + "  events");

While running this script one can also write out an n-tuple file
that can be used with JAW interactive shell for past analysis.
Example of writing an n-tuple can be found in following sections.
