#!/bin/sh
# Start script of the software "SimpleRedirect"

# Validate JRE installation
JAVA_VER=$(java -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*"/\1\2/p;')
if ! [ "$JAVA_VER" -ge 18 ]; then
  echo "[SimpleRedirect] Could not find JRE 1.8 or later, please install it manually."
  echo "[SimpleRedirect] https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html"
  exit 1
fi

# Load config into local variables
for line in $(cat conf.txt)
do
   set -f; IFS='=' && set -- $line && set +f; unset IFS
   eval "$1=$2"
done

# Start application
java -jar bin/SimpleRedirect.jar $MONGODB $PORT
