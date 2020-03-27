#!/bin/sh

if [ $# -lt 1 ]
    then
    echo "<usage> : ./dcall.sh -host <host_address> -port <host_port> -peers <peer1_host_address:peer1_host_port> <peer2_host_address:peer2_host_port> <etc..>";
    echo "<OR> sh dcall.sh -host <host_address> -port <host_port> -peers <peer1_host_address:peer1_host_port>";
    echo "";
    echo "(Don't use bash to start the script otherwise you won't see any output stacktrace)"
    exit 1;
fi

echo "Starting DCall in silent mode (detashed from current tty)"
echo "       > Trying to connect to [ $@ ]"

java -jar ${project.artifactId}-${project.version}.${project.packaging} $@ < /dev/null &> /dev/null &
exit 0;