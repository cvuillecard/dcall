@echo off

set PWD=%~dp0
set JAVA_BIN=%JAVA_HOME%\bin\javaw.exe

echo _____________________________________________________________
echo Working directory : %PWD%
echo Using : %JAVA_BIN%
echo *************************************************************
echo * Starting DCall in silent mode (detashed from current tty) *
echo *      > Trying to connect to [ %* ]                        *
echo *************************************************************
echo <usage> : dcall.bat -host <host_address> -port <host_port> -peers <peer1_host_address:peer1_host_port> <peer2_host_address:peer2_host_port> <etc..>";
echo _____________________________________________________________

"%JAVA_BIN%" -jar ${project.artifactId}-${project.version}.${project.packaging} %*
