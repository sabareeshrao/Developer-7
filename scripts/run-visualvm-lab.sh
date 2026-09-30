#!/usr/bin/env bash
set -euo pipefail

mvn -q -DskipTests package

exec java \
  -Xms128m \
  -Xmx256m \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=target/geoops-oom.hprof \
  -jar target/geoops-0.0.1-SNAPSHOT.jar
