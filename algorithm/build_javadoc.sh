#!/bin/bash

echo "Building Javadoc"


javadoc -private -d docs/javadoc \
src/gov/nasa/gsfc/drl/h2g/*.java \
src/gov/nasa/gsfc/drl/h2g/util/*.java \
src/gov/nasa/gsfc/drl/h2g/vector/*.java 

echo "Done."

