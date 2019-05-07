#!/bin/bash

set -x

if [[ "$1" == "consumer" ]]; then
  echo "Starting gateway ..."
  nohup java -jar \
        -Xms4G \
        -Xmx4G \
        -Dsalt=$2 \
        -Dlogs.dir=/root/logs \
        -cp=/root/dists/workspace-gateway.jar \
        /root/dists/service-consumer.jar $2 \
        > /dev/null 2>&1 &
elif [[ "$1" == "provider-small" ]]; then
  echo "Starting small provider service..."
  nohup java -jar \
        -Xms1G \
        -Xmx1G \
        -Dsalt=$2 \
        -Dquota=small \
        -Dlogs.dir=/root/logs \
        -cp=/root/dists/workspace-provider.jar \
        /root/dists/service-provider.jar \
        > /dev/null 2>&1 &
elif [[ "$1" == "provider-medium" ]]; then
  echo "Starting medium provider service..."
  nohup java -jar \
        -Xms2G \
        -Xmx2G \
        -Dsalt=$2 \
        -Dquota=medium \
        -Dlogs.dir=/root/logs \
        -cp=/root/dists/workspace-provider.jar \
        /root/dists/service-provider.jar \
        > /dev/null 2>&1 &
elif [[ "$1" == "provider-large" ]]; then
  echo "Starting large provider service..."
  nohup java -jar \
        -Xms3G \
        -Xmx3G \
        -Dsalt=$2 \
        -Dquota=large \
        -Dlogs.dir=/root/logs \
        -cp=/root/dists/workspace-provider.jar \
        /root/dists/service-provider.jar \
        > /dev/null 2>&1 &
else
  echo "[ERROR] Unrecognized arguments, exit."
  exit 1
fi

