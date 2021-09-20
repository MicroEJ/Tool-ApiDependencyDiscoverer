..
    Copyright 2021 MicroEJ Corp. All rights reserved.
    This library is provided in source code for use, modification and test, subject to license terms.
    Any modification of the source code will break MicroEJ Corp. warranties on the whole library.

Overview
========

This document will help you to setup a vagrant VM ready to compile executable jar files to Windows executable files.

varSetup.bat
~~~~~~~~~~~~

Declares every variable needed by the two scripts `setup.bat` and `build.bat`.

setup.bat
~~~~~~~~~

Installs every components needed to build a jar file into an exe file through GraalVM. The java 8 version of GraalVM is used, be aware that any build of a jar file compiled for java 8+ may not work.

build.bat
~~~~~~~~~

Builds a jar file to an executable Windows. Will start by calling the native-image-agent to generate files that will allow the program to handle reflexions, JNI, resource, and proxy configuration. By default the agent is lauched only once with --help option but it is possible to launch the agent multiple times with different options/parameters.

Usage
=====

This usage example is done with the `gusztavvargadr/windows-10 <https://app.vagrantup.com/gusztavvargadr/boxes/windows-10>`_ image loaded with Vagrant and VirtualBox.

VM Setup
~~~~~~~~

Create a directory that will contain the vagrant config file and, by default, the shared content.

On linux create the virtual machine with the following command :

>>> vagrant init gusztavvargadr/windows-10
>>> vagrant up

Setup
~~~~~

Copy the files `varSetup.bat` , `setup.bat` and `build.bat` , located at the root of the project https://github.com/MicroEJ/Tool-ApiDependencyDiscoverer into the previously created folder.

SSH into the virtual machine :

>>> vagrant ssh

Go to the shared folder on the virtual machine :

>>> cd c:\vagrant\<name of the shared folder>

Execute the script :

>>> setup.bat

Build
~~~~~

On the host machine place the jar file you want to build into an executable Windows in the shared folder. Open the build.bat file and edit the following lines to your needs :

    java -agentlib:native-image-agent=config-merge-dir=META-INF/native-image -jar microejdd.jar --help

    native-image.cmd --verbose --static --no-fallback -H:+ReportExceptionStackTraces -H:Log=registerResource -jar microejdd.jar

For a basic application use :

    java -agentlib:native-image-agent=config-merge-dir=META-INF/native-image -jar <name_of_your_jar>.jar --help

    native-image.cmd --verbose --static --no-fallback -H:+ReportExceptionStackTraces -jar <name_of_your_jar>.jar

Return to the shared folder in the virtual machine terminal and execute the build script :

>>> build.bat

The resulting exe file is now in the shared folder with as <name_of_your_jar>.exe

