#!/bin/sh

if [ $# -ne 1 ]
    then
    echo "<usage> : ./dcall.sh <peer_host_address>";
    echo "<OR> sh dcall.sh <peer_host_address>";
    echo "";
    echo "(Don't use bash to start the script otherwise you won't see any output stacktrace)"
    exit 1;
fi

echo "Starting DCall in silent mode (detashed from current tty)"
echo "       > Trying to connect to $1.."

java -jar ${project.artifactId}-${project.version}.${project.packaging} $1 < /dev/null &> /dev/null &
exit 0;