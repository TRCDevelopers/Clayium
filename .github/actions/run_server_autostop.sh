#!/bin/bash

stdbuf -oL bash -c "./gradlew runServer" | while read line; do
  echo $line
  if [ "$(echo $line | grep -c "Done")" -gt 0 ]; then
    echo "Server has started"
    pkill -P $$
    exit 0
  fi
done
