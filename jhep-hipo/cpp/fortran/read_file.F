C-*************************************
C- Reading HIPO file from fortran
C-*************************************
      PROGRAM READ_FILE
      INTEGER NRECORDS,R,NEVENTS,E, NROWS
      INTEGER CHARGE(30)
C-
      CALL HIPO_OPEN_FILE(NRECORDS,'infile.hipo')
      WRITE(*,*) '[OPEN] NRECORDS = ', NRECORDS
      DO R=1,NRECORDS
         CALL HIPO_READ_RECORD(R,NEVENTS)
         WRITE(*,*)'[READ] N EVENTS # ',NEVENTS
         DO E=1,NEVENTS
            CALL HIPO_READ_EVENT(E)
            CALL HIPO_READ_NODE_INT(22,8,NROWS,CHARGE)
            IF(NROWS.GT.0) THEN
              WRITE(*,*) (CHARGE(j),J=1,NROWS)
            ENDIF
         ENDDO
      ENDDO
      WRITE(*,*) '[DONE] done reading file'
      END
