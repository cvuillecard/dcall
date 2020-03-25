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

"%JAVA_BIN%" -jar ${project.build.finalName}.${project.packaging} %*
