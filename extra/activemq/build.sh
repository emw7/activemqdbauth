#!/bin/bash

( cd ../.. ; ./build.sh )

rm -fr build/
cp -pr ../../build .

docker build --force-rm --progress plain --tag activemqdbauth_051803 .

rm -fr build
