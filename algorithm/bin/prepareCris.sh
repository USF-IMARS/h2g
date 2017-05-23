#!/bin/bash
case $0 in
    /*) H2G_HOME="`dirname $0`/.." ;;
     *) PWD=`pwd`; H2G_HOME="`dirname $PWD/$0`/.." ;;
esac
LIB=$H2G_HOME/lib64
java \
-Xmx2g \
-Djava.library.path=$LIB/linux \
-Djava.awt.headless=true \
-classpath \
$LIB/h2g.jar:\
$LIB/jhdf5.jar:\
$LIB/jhdf5obj.jar:\
$LIB/jhdf4obj.jar:\
$LIB/jhdf.jar:\
$LIB/jhdfobj.jar \
gov.nasa.gsfc.drl.h2g.util.PrepareCris $@
