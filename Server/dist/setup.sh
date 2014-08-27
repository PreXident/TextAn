#!/bin/bash

CLASSPATH="TextAn-server.jar"
SETUPER_CLASS_NAME="cz.cuni.mff.ufal.textan.server.setup.Setuper"
PARAMS="$*"

java -cp $CLASSPATH $SETUPER_CLASS_NAME $PARAMS