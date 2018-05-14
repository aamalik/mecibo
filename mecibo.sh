#!/usr/bin/env bash

#!/bin/bash
SCRIPT='java -jar /srv/cibo/lecibo/target/lecibo-0.0.1-SNAPSHOT.jar --server.port=8090 --api.relPath=/lecibo'

PIDFILE=/var/run/TPP.pid
LOGFILE=/var/log/TPP.log
RUNAS=root

start() {
  if [ -f /var/run/$PIDNAME ] && kill -0 $(cat /var/run/$PIDNAME); then
    echo 'Service already running' >&2
    return 1
  fi
  echo 'Starting TPP service...' >&2
  local CMD="$SCRIPT &> \"$LOGFILE\" & echo \$!"
  su -c "$CMD" $RUNAS > "$PIDFILE"
  echo 'Service started' >&2
}

stop() {
  if [ ! -f "$PIDFILE" ] || ! kill -0 $(cat "$PIDFILE"); then
    echo 'Service not running' >&2
    return 1
  fi
  echo 'Stopping service...' >&2
  kill -15 $(cat "$PIDFILE") && rm -f "$PIDFILE"
  echo 'Service stopped' >&2
}

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  restart)
    stop
   start
    ;;
  *)

echo "Usage: $0 {start|stop|restart|uninstall}"
esac
