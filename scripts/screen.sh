#!/bin/sh
# Boot script of the software "SimpleRedirect"

# Install screen if not already
if ! dpkg -s screen >/dev/null 2>&1; then
  apt install screen -y
fi

# Attach or start screen session
if ! screen -rx SimpleRedirect >/dev/null 2>&1; then
  chmod +x bin/start.sh
  screen -S SimpleRedirect sh bin/start.sh
fi
