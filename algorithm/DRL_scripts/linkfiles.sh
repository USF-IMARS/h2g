#!/bin/bash
#
# linkfiles.sh makes appropriate symbolic links for the XML files
# PGE302 needs to run.  Some day we'll be able to tell LPEATE where
# to look for those files... *sigh*...
#
# Takes two arguments - directory of XML files, link destination directory
ln -s -f $1/*.{xml,xsd,cfg} $2
