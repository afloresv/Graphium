#!/bin/bash
source lib/lib.sh

case $1 in
  "Create")
  java $FLAGS -classpath $LIBS ve.usb.ldc.graphium.load.LoadGraph $2 $3 $4;;
  "Berlin")
  java $FLAGS -classpath $LIBS ve.usb.ldc.graphium.berlin.$2 $3 $4 $5;;
esac
