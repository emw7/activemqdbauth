#!/usr/bin/env bash

#./build.sh

docker run --network=host --name activemqdbauth --rm -p 8161:8161 -p 61616:61616 activemqdbauth_051803

