rem shell
rem
rem Copyright 2021-2022 MicroEJ Corp. All rights reserved.
rem Use of this source code is governed by a BSD-style license that can be found with this software.

call varSetup.bat

echo "graal build started"
cd target~
cd executables
mkdir META-INF\native-image

"%graalInstallDir%%graalVMName%\bin\java" -agentlib:native-image-agent=config-merge-dir=META-INF/native-image -jar microejdd.jar --help

"%graalInstallDir%%graalVMName%\bin\java" -agentlib:native-image-agent=config-merge-dir=META-INF/native-image -jar microejdd.jar --version

"%graalInstallDir%%graalVMName%\bin\java" -agentlib:native-image-agent=config-merge-dir=META-INF/native-image -jar microejdd.jar --repository-url=none

type .\META-INF\native-image\resource-config.json

call "C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\VC\Auxiliary\Build\vcvars64.bat"

"%graalInstallDir%%graalVMName%\bin\native-image.cmd" --verbose --initialize-at-build-time --enable-url-protocols=http,https --static --no-fallback -H:+ReportExceptionStackTraces -jar microejdd.jar

echo "graal build ended"
