Vector Operations
*****************

Defining vectors
================

A vector is series of numbers with given name that can be defined within the CLI.

.. code-block:: bash

   jpaw> vector/create a 1.0,2.0,3.0,4.0

This will create a vector with name "a". 

.. code-block:: bash

   jpaw> vector/create b 5.5,6.8,8.2,12.7

This will create a new vector named "b". There are several ways to plot vectors.
The vector can be plotted on it's own, where the X axis is just the number of the element 
and Y axis is the value of the vector, or two vectors can be plotted against each other,
setting first vector as Y axis and second vector as X axis

.. code-block:: bash

   jpaw> vector/plot a !
   jpaw> vector/plot a%b !

The vectors can also be read from a file. Where each column in the file is a vector.
The syntax for readin from file is:

.. code-block:: bash

   jpaw> vector/read a:b myvector.txt 0

The first argument is the names of vectors, second argument is the text file name, and
third argument is the offset of the column. Let's assume that the text file contains 5
columns, and user wants to read 3rd column into a single vector. The command is:

.. code-block:: bash

   jpaw> vector/read a myvector.txt 2

The column offset starts from 0, that's why the nuber 2 after the file name represents the third 
column.
To display all the vectors defined in the memory use the list command:

.. code-block:: bash

   jpaw> vector/list
