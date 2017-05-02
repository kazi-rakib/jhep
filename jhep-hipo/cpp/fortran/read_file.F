C-*************************************
C- Reading HIPO file from fortran
C-*************************************
      PROGRAM READ_FILE
      INTEGER NRECORDS,R
C-
      CALL HIPO_OPEN_FILE(NRECORDS,'infile.hipo')
      WRITE(*,*) '[OPEN] NRECORDS = ', NRECORDS
      DO R=1,NRECORDS
C         CALL HIPO_READ_RECORD(R,NEVENTS)
      ENDDO
      WRITE(*,*) ' [READ] done reading file'
      END
