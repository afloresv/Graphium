#!/bin/bash
source lib/lib.sh

mkdir -p log
echo "$3 $1 $2" > log/$3-$1-$2.log
touch log/$3-$1-$2.time

for i in {0..19}
do
	java $FLAGS -classpath $LIBS ve.usb.ldc.graphium.berlin.$1 $i $3 Berlin$2 > log/$3-$1-$2-$i.log 2>> log/$3-$1-$2.time
	echo "-------------------------" >> log/$3-$1-$2.log
	sort log/$3-$1-$2-$i.log >> log/$3-$1-$2.log
	rm log/$3-$1-$2-$i.log
	sleep 2
done
