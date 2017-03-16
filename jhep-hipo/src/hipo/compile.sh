rm -rf *.o
gfortran -c ntuple_maker.F
gfortran -o ntuple_maker ntuple_maker.o lib/libhipo.a lib/liblz4.a
