Ntuple Operations
*****************

Writing Ntuples from JAVA code
==============================

The following code shows how one can write out an NTUPLE from JAVA
code that can be used in the JAW (CLI).

.. code-block:: bash

    TreeFile tree = new TreeFile("test.hipo","T","a:b:c:d:f");
    float[]  rowf = new float[5];
        
    for(int i = 0; i < 4800000; i++){
        for(int k = 0; k < row.length; k++) 
	    rowf[k] = (float) (Math.random()*10.0);
        tree.addRow(rowf);
    }
    tree.close();

This example creates an Ntuple tree with 5 columns and fills them with random numbers.
Can be used in the groovy script to outputs of analysis results.

Importing ntuples in JAW
========================

To use ntuples one should open binary ntuple file, or import text file into an ntuple.
To import ntuples, use

.. code-block:: bash

   jaw> ntuple/read a:b:c:d myntuple.txt myntuple.hipo

This will convert the text file with 4 columns into a binary (compressed) n-tuple file
named myntuple.hipo

Plotting Ntuple data
====================

Once a binary ntuple file exists, one needs to open it in the interactive shell
to have access to it's data. It starts with:

.. code-block:: bash

   jaw> ntuple/open 10 myntuple.hipo

This will assign an ID=10 to the n-tuple. Several ntuples can be opened at one time,
since the n-tuples are not read into memory, it does not cost any memory to open all
at once. To produce plots from n-tuple use the following commands

.. code-block:: bash

   jaw> ntuple/plot 10.a !

will plot variable "a" as a histogram (with default binning). The second argument is cuts
to be used in the plot (the "!" sign indicates that no cuts are used). For example:

.. code-block:: bash

   jaw> ntuple/plot 10.a a>1.0&&a<3.2

Will produce a plot for values of "a" between 1.0 and 3.2. The syntax for plotting 2D histograms
is:

.. code-block:: bash

   jaw> ntuple/plot 10.a%b a>1.0&&a<3.2&&b>0.0&&b<10.0

will produce 2D plot of variable "a" (Y axis) vs variable "b" (X axis). All plots are produced on
the active graphics canvas, which is instantiated with only one pad, to increase number of pads 
on the canvas use commands.

.. code-block:: bash

   jaw> canvas/zone 2 2

This will create a layout with 2 columns and 2 rows on the active canvas. To clear the canvas use:

.. code-block:: bash

   jaw> canvas/clear

Running Scripts
===============

The interactive commands can be combined in a script and ran at once. User can create a text file
with all the commands typed in, then use the interactive shell to run them by:

.. code-block:: bash

   jaw> exec myscript.kumac




