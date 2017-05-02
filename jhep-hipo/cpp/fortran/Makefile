#-----------------------------------------------
# Makefile script to compile fortran library
#-----------------------------------------------

F77 = gfortran

exe: read_file.o
	$(F77) -o read_file.exe read_file.o -lgcc -lstdc++ ../lib/libhipo.a -L/usr/local/lib -llz4
clean: 
	@rm -rf *~ *.o

%.o:%.F
	$(F77) -c $<