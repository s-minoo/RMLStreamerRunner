#!/bin/bash

if test -f "./RMLInjector.jar"; then
    exit 0
fi

mkdir build
cd build

git clone git@github.com:RMLio/RMLStreamer.git 
cd RMLStreamer

mvn install -DskipTests
mv target/RMLStreamer-2.4.0.jar ../../RMLInjector.jar


