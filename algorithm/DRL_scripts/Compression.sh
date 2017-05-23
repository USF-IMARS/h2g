#!/bin/bash
# Takes as input the name of a an HDF5 file
# Creates a file named CompressionCheck.txt in the current directory
# The file contains one line of the form
# 	COMPRESSION="XXX"
# where XXX is Compressed if the header of input file contains the string "COMPRESSION DEFLATE",otherwise Uncompressed

# Clean any existing  files
rm -f h5dump.txt

null=`${H5UTILHOME}/h5dump -p -H  $1 > h5dump.txt`
# If the string "COMPRESSION DEFLATE" is found in the h5dump.txt
# The file is flagged as Compressed otherwise Uncompressed
if grep -q "COMPRESSION DEFLATE" h5dump.txt;
then 
    echo COMPRESSION=\"Compressed\" > CompressionCheck.txt
else
    echo COMPRESSION=\"Uncompressed\" > CompressionCheck.txt
fi
# Clean the temporary file 
#rm -f h5dump.txt