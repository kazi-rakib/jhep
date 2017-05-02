#-----------------------------------------------
# Makefile script to compile fortran library
#-----------------------------------------------

LZ4_LIBPATH=/usr/local/lib
LZ4_LIBPATH_JLAB=/group/clas12/packages/lz4/lib
LZ4_LIB=lz4
F77=gfortran

exe: read_file.o
	$(F77) -o read_file.exe read_file.o -lgcc -lstdc++ ../lib/libhipo.a -L$(LZ4_LIBPATH) -L$(LZ4_LIBPATH_JLAB) -llz4
clean: 
	@rm -rf *~ *.o *.exe

%.o:%.F
	$(F77) -c $<
