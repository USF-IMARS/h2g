#!/bin/bash
LIB=./lib64
#LIB=./lib32
case $0 in
    /*) H2G_HOME="`dirname $0`" ;;
     *) PWD=`pwd`; H2G_HOME="`dirname $PWD/$0`" ;;
esac
rm -rf classes/*
javac -Xlint \
-d ./classes \
-classpath \
./classes:\
$LIB/jproj.jar:\
$LIB/jhdf5.jar:\
$LIB/jhdf5obj.jar:\
$LIB/jhdf4obj.jar:\
$LIB/jhdf.jar:\
$LIB/jhdfobj.jar:\
$LIB/jai_imageio.jar \
src/gov/nasa/gsfc/drl/h2g/*.java \
src/gov/nasa/gsfc/drl/h2g/util/*.java \
src/gov/nasa/gsfc/drl/h2g/vector/*.java 
jar -cf $LIB/h2g.jar -C ./classes .    
sed s/32/64/ ./bin/h2gtemplate.sh > ./bin/h2g.sh
