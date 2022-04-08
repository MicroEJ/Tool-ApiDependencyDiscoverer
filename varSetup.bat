REM shell
REM
REM Copyright 2021-2022 MicroEJ Corp. All rights reserved.
REM Use of this source code is governed by a BSD-style license that can be found with this software.

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

set "PATH+GRAALVM=%graalInstallDir%%graalVMName%\bin\"

REM JAVA_HOME 

set "JAVA_HOME=%graalInstallDir%%graalVMName%"