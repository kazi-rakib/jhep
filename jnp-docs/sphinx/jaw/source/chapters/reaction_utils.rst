Physics Reaction Module
***********************

Physics reaction module includes tools for quick
physics analysis from CLAS12 data.

Filtering files
===============

To start analysis one needs a reconstructed file from CLAS12
cooked files. In order to run analysis at good speeds the file
must be filtered to contain only necessary banks for physics
analysis. Use the following command line:

.. code-block:: bash
   
   > $COATJAVA/bin/hipo-utils -filter -e 331 -l 330:331 -o myFiltered.hipo clasrun_002875.hipo

This will write only reconstruction header bank, containing event number, run number and other
relevant information, and reconstructed event bank with particles.

Defining a reaction
===================

To start physics analysis run the script named "jaw" from COATJAVA bin directory or
download the stand alone version of the analysis studio and run the script from it.
First step in analyzing the data is to define a reaction of interest, the rection
can be defined in command line as

.. code-block:: bash

   jaw> reaction/create 10 11:2212:X+:X-:Xn 2.2

The line defines a reaction with id=10 requiring in the event to have at least
one electron and at least one proton (all other events will be skipped), the
"X+:X-:Xn" indicates that event can have any number of positive, negative and neutral
particle in addition to specified electron and proton. The 3rd argument is the beam energy.

In the filter string multiple instance of particles can be requested, such as:

.. code-block:: bash

      jaw> reaction/create 10 11:22:22:X+:X-:Xn 2.2

This filter will chose event with at least one electron, at least two photons, and
any number of positive, negative and neutral particles that follow. When constructing
expressions from this kind of event, photon ids have to be followed by their order.
[22,0] - is the first photon in the event and [22,1] is the second photon.

After defining a reaction a file has to be attached to the reaction.

.. code-block:: bash

   jaw> reaction/file 10 myFiltered.hipo

Now we have reaction pattern and file defined, we can start looking a particles.

Defining particles
==================

Interface will only go through events which match the pattern indicated in create command.
Now user needs to define variables from the particles. The variables can be defined as 
properties of any given particle or properties of composite particle constructed from 
given expression. Example:

.. code-block:: bash

   jaw> reaction/particle 10 w2    [b]+[t]-[11] mass2
   jaw> reaction/particle 10 ephi  [11] phi

The first line defines a variable w2, which is the mass squared of particle
constructed from the beam, target and the electron from the event. The second line
defines a variable ephi which is the phi angle of reconstructed electron.
When the reaction filter has multiple instance of same particle, they can be referred
to with their order in the expression. For example to constrcut pi0 mass from two photons
use:

.. code-block:: bash

      jaw> reaction/particle 10 pi0m [22,0]+[22,1] mass

This expression means add the 4-vectors of 1st occuring photon to second accuring
photon and assign property mass to variable pi0m.

Plotting variables
==================

To plot any of the defined variables use the command:

.. code-block:: bash

   jaw> reaction/plot 10.w2 w2>0.5 1000 200

This will print w2 spectrum for matched events, where w2 is larger than
0.5, it will process only 1000 events from the file and created histogram
will have 200 bins. This is general syntax of the command, but most of the 
arguments can be ommitted. For example:

.. code-block:: bash
   
   jaw>	reaction/plot 10.w2 ! ! 200

Command will plot w2 without any cuts, for entire file and 200 bins in the histogram.
And

.. code-block:: bash

   jaw> reaction/plot 10.w2

Will plot w2 without any cuts, for entire file, with default number of bins=100.
Two dimentional plots are also supported, the syntax is

.. code-block:: bash 

   jaw> reaction/plot 10.w2%ephi w2>0.5

This will plot w2 vs electron phi for all events where w2>0.5.

Available properties
====================

Here is the list of properties that are available for particles.

+------------------------+---------------------------------------------+
| Property name          |          Description                        |
+========================+=============================================+
| mass                   | mass of the particle                        |
+------------------------+---------------------------------------------+
| mass2                  |  mass squared of the particle               |
+------------------------+---------------------------------------------+
| p                      |  momentum of the particle                   |
+------------------------+---------------------------------------------+
| px                     | x-component of the momentum of the particle |
+------------------------+---------------------------------------------+
| py                     | y-component of the momentum of the particle |
+------------------------+---------------------------------------------+
| pz                     | z-component of the momentum of the particle |
+------------------------+---------------------------------------------+
| vx                     | x-component of the vertex of the particle   |
+------------------------+---------------------------------------------+
| vy                     | y-component of the vertex of the particle   |
+------------------------+---------------------------------------------+
| vz                     | z-component of the vertex of the particle   |
+------------------------+---------------------------------------------+
| theta                  | theta angle of the particle                 |
+------------------------+---------------------------------------------+
| phi                    | phi angle of the particle                   |
+------------------------+---------------------------------------------+

