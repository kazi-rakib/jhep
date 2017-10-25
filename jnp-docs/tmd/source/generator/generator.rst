TMD Software Quick-Start
************************

Download the Package
====================

The package can be downloaded from: http://userweb.jlab.org/~gavalian/software/tmd/tmd-soft-1.0.tar.gz

Generating Events
=================

To generate events use the command:

.. code-block:: java

    >./bin/sidis-generator -n 10000 -o sidis_output

This will generate 10000 events and save them into LUND files with given name
on the command line. The file is split every 20,000 events.

LUND format
===========

The output LUND format consists of event header followed by particle information
for each event. The event header has 10 columns. Here is the description of
the header columns:

\1. number of particles in the event

\2. cross section of the event

\3. initial beam energy

\4. Q2 for the event

\5. x Bjorken for the event

\6. z for the event

\7. pt for the hadron

\8. phi angle between the electron scattering plane and hadron plane

Here is sample event from LUND file:

.. code-block:: bash

    2  1.08494  11.00000  1.37373  0.16414  0.83483  0.14713  1.39382  0.00000  0.00000
 1 -1.    1     11  0  0    0.6931   -0.5766    6.4777    6.5402    0.0005      0.0000    0.0000    0.0000
 2  1.    1    211  0  0   -0.4467    0.5599    3.6510    3.7232    0.1396      0.0000    0.0000    0.0000
