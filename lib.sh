#!/usr/bin/env bash

set -eEu

set +u
export EMW7_PLUGIN_FULL_LIB_DIR=${PLUGIN_FULL_LIB_DIR:-target/dependency}

export EMW7_BUILD_DIR=${BUILD_DIR:-./build}
export EMW7_PLUGIN_ADDITIONAL_LIB_DIR=${PLUGIN_ADDITIONAL_LIB_DIR:-$EMW7_BUILD_DIR/lib}
export EMW7_ACTIVEMQ_LIB_DIR=${ACTIVEMQ_LIB_DIR:-/opt/apache-activemq/lib}
set -u

function printEmw7Env ()
{
    echo "=== CONFIGURATION"
    printenv | grep -oP "^EMW7_\K.*" 
    echo "==="
}

printEmw7Env

mkdir -p $EMW7_PLUGIN_ADDITIONAL_LIB_DIR

for f in $EMW7_PLUGIN_FULL_LIB_DIR/* ; do
  lib_bn=${f#$EMW7_PLUGIN_FULL_LIB_DIR/}
  lib_bn=${lib_bn%-*.jar}
  if ! compgen -G "$EMW7_ACTIVEMQ_LIB_DIR/$lib_bn-*.jar" > /dev/null ; then
    echo "selected $lib_bn to be installed"
    cp $f $EMW7_PLUGIN_ADDITIONAL_LIB_DIR/
  else
    echo "$lib_bn *NOT* to be installed"
  fi
done
