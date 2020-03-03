#!/bin/sh

echo "Starting DCall in silent mode (detashed from current tty)"
java -jar ${project.artifactId}-${project.version}.${project.packaging} < /dev/null &> /dev/null &
