#!/bin/sh

if [ $# -ne 1 ]
    then
    echo "<usage> : ./dcall.sh <peer_host_address>";
    exit 1;
fi

echo "Starting DCall in silent mode (detashed from current tty)"
echo "       > Trying to connect to $1.."

java -jar ${project.artifactId}-${project.version}.${project.packaging} $1 < /dev/null &> /dev/null &
exit 0;