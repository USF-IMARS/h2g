@echo off
.\j2sdk1.4.1_07-Windows\bin\javac -Xmx1g -Djava.library.path=.\lib\linux -Djava.awt.headless=true -classpath .\lib\h2g.jar;.\lib\jhdf5.jar;.\lib\jhdf5obj.jar;.\lib\jhdf4obj.jar;.\lib\jhdf.jar;.\lib\jhdfobj.jar;.\lib\jai_imageio.jar gov.nasa.gsfc.drl.h2g.Create %1 %2 %3 %4 %5 %6 %7 %8 %9
