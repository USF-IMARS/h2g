#!/bin/bash
case $0 in
    /*) H2G_HOME="`dirname $0`" ;;
     *) PWD=`pwd`; H2G_HOME="`dirname $PWD/$0`" ;;
esac
rm -rf classes/*
$H2G_HOME/j2sdk1.4.1_07/bin/javac \
-d ./classes \
-classpath \
./classes:\
./lib/jhdf5.jar:\
./lib/jhdf5obj.jar:\
./lib/jhdf4obj.jar:\
./lib/jhdf.jar:\
./lib/jhdfobj.jar:\
./lib/jai_imageio.jar \
src/gov/nasa/gsfc/drl/h2g/*.java \
src/gov/nasa/gsfc/drl/h2g/test/*.java \
src/gov/nasa/gsfc/drl/h2g/util/*.java \
src/gov/nasa/gsfc/drl/h2g/vector/*.java
$H2G_HOME/j2sdk1.4.1_07/bin/jar -cf ./lib/h2g.jar -C ./classes . -C . colormaps
