#!/bin/bash
# -cp /root/dists/workspace-gateway.jar:/root/dists/workspace-common.jar \

set -x

echo "Starting gateway ..."
java -jar \
     -Xms4G \
     -Xmx4G \
     -Dsalt=$2 \
     -Dlogs.dir=/root/runtime/logs \
     /root/dists/service-consumer.jar

