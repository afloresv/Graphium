#!/bin/bash
LIBS=`ls lib/*.jar | paste -s -d ":" -`
FLAGS="-Xms2g -Xmx2g -XX:-UseGCOverheadLimit "
