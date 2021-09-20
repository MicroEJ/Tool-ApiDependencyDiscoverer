REM shell
REM
REM Copyright 2021 MicroEJ Corp. All rights reserved.
REM This library is provided in source code for use, modification and test, subject to license terms.
REM Any modification of the source code will break MicroEJ Corp. warranties on the whole library.

REM Sets variables related to graalVM and Java version, may be edited to test more recent java version build (like 11)
REM Needs to be called for every jobs because theirs variables are independant.

SET "graalvm=21.2.0"
SET "java=8"
SET "arch=amd64"
SET "graalVMName=graalvm-ce-java%java%-%graalvm%"
SET "graalVMFullName=graalvm-ce-java%java%-windows-%arch%-%graalvm%"
SET "graalURL=https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-%graalvm%/%graalVMFullName%.zip"
SET "graalInstallDir=C:\Programmes\Java\"

REM PATH

setx /M PATH "%graalInstallDir%%graalVMName%\bin\;%PATH%"

REM JAVA_HOME 

setx /M JAVA_HOME "%graalInstallDir%%graalVMName%"

call RefreshEnv.cmd