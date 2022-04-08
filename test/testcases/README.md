# Add Source Tests (Java 7)

This testsuite test classes are compiled in Java 7 which is the version on which MicroEJ code is based. If you have to add a test that needs to be performed on classes compiled in a newer version you must follow this procedure :

### Create a Project to Test

- Create a new project.
- Change the compliance level of the project if needed.
	- First, the IDE must be setup to use a JDK/JRE capable of compiling code in the test project Java version.
		- Go to `Window`>`Preferences`>`Java`>`Installed JREs`.
  		- Add the JRE/JDK with `Add...`>`Standard VM`>`Select Directory`> navigate to and select the root directory of the desired JRE/JDK >`Finish`.
  		- The JRE/JDK can now be selected from the `Installed JREs` tab.
  	
  	- The project compliance level can now be changed.
  		- Right Click on the project.
  		- Go to `Properties` > `Java Compiler`.
  		- Check `Enable project specific settings`.
  		- Select the desired `Compiler compliance level`.

* Add the classes to test.

### Check Copyrights

The test projects must contain the same copyright headers as the Dependency Discoverer.
A simple way to do it is to copy the test project into the Dependency Discoverer project directory and try to commit the newly added files.
Keep a proof or/and a message signaling the copyrights have been checked in the associated Youtrack card.

### Build JAR File

Go to :

`File` > `Export...` > `Java` > `JAR file`

Select your project and then :
- uncheck `[ ] .classpath`
- uncheck `[ ].project`
- check `[x] Export generated class files and resources`
- check `[x] Export Java source files and resources`

### Add the Test

**Add Test JAR**

copy the previously generated jar under the directory : `test\testpaths\jar-classpath\test-java<version used for compilation>-<test name>` .

**Add Test Class**

In the `com.microej.tool.dependencydiscoverer` package of the DependencyDiscoverer-testcases project, create a new test class with the name `Test<test name>.jar`. The associated test jars directory path can be retrive by using : `System.getProperty("user.dir")+/../testpaths/jar-classpath/test-java<version used for compilation>-<test name>`.

----
Copyright 2022 MicroEJ Corp. All rights reserved.  
Use of this source code is governed by a BSD-style license that can be found with this software.