@echo off
.\j2sdk1.4.1_07-Windows\bin\javac -d .\classes -classpath .\classes:.\lib\jhdf5.jar:.\lib\jhdf5obj.jar:.\lib\jhdf4obj.jar:.\lib\jhdf.jar:.\lib\jhdfobj.jar:.\lib\jai_imageio.jar src\gov\nasa\gsfc\drl\h2g\*.java src\gov\nasa\gsfc\drl\h2g\test\*.java src\gov\nasa\gsfc\drl\h2g\util\*.java
.\j2sdk1.4.1_07-Windows\jar -cf .\lib\h2g.jar -C .\classes . -C . colormaps
