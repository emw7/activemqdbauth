#!/bin/bash

rm -fr build

docker build --force-rm --target export-stage -o build --progress plain .
