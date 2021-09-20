rem shell
rem
rem Copyright 2021 MicroEJ Corp. All rights reserved.
rem This library is provided in source code for use, modification and test, subject to license terms.
rem Any modification of the source code will break MicroEJ Corp. warranties on the whole library.

call varSetup.bat

echo "graal build started"
cd target~
cd executables
mkdir META-INF\native-image

java -agentlib:native-image-agent=config-merge-dir=META-INF/native-image -jar microejdd.jar --help

java -agentlib:native-image-agent=config-merge-dir=META-INF/native-image -jar microejdd.jar --version

java -agentlib:native-image-agent=config-merge-dir=META-INF/native-image -jar microejdd.jar --repository-url=none

type .\META-INF\native-image\resource-config.json

call "C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\VC\Auxiliary\Build\vcvars64.bat"

native-image.cmd --verbose --initialize-at-build-time --enable-url-protocols=http,https --static --no-fallback -H:+ReportExceptionStackTraces -jar microejdd.jar

echo "graal build ended"
