#!/bin/bash
#
# linkfiles.sh deletes all symbolic links in a directory
# (just for neatness sake)
#
for x in "$1"/*
do
    echo $x
    if [ -L "$x" ]
    then
	rm $x
    fi
done
