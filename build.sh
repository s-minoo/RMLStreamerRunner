#!/bin/bash

if test -f "./RMLInjector.jar"; then
    exit 0
fi

./gradlew shadowJar
mv app/build/libs/RMLInjector-0.1.0-all.jar RMLInjector.jar
mkdir build
cd build

git clone git@github.com:RMLio/RMLStreamer.git 
cd RMLStreamer

mvn install -DskipTests
mv target/RMLStreamer-*.jar ../../RMLStreamer.jar
