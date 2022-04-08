REM shell
REM
REM Copyright 2021-2022 MicroEJ Corp. All rights reserved.
REM Use of this source code is governed by a BSD-style license that can be found with this software.


REM ==============
REM Variable setup
REM ==============

call varSetup.bat

REM ============
REM pre-installs
REM ============

REM choco is needed for this script to work, add the install here if your Windows image doesn't ship it.

REM ====================
REM installs for GraalVM
REM ====================

REM visual studio tools
REM -------------------

choco install visualstudio2017community --version 15.9.17.0 --no-progress --package-parameters "--add Microsoft.VisualStudio.Component.VC.Tools.ARM64 --add Microsoft.VisualStudio.Component.VC.CMake.Project" --yes


REM graalvm
REM -------
		   
REM -- mandatory --

if not exist %graalInstallDir%NUL mkdir %graalInstallDir%

if not exist %graalVMFullName%.zip curl -# -L -O %graalURL%

REM powershell Expand-Archive graalvm-ce-java8-windows-amd64-21.2.0.zip -DestinationPath %graalInstallDir%

if not exist  %graalInstallDir%%graalVMName% powershell Expand-Archive %graalVMFullName%.zip -DestinationPath %graalInstallDir%

REM native-image
REM ------------

"%graalInstallDir%%graalVMName%\bin\gu.cmd" install native-image

