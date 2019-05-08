#!/bin/bash

#  -cp /root/dists/workspace-gateway.jar /root/dists/workspace-common.jar \

set -x

if [[ "$1" == "provider-small" ]]; then
  echo "Starting small provider service..."
  java -jar \
        -Xms1G \
        -Xmx1G \
        -Dsalt=$2 \
        -Dquota=small \
        -Dlogs.dir=/root/runtime/logs \
        /root/dists/service-provider.jar
elif [[ "$1" == "provider-medium" ]]; then
  echo "Starting medium provider service..."
  java -jar \
        -Xms2G \
        -Xmx2G \
        -Dsalt=$2 \
        -Dquota=medium \
        -Dlogs.dir=/root/runtime/logs \
        /root/dists/service-provider.jar
elif [[ "$1" == "provider-large" ]]; then
  echo "Starting large provider service..."
  java -jar \
        -Xms3G \
        -Xmx3G \
        -Dsalt=$2 \
        -Dquota=large \
        -Dlogs.dir=/root/runtime/logs \
        /root/dists/service-provider.jar
else
  echo "[ERROR] Unrecognized arguments, exit."
  exit 1
fi

