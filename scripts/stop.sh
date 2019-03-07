#!/bin/sh
# Stop script of the software "SimpleRedirect"

if ! screen -S SimpleRedirect -X stuff 'exit\r' >/dev/null 2>&1; then
  echo "[SimpleRedirect] Could not invoke exit command, try manually killing the session."
fi
